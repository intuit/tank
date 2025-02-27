package com.intuit.tank.vmManager.environment.amazon;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.logging.ControllerLoggingConfig;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.InstanceDescription;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmInstanceType;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMKillRequest;
import com.intuit.tank.vm.vmManager.VMRequest;
import com.intuit.tank.vmManager.environment.IEnvironmentInstance;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2AsyncClientBuilder;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import jakarta.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class AmazonInstance implements IEnvironmentInstance {

    protected static final long ASSOCIATE_IP_MAX_WAIT_MILIS = 1000 * 60 * 2;// 2 minutes
    private static final Logger LOG = LogManager.getLogger(AmazonInstance.class);

    private Ec2AsyncClient ec2AsyncClient;
    private VMRegion vmRegion;

    private final TankConfig config = new TankConfig();

    /**
     *
     * @param vmRegion
     */
    public AmazonInstance(@Nonnull Ec2AsyncClient ec2AsyncClient, @Nonnull VMRegion vmRegion) {
        this.ec2AsyncClient = ec2AsyncClient;
        this.vmRegion = vmRegion;
    }

    /**
     *
     * @param vmRegion
     */
    public AmazonInstance(@Nonnull VMRegion vmRegion) {
        this.vmRegion = vmRegion;
        try {
            CloudCredentials creds = new TankConfig().getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
            Ec2AsyncClientBuilder ec2ClientBuilder = Ec2AsyncClient.builder();
            if (creds != null && StringUtils.isNotBlank(creds.getProxyHost())) {
                try {
                    ProxyConfiguration.Builder proxyConfig = ProxyConfiguration.builder().host(creds.getProxyHost());
                    if (StringUtils.isNotBlank(creds.getProxyPort())) {
                        proxyConfig.port(Integer.parseInt(creds.getProxyPort()));
                    }
                    SdkAsyncHttpClient.Builder<NettyNioAsyncHttpClient.Builder> httpClientBuilder =
                            NettyNioAsyncHttpClient.builder().proxyConfiguration(proxyConfig.build());
                    ec2ClientBuilder.httpClientBuilder(httpClientBuilder);
                } catch (NumberFormatException e) {
                    LOG.error("invalid proxy setup.");
                }
            }
            if (creds != null && StringUtils.isNotBlank(creds.getKey()) && StringUtils.isNotBlank(creds.getKeyId())) {
                AwsCredentials credentials = AwsBasicCredentials.create(creds.getKeyId(), creds.getKey());
                ec2ClientBuilder.credentialsProvider(StaticCredentialsProvider.create(credentials));
            }
            ec2AsyncClient = ec2ClientBuilder.region(Region.of(vmRegion.getRegion())).build();
        } catch (Exception ex) {
            LOG.error("Error initializing amazon client: {}", ex, ex);
            throw new RuntimeException(ex);
        }
    }

    public void attachVolume(String volumneId, String instanceId, String device) {
        AttachVolumeRequest attachVolumeRequest =
                AttachVolumeRequest.builder().volumeId(volumneId).instanceId(instanceId).device(device).build();
        ec2AsyncClient.attachVolume(attachVolumeRequest);
        RebootInstancesRequest rebootInstancesRequest =
                RebootInstancesRequest.builder().instanceIds(instanceId).build();
        ec2AsyncClient.rebootInstances(rebootInstancesRequest);
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public List<VMInformation> describeInstances(List<String> instanceIds) {
        try {
            return ec2AsyncClient.describeInstances().get().reservations().stream()
                    .flatMap(reservationDescription -> reservationDescription.instances()
                            .stream()
                            .filter(instance -> instanceIds.contains(instance.instanceId()))
                            .map(instance -> AmazonDataConverter.instanceToVmInformation(reservationDescription.requesterId(), instance, vmRegion)))
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException error) {
            LOG.error("Error getting instances: {}", error.toString(), error);
        }
        return Collections.emptyList();
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public List<VMInformation> create(VMRequest request) {
        Subsegment subsegment = AWSXRay.beginSubsegment("Request.Agents." + vmRegion.getRegion());
        List<VMInformation> result = new ArrayList<>();
        try {
            ControllerLoggingConfig.setupThreadContext();
            VMInstanceRequest instanceRequest = (VMInstanceRequest) request;
            subsegment.putMetadata("count", instanceRequest.getNumberOfInstances());
            subsegment.putMetadata("instanceType", instanceRequest.getSize());
            InstanceDescription instanceDescription = (instanceRequest.getInstanceDescription() != null)
                    ? instanceRequest.getInstanceDescription()
                    : config.getVmManagerConfig().getInstanceForRegionAndType(vmRegion, instanceRequest.getImage());

            // Get the required data
            if (instanceRequest.getReuseStoppedInstance()) {
                List<VMInformation> instances = findAllInstancesOfType(this.vmRegion, instanceRequest.getImage());
                LOG.debug("looking for stopped instance with ami-id of {}", instanceRequest.getImage());
                for (VMInformation vmInfo : instances) {
                    LOG.debug("found instance with id {} with state of {}", vmInfo.getInstanceId(), vmInfo.getState());
                    if ("stopped".equalsIgnoreCase(vmInfo.getState())) {
                        StartInstancesRequest startInstancesRequest = StartInstancesRequest.builder().instanceIds(vmInfo.getInstanceId()).build();
                        // restart this instance
                        StartInstancesResponse startInstances = ec2AsyncClient.startInstances(startInstancesRequest).get();
                        result.addAll(AmazonDataConverter.processStateChange(startInstances.startingInstances()));
                        break;
                    }
                }
            }
            if (result.isEmpty()) {
                VmInstanceType vmType = config.getVmManagerConfig().getInstanceType(instanceRequest.getSize());
                String keyPair = instanceDescription.getKeypair();
                if (instanceRequest.getJobId() != null) {
                    instanceRequest.addUserData(TankConstants.KEY_JOB_ID, instanceRequest.getJobId());
                }
                if (instanceRequest.getReportingMode() != null) {
                    LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Setting reporting mode to " + instanceRequest.getReportingMode())));
                    instanceRequest.addUserData(TankConstants.KEY_REPORTING_MODE, instanceRequest.getReportingMode());
                } else {
                    LOG.debug("Reporting mode not set.");
                }
                if (vmType.getJvmArgs() != null) {
                    instanceRequest.addUserData(TankConstants.KEY_JVM_ARGS, vmType.getJvmArgs());
                }
                CloudCredentials cloudCredentials = config.getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
                if (StringUtils.isNotBlank(cloudCredentials.getKeyId()) && StringUtils.isNotBlank(cloudCredentials.getKey())) {
                    instanceRequest.addUserData(TankConstants.KEY_AWS_SECRET_KEY_ID, cloudCredentials.getKeyId());
                    instanceRequest.addUserData(TankConstants.KEY_AWS_SECRET_KEY, cloudCredentials.getKey());
                }

                instanceRequest.addUserData(TankConstants.KEY_CONTROLLER_URL, config.getControllerBase());
                instanceRequest.addUserData(TankConstants.KEY_AGENT_TOKEN, config.getAgentConfig().getAgentToken());
                instanceRequest.addUserData(TankConstants.KEY_NUM_USERS_PER_AGENT, Integer.toString(instanceRequest.getNumUsersPerAgent()));

                if (instanceRequest.isUseEips()) {
                    instanceRequest.addUserData(TankConstants.KEY_USING_BIND_EIP, Boolean.TRUE.toString());
                }
                if (instanceRequest.getLoggingProfile() != null) {
                    LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Setting loggingProfile to " + instanceRequest.getLoggingProfile())));
                    instanceRequest.addUserData(TankConstants.KEY_LOGGING_PROFILE, instanceRequest.getLoggingProfile());
                } else {
                    LOG.debug("Logging  profile not set.");
                }
                if (instanceRequest.getStopBehavior() != null) {
                    LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Setting stopBehavior to " + instanceRequest.getStopBehavior())));
                    instanceRequest.addUserData(TankConstants.KEY_STOP_BEHAVIOR, instanceRequest.getStopBehavior());
                } else {
                    LOG.debug("stop Behavior not set.");
                }

                String userData = buildUserData(instanceRequest.getUserData());

                int totalInstanceRequest = instanceRequest.getNumberOfInstances();
                String image = getAMI(instanceDescription);
                LOG.info(new ObjectMessage(ImmutableMap.of("Message","Requesting " + totalInstanceRequest + " instances in " + vmRegion.getName() + " with AMI=" + image)));

                RunInstancesRequest.Builder runInstancesRequestTemplate = RunInstancesRequest.builder();
                runInstancesRequestTemplate.metadataOptions(
                        InstanceMetadataOptionsRequest.builder().httpTokens(HttpTokensState.REQUIRED).build());
                Tenancy tenancy = StringUtils.isEmpty(instanceDescription.getTenancy()) ? Tenancy.DEFAULT : Tenancy.fromValue(instanceDescription.getTenancy());
                runInstancesRequestTemplate.imageId(image)
                        .keyName(keyPair)
                        .placement(Placement.builder().tenancy(tenancy).build())
                        .monitoring(RunInstancesMonitoringEnabled.builder().build())
                        .userData(userData);

                Collection<String> c = instanceDescription.getSecurityGroupIds();
                if (!c.isEmpty()) {
                    LOG.info(new ObjectMessage(ImmutableMap.of("Message","Security Group IDs " + c.toString())));
                    runInstancesRequestTemplate.securityGroupIds(c);
                } else {
                    runInstancesRequestTemplate.securityGroups(instanceDescription.getSecurityGroup());
                }
                if (StringUtils.isNotBlank(instanceDescription.getIamRole())) {
                    runInstancesRequestTemplate.iamInstanceProfile(
                            IamInstanceProfileSpecification.builder().name(instanceDescription.getIamRole()).build());
                }
                // add zone info if specified
                if (!StringUtils.isEmpty(instanceDescription.getZone())) {
                    runInstancesRequestTemplate.placement(Placement.builder().availabilityZone(instanceDescription.getZone()).build());
                }
                runInstancesRequestTemplate.tagSpecifications(buildTags(instanceRequest));

                // Loop to request agent instances
                List<String> subnetIds = instanceDescription.getSubnetIds();
                Collections.shuffle(subnetIds, new Random());
                int splitRequestBase = totalInstanceRequest / subnetIds.size();
                int remainder = totalInstanceRequest % subnetIds.size();
                List<CompletableFuture<RunInstancesResponse>> futures = new ArrayList<>();
                for (String subnetId : subnetIds) {
                    int requestCount = splitRequestBase + (remainder-- > 0 ? 1 : 0);
                    if (requestCount > 0)
                        futures.add(requestInstances(runInstancesRequestTemplate, subnetId, requestCount, vmType.getTypes()));
                }
                futures.stream().map(CompletableFuture::join).forEach(response -> {
                    result.addAll(AmazonDataConverter.processReservation(response.requesterId(), response.instances(), vmRegion));
                });

                if (instanceRequest.isUseEips()) {
                    Set<Address> availableEips = new HashSet<Address>();
                    synchronized (instanceRequest.getRegion()) {
                        DescribeAddressesResponse describeAddresses =
                                ec2AsyncClient.describeAddresses(DescribeAddressesRequest.builder().build()).get();
                        Set<String> reserved = config.getVmManagerConfig().getReservedElasticIps();
                        for (Address address : describeAddresses.addresses()) {
                            String elasticIPType = instanceDescription.isVPC() ? "vpc" : "standard";
                            if (elasticIPType.equalsIgnoreCase(address.domainAsString()) && StringUtils.isBlank(address.instanceId())) {
                                String ip = address.publicIp();
                                if (!reserved.contains(ip)) {
                                    availableEips.add(address);
                                }
                            }
                        }
                    }
                    List<Address> randomizedIps = new ArrayList<Address>(availableEips);
                    Collections.shuffle(randomizedIps);

                    synchronized (instanceRequest.getRegion()) {
                        Iterator<Address> iter = randomizedIps.iterator();
                        List<AssociateContainer> bindIps = new ArrayList<AssociateContainer>();
                        for (VMInformation info : result) {
                            if (!iter.hasNext()) {
                                break;
                            }
                            bindIps.add(new AssociateContainer(info.getInstanceId(), iter.next()));
                        }
                        CountDownLatch latch = new CountDownLatch(bindIps.size());
                        for (AssociateContainer container : bindIps) {
                            associateAddress(container.instanceId, container.address, latch);
                        }
                        latch.await();
                    }
                }
            }
            if (instanceDescription.getPublicIp() != null && result.size() == 1) {
                // bind to the public ip
                String instanceId = result.get(0).getInstanceId();
                // wait for instance to be in running state
                associateAddress(instanceId, Address.builder().publicIp(instanceDescription.getPublicIp()).build(), null);
                // reboot(result);
            }

        } catch (SdkException ae) {
            LOG.error("Amazon issue starting instances: {} : {}", vmRegion, ae.getMessage(), ae);
            throw new RuntimeException(ae);
        } catch (Exception ex) {
            LOG.error("Error starting instances: {} : {}", vmRegion, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        } finally {
            AWSXRay.endSubsegment();
        }
        return result;
    }

    /**
     * @param runInstancesRequestTemplate
     * @param subnetId
     * @param requestCount
     * @return
     */
    private CompletableFuture<RunInstancesResponse> requestInstances(
            RunInstancesRequest.Builder runInstancesRequestTemplate, String subnetId, int requestCount, List<String> instanceTypes) {
        RunInstancesRequest.Builder runInstancesRequest = runInstancesRequestTemplate.copy();
        if (instanceTypes.isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("No instance types available"));
        }
        String instanceType = instanceTypes.get(0);
        List<String> remainingTypes = instanceTypes.subList(1, instanceTypes.size());
        return ec2AsyncClient.runInstances(
                runInstancesRequest
                        .instanceType(instanceType)
                        .subnetId(subnetId)
                        .minCount(1).maxCount(requestCount).build())
                .exceptionally(ex -> {
                    if (ex instanceof Ec2Exception) { //TODO: Filter on exact capacity exception message
                        LOG.warn("Failure requesting instance type: {} : {} : {}", instanceType, vmRegion,  ex.getMessage());
                        return requestInstances(runInstancesRequestTemplate, subnetId, requestCount, remainingTypes).join();
                    } else {
                        LOG.error("Error requesting instances: {}: {}", vmRegion, ex.getMessage(), ex);
                    }
                    throw new RuntimeException(ex);
                })
                .thenApply(response -> {
                    if (response.instances().size() < requestCount) {
                        RunInstancesResponse res = requestInstances(runInstancesRequestTemplate, subnetId, requestCount - response.instances().size(), remainingTypes).join();
                        return response.toBuilder().instances(res.instances()).build();
                    }
                    return response;
                });
    }

    /**
     * @param instanceRequest
     * @return
     */
    private Collection<TagSpecification> buildTags(VMInstanceRequest instanceRequest) {
        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.builder().key("Name").value(buildNameTag(instanceRequest)).build());
        tags.add(Tag.builder().key("Controller").value(config.getInstanceName()).build());

        if (instanceRequest.getJobId() != null) {
            instanceRequest.addUserData(TankConstants.KEY_JOB_ID, instanceRequest.getJobId());
            instanceRequest.addUserData(TankConstants.KEY_CONTROLLER_URL, config.getControllerBase());
            tags.add(Tag.builder().key("JobId").value(instanceRequest.getJobId()).build());

            if (NumberUtils.isCreatable(instanceRequest.getJobId())) {
                JobInstance jobInstance = new JobInstanceDao().findById(Integer.valueOf(instanceRequest.getJobId()));
                if (jobInstance != null) {
                    tags.add(Tag.builder().key("JobName").value(jobInstance.getName()).build());
                }
            }
        }
        tags.addAll(
                config.getVmManagerConfig().getTags().stream()
                        .map(tag -> Tag.builder().key(tag.getName()).value(tag.getValue()).build())
                        .toList()
                );
        return Collections.singleton(
                TagSpecification.builder()
                        .tags(tags)
                        .resourceType(ResourceType.INSTANCE).build());
    }

    /**
     * @param instanceRequest
     * @return
     */
    private String buildNameTag(VMInstanceRequest instanceRequest) {
        String nameTag = ( StringUtils.isNoneEmpty(instanceRequest.getJobId()) ) ?
                instanceRequest.getImage().getConfigName() + "-job[" + instanceRequest.getJobId() + "]" :
                instanceRequest.getImage().getConfigName();
        return ( StringUtils.isNoneEmpty(config.getInstanceName()) ) ?
                config.getInstanceName() + "-" + nameTag :
                nameTag;
    }

    @Override
    public void kill(VMRequest request) {
        VMKillRequest killRequest = (VMKillRequest) request;
        TerminateInstancesRequest terminateInstancesRequest =
                TerminateInstancesRequest.builder().instanceIds(killRequest.getInstances()).build();
        ec2AsyncClient.terminateInstances(terminateInstancesRequest).whenCompleteAsync((response, exception) -> {
            if (exception != null) {
                throw new RuntimeException("Failed to terminate EC2 instances.", exception);
            } else if (response == null || response.terminatingInstances().isEmpty()) {
                throw new RuntimeException("No EC2 instances found to terminate.");
            } else {
                response.terminatingInstances().forEach(instance -> {
                    LOG.debug("killed instance {}", instance.instanceId());
                });
            }
        });
    }

    @Override
    public void killInstances(List<String> instanceIds) {
        // Filter instanceId list to only instances in the client defined region.
        ec2AsyncClient.describeInstances().whenCompleteAsync((response, exception) -> {
                    if (exception != null) {
                        throw new RuntimeException("Failed to describe running EC2 instances.", exception);
                    } else if (response == null || response.reservations().isEmpty()) {
                        throw new RuntimeException("No running EC2 instances found.");
                    } else {
                        List<String> instanceIdsInRegion = response.reservations().stream()
                                .flatMap(reservation -> reservation.instances().stream())
                                .map(Instance::instanceId)
                                .filter(instanceIds::contains)
                                .collect(Collectors.toList());
                        if (!instanceIdsInRegion.isEmpty()) {
                            ec2AsyncClient.terminateInstances(TerminateInstancesRequest.builder().instanceIds(instanceIdsInRegion).build());
                        }
                    }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VMInformation> findInstancesOfType(VMRegion region, VMImageType type) {
        List<VMInformation> ret = new ArrayList<>();
        try {
            DescribeInstancesResponse response = ec2AsyncClient.describeInstances().get();
            InstanceDescription instanceForRegionAndType = config.getVmManagerConfig().getInstanceForRegionAndType(region, type);
            for (Reservation reservation : response.reservations()) {
                if (reservation.instances() != null) {
                    for (Instance instance : reservation.instances()) {
                        if ((instance.state().nameAsString().equalsIgnoreCase("running")
                                || instance.state().nameAsString().equalsIgnoreCase("pending"))
                                && instance.imageId().equals(instanceForRegionAndType.getAmi())) {
                            ret.add(AmazonDataConverter.instanceToVmInformation(reservation.requesterId(), instance, region));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting instances: {}", e.toString(), e);
            throw new RuntimeException(e);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    private List<VMInformation> findAllInstancesOfType(VMRegion region, VMImageType type) {
        List<VMInformation> ret = new ArrayList<>();
        try {
            DescribeInstancesResponse response = ec2AsyncClient.describeInstances().get();
            InstanceDescription instanceForRegionAndType = config.getVmManagerConfig().getInstanceForRegionAndType(region, type);
            for (Reservation reservation : response.reservations()) {
                if (reservation.instances() != null) {
                    for (Instance instance : reservation.instances()) {
                        if (instance.imageId().equals(instanceForRegionAndType.getAmi())) {
                            ret.add(AmazonDataConverter.instanceToVmInformation(reservation.requesterId(), instance, region));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting instances: {}", e.toString(), e);
            throw new RuntimeException(e);
        }
        return ret;
    }

    /**
     * Request the ami parameter from SSM
     * @param instanceDescription
     * @return The ami assigned
     */
    private String getAMI(InstanceDescription instanceDescription) {
        String name = instanceDescription.getSSMAmi();
        if (StringUtils.isNotEmpty(name)) {
            try (SsmClient ssmClient = SsmClient.builder().build()) {
                GetParameterResponse response = ssmClient.getParameter(GetParameterRequest.builder().name(name).build());
                return response.parameter().value();
            } catch (Exception e) {
                LOG.error("Error retrieving AMI from SSM with name {}, default to InstanceRequest", name, e);
            }
        }
        return instanceDescription.getAmi();
    }

    @Override
    public void associateAddress(final String instanceId, final Address address, final CountDownLatch latch) {

        new Thread( () -> {
            boolean associated = false;

            try {
                long start = System.currentTimeMillis();
                int count = 0;
                LOG.debug("Setting ip for instance {} to {}", instanceId, address.publicIp());
                while ((System.currentTimeMillis() - start) < ASSOCIATE_IP_MAX_WAIT_MILIS && !associated) {
                    count++;
                    try {
                        if (address.allocationId() == null) {
                            ec2AsyncClient.associateAddress(
                                    AssociateAddressRequest.builder().instanceId(instanceId).publicIp(address.publicIp()).build());
                        } else {
                            ec2AsyncClient.associateAddress(
                                    AssociateAddressRequest.builder().instanceId(instanceId).allocationId(address.allocationId()).build());
                        }
                        Thread.sleep((new Random().nextInt(10) + 10) * 100L);
                        DescribeInstancesResponse describeInstances =
                                ec2AsyncClient.describeInstances(
                                        DescribeInstancesRequest.builder().instanceIds(instanceId).build()).get();
                        associated = describeInstances.reservations().stream()
                                .flatMap(reservation -> reservation.instances().stream())
                                .anyMatch(instance -> address.publicIp().equals(instance.publicIpAddress()));
                        if (associated) {
                            LOG.info("{} associated with {}", instanceId, address.publicIp());
                        } else if (count % 5 == 0) {
                            LOG.debug("{} not associated yet {}. Retrying... count = {}", instanceId, address.publicIp(), count);
                        }
                    } catch (Exception e) {
                        if (count < 5) {
                            LOG.warn("Error associating ip address: {} Will retry.", String.valueOf(e));
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Error setting elastic ip: {}", e, e);
            } finally {
                LOG.debug("exiting associated = {}", associated);
                if (latch != null) {
                    latch.countDown();
                }
            }
        }).start();

    }

    @Override
    public void reboot(List<VMInformation> instances) {
        List<String> instanceIds = instances.stream()
                .map(VMInformation::getInstanceId)
                .collect(Collectors.toCollection(() -> new ArrayList<>(instances.size())));
        ec2AsyncClient.rebootInstances(RebootInstancesRequest.builder().instanceIds(instanceIds).build());
    }

    /**
     * @param instanceIds
     */
    public void stopInstances(List<String> instanceIds) {
        // Filter instanceId list to only instances in the client defined region.
        ec2AsyncClient.describeInstances().whenCompleteAsync((response, exception) -> {
            if (exception != null) {
                throw new RuntimeException("Failed to describe running EC2 instances.", exception);
            } else if (response == null || response.reservations().isEmpty()) {
                throw new RuntimeException("No running EC2 instances found.");
            } else {
                List<String> instanceIdsInRegion = response.reservations().stream()
                        .flatMap(reservation -> reservation.instances().stream())
                        .map(Instance::instanceId)
                        .filter(instanceIds::contains)
                        .collect(Collectors.toList());
                if (!instanceIdsInRegion.isEmpty()) {
                    ec2AsyncClient.stopInstances(StopInstancesRequest.builder().instanceIds(instanceIdsInRegion).build());
                }
            }
        });
    }

    /**
     * @param userDataMap
     * @return
     */
    private String buildUserData(@Nonnull Map<String, String> userDataMap) {
        try {
            String sb = new ObjectMapper().writeValueAsString(userDataMap);
            return Base64.getEncoder().encodeToString(sb.getBytes());
        } catch (JsonProcessingException e) {
            LOG.error("Failed to convert userDataMap to Json: {}", e.getMessage());
        }
        return "";
    }

    private static class AssociateContainer {
        private String instanceId;
        private Address address;

        private AssociateContainer(String instanceId, Address address) {
            super();
            this.instanceId = instanceId;
            this.address = address;
        }

    }

    public Optional<String> findDNSName(String instanceId) {
        try {
            Optional<String> dnsName =  ec2AsyncClient.describeInstances().get().reservations().stream()
                    .flatMap(reservationDescription -> reservationDescription.instances().stream())
                    .filter(instance -> instanceId.equals(instance.instanceId()))
                    .findFirst()
                    .map(instance -> (StringUtils.isNotEmpty(instance.publicDnsName()))
                            ? instance.publicDnsName()
                            : instance.privateDnsName());
            if (dnsName.isPresent()) {
                return dnsName;
            }
        } catch (Exception e) {
            LOG.error("Error getting public dns in {}: {}", vmRegion, e.getMessage());
        }
        return Optional.empty();
    }
}
