package com.intuit.tank.perfManager.workLoads;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.IOException;
import java.io.Serializable;
import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.amazonaws.xray.contexts.SegmentContextExecutors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.vm.api.enumerated.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.util.DataFileUtil;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.DataFileRequest;
import com.intuit.tank.vm.agent.messages.StandaloneAgentRequest;
import com.intuit.tank.vm.perfManager.StandaloneAgentTracker;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.JobRequest;
import com.intuit.tank.vm.vmManager.JobVmCalculator;
import com.intuit.tank.vm.vmManager.RegionRequest;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

@Named
@ApplicationScoped
public class JobManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private final HttpClient client = HttpClient.newHttpClient();

    private static final Logger LOG = LogManager.getLogger(JobManager.class);

    private static final int MAX_RETRIES = 60;
    private static final long RETRY_SLEEP = 30 * 1000; // 30 second

    @Inject
    private VMTracker vmTracker;

    @Inject
    private StandaloneAgentTracker standaloneTracker;

    @Inject
    private Instance<WorkLoadFactory> workLoadFactoryInstance;

    private Map<String, JobInfo> jobInfoMapLocalCache = new HashMap<String, JobInfo>();

    private Map<Integer, Integer> dataFileCountMap = new ConcurrentHashMap<Integer, Integer>();

    @Inject
    private TankConfig tankConfig;

    /**
     * @param id
     * @throws Exception
     */
    public synchronized void startJob(int id) {
        IncreasingWorkLoad project = workLoadFactoryInstance.get().getModelRunner(id);
        JobRequest jobRequest = project.getJob();
        if(jobRequest.getIncrementStrategy().equals(IncrementStrategy.standard)){
            jobRequest.getRegions().forEach(r -> LOG.info("Nonlinear - Region " + r.getRegion().name() + " has " + r.getUsers() + "% of users"));
            LOG.info("Nonlinear - Target Ramp Rate: " + jobRequest.getUserIntervalIncrement() + " users per second" + "\n"
                    + "Number of Agents: " + jobRequest.getNumUsersPerAgent() + " for job " + jobRequest.getId());
        }
        jobInfoMapLocalCache.put(Integer.toString(id), new JobInfo(jobRequest));
        if (tankConfig.getStandalone()) {
            JobInstanceDao jobInstanceDao = new JobInstanceDao();
            JobInstance jobInstance = jobInstanceDao.findById(id);
            List<AgentAvailability> agents = standaloneTracker.getAgents(jobRequest.getTotalVirtualUsers());
            if (agents == null) {
                jobInstance.setStatus(JobQueueStatus.Aborted);
                jobInstanceDao.saveOrUpdate(jobInstance);
            } else {
                // start the jobs by sending start commmand
                try {
                    for (AgentAvailability a : agents) {
                        StandaloneAgentRequest standaloneAgentRequest = new StandaloneAgentRequest(
                                Integer.toString(id), a.getInstanceId(), jobRequest.getTotalVirtualUsers());
                        standaloneAgentRequest.setStopBehavior(jobRequest.getStopBehavior());
                        sendRequest(a.getInstanceUrl(), standaloneAgentRequest);
                    }
                } catch (Exception e) {
                    LOG.error("Error starting agents: " + e, e);

                    // TODO: kill any agents that were started
                    jobInstance.setStatus(JobQueueStatus.Aborted);
                    jobInstanceDao.saveOrUpdate(jobInstance);
                }
            }
        } else {
            SegmentContextExecutors.newSegmentContextExecutor().execute(project);
        }
    }

    private void sendRequest(String instanceUrl, StandaloneAgentRequest standaloneAgentRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String requestBody = objectMapper
                    .writeValueAsString(standaloneAgentRequest);
            var request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .uri(URI.create(instanceUrl + AgentCommand.request.getPath()))
                    .header(HTTP.CONTENT_TYPE, "application/xml")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("failed to send standalone start to agent: " + response.toString());
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("Error sending StandaloneAgentRequest : " + e.getMessage(), e);
        }
    }

    public AgentTestStartData registerAgentForJob(AgentData agentData) {
        LOG.info("Received Agent Ready call from " + agentData.getInstanceId() + " with Agent Data: " + agentData);
        AgentTestStartData ret = null;
        JobInfo jobInfo = jobInfoMapLocalCache.get(agentData.getJobId());
        // TODO: figure out controller restarts
        if (jobInfo != null) {
            synchronized (jobInfo) {
                ret = new AgentTestStartData(jobInfo.scripts, jobInfo.getUsers(agentData), jobInfo.jobRequest.getRampTime());
                LOG.info("Nonlinear - registerAgentForJob - updated userMap, current userMap keys: " + jobInfo.userMap.keySet());
                LOG.info("Nonlinear - registerAgentForJob - updated userMap, current userMap values: " + jobInfo.userMap.values());
                if(jobInfo.jobRequest.getIncrementStrategy().equals(IncrementStrategy.increasing)) {
                    ret.setAgentInstanceNum(jobInfo.agentData.size());
                } else {
                    ret.setAgentInstanceNum(jobInfo.agentData.size() + 1); // non-linear: agent instance number is 1-based
                }
                LOG.info("Nonlinear - registerAgentForJob - Setting agent order to " + ret.getAgentInstanceNum() + " for instance " + agentData.getInstanceId());
                ret.setDataFiles(getDataFileRequests(jobInfo));
                ret.setJobId(agentData.getJobId());
                ret.setSimulationTime(jobInfo.jobRequest.getSimulationTime());
                ret.setStartUsers(jobInfo.jobRequest.getBaselineVirtualUsers());
                LOG.info("Nonlinear - registerAgentForJob - Setting total agents in AgentTestStartData to " + jobInfo.numberOfMachines);
                ret.setTotalAgents(jobInfo.numberOfMachines);
                ret.setIncrementStrategy(jobInfo.jobRequest.getIncrementStrategy());
                ret.setUserIntervalIncrement(jobInfo.jobRequest.getUserIntervalIncrement()); // non-linear: target ramp rate
                jobInfo.agentData.add(agentData);
                CloudVmStatus status = vmTracker.getStatus(agentData.getInstanceId());
                if(status != null) {
                    status.setVmStatus(VMStatus.pending);
                    vmTracker.setStatus(status);
                }
                if (jobInfo.isFilled()) {
                    new Thread( () -> { startTest(jobInfo); }).start();
                }
            }
        }
        return ret;
    }

    private void startTest(final JobInfo info) {
        String jobId = info.jobRequest.getId();
        LOG.info("Sending start commands for job " + jobId + " asynchronously to following agents: " +
                info.agentData.stream().collect(Collectors.toMap(AgentData::getInstanceId, AgentData::getInstanceUrl)));
        LOG.info("Sleeping for 30 seconds before starting test, to give time for last agent to process AgentTestStartData.");
        try {
            Thread.sleep(RETRY_SLEEP);// 30 seconds
        } catch (InterruptedException ignored) { }
        info.agentData.parallelStream()
                .map(agentData -> agentData.getInstanceUrl() + AgentCommand.start.getPath())
                .map(URI::create)
                .map(uri -> sendCommand(uri, MAX_RETRIES))
                .filter(Objects::nonNull)
                .forEach(future -> {
                    HttpResponse response = (HttpResponse) future.join();
                    if (response.statusCode() == HttpStatus.SC_OK) {
                        LOG.info("Start Command to " + response.uri() + " was SUCCESSFUL for job " + jobId);
                    } else {
                        LOG.error("Start Command to " + response.uri() + " returned statusCode " + response.statusCode() + " for job " + jobId);
                    }
                });
    }

    private CloudVmStatus createFailureStatus(AgentData data) {
        return new CloudVmStatus(data.getInstanceId(), data.getJobId(), null, JobStatus.Unknown, VMImageType.AGENT,
                data.getRegion(), VMStatus.stopped, new ValidationStatus(), data.getUsers(), 0, null, null);
    }

    /**
     * Convert the List of instanceIds to instanceUrls to instanceUris and pass it to sendCommand(List<URI>, retry) to send asynchttp commands.
     * @param instanceIds
     * @param cmd
     * @return Array of CompletableFuture that will probably never be looked at.
     */
    public List<CompletableFuture<?>> sendCommand(List<String> instanceIds, AgentCommand cmd) {
        List<String> instanceUrls = getInstanceUrl(instanceIds);
        if (!instanceUrls.isEmpty()) {
            return instanceUrls.parallelStream()
                    .map(instanceUrl -> instanceUrl + cmd.getPath())
                    .map(URI::create)
                    .map(uri -> sendCommand(uri, 0))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Search the local jobInfo Map Cache to map the instanceId to the instanceUrl.  If not found search AWS with findAgent call
     * @param instanceIds
     * @return List of InstanceUrls
     */
    protected List<String> getInstanceUrl(List<String> instanceIds) {
        return instanceIds.stream()
                .filter(StringUtils::isNotEmpty)
                .map(instanceId -> jobInfoMapLocalCache.values().stream()
                        .flatMap(info -> info.agentData.stream())
                        .filter(data -> instanceId.equals(data.getInstanceId()))
                        .findFirst()
                        .orElse(findAgent(instanceId)))
                .filter(Objects::nonNull)
                .map(AgentData::getInstanceUrl)
                .collect(Collectors.toList());
    }

    /**
     * If the controller has been rebooted, and jobInfoMapLocalCache doesn't have a registered agent, then search for the instance in AWS
     * @param instanceId
     * @return AgentData
     */
    private AgentData findAgent(String instanceId) {
        for (VMRegion region : tankConfig.getVmManagerConfig().getConfiguredRegions()) {
            AmazonInstance amazonInstance = new AmazonInstance(region);
            String instanceUrl = amazonInstance.findDNSName(instanceId);
            if (StringUtils.isNotEmpty(instanceUrl)) {
                instanceUrl = "http://" + instanceUrl + ":" + tankConfig.getAgentConfig().getAgentPort();
                return new AgentData("0", instanceId, instanceUrl, 0, region, "zone");
            }
        }
        return null;
    }

    /**
     * Send Async http commands
     * @param uri URL endpoint of agent, including command path
     * @param retry count attempts
     * @return CompletableFuture Array
     */
    private CompletableFuture<?> sendCommand(final URI uri, final int retry) {
        HttpRequest request = HttpRequest.newBuilder(uri).build();
        LOG.info("Sending command to url " + uri);
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenCompleteAsync((response, t) -> {
                    if (t != null) {
                        LOG.error("Error sending command to url " + request.uri() + ": " + t.getMessage(), t);
                        if (retry > 0) {
                            try {
                                Thread.sleep(RETRY_SLEEP);// 30 seconds
                            } catch (InterruptedException ignored) {}
                            sendCommand(uri, (retry - 1));
                        }
                    }
                }).exceptionally( ex -> {
                    if (ex.getCause().getCause() instanceof NoRouteToHostException) {
                        LOG.error("No Route to Host: " + request.uri() + ", validate host connectivity");
                    } else {
                        LOG.error("Exception sending command to url " + request.uri() + ": " + ex.getMessage(), ex);
                    }
                    return null;
                });
    }

    private DataFileRequest[] getDataFileRequests(JobInfo info) {
        List<DataFileRequest> ret = new ArrayList<DataFileRequest>();
        DataFileDao dataFileDao = new DataFileDao();
        boolean setAsDefault = info.jobRequest.getDataFileIds().size() == 1;
        for (Integer id : info.jobRequest.getDataFileIds()) {
            int version = 0;
            DataFile dataFile = dataFileDao.findById(id);
            if (dataFile != null) {
                int numLinesPerAgent = (int) Math.floor(getNumberOfLines(id) / info.numberOfMachines);
                int offset = info.agentData.size() * numLinesPerAgent;
                DataFileRequest dataRequest = new DataFileRequest(dataFile.getPath(), setAsDefault,
                        DataFileUtil.getDataFileServiceUrl(dataFile.getId(), offset, numLinesPerAgent));
                ret.add(dataRequest);
            }
        }
        return ret.toArray(new DataFileRequest[0]);
    }

    private int getNumberOfLines(Integer dataFileId) {
        Integer ret = dataFileCountMap.get(dataFileId);
        if (ret == null) {
            synchronized (dataFileId) {
                ret = 0;
                DataFileDao dfd = new DataFileDao();
                DataFile dataFile = dfd.findById(dataFileId);
                if (dataFile != null) {
                    ret = DataFileUtil.getNumLines(dataFile);
                }
                dataFileCountMap.put(dataFileId, ret);
            }
        }
        return ret;
    }

    private static class JobInfo {
        public String scripts;
        private JobRequest jobRequest;
        private Set<AgentData> agentData = new HashSet<AgentData>();
        private Map<RegionRequest, Integer> userMap = new HashMap<RegionRequest, Integer>();
        private int numberOfMachines;

        public JobInfo(JobRequest jobRequest) {
            super();
            this.jobRequest = jobRequest;
            initializeUserMap(jobRequest);
            scripts = jobRequest.getScriptsXmlUrl();
        }

        public boolean isFilled() {
            for (Integer i : userMap.values()) {
                if (i != 0) {
                    return false;
                }
            }
            return true;
        }

        public int getUsers(AgentData agent) {
            int ret = 0;
            VMRegion region = agent.getRegion();
            if(jobRequest.getIncrementStrategy().equals(IncrementStrategy.increasing)) {
                for (RegionRequest r : jobRequest.getRegions()) {
                    if (Integer.parseInt(r.getUsers()) > 0 && region == r.getRegion()) {
                        int numUsersRemaining = userMap.get(r);
                        ret = Math.min(agent.getCapacity(), numUsersRemaining);
                        userMap.put(r, numUsersRemaining - ret);
                        break;
                    }
                }
            } else {
                for (RegionRequest r : jobRequest.getRegions()) {
                    if (Integer.parseInt(r.getPercentage()) > 0 && region == r.getRegion()) {
                        int numAgentsRemaining = userMap.get(r);
                        userMap.put(r, numAgentsRemaining - 1);
                        break;
                    }
                }
            }
            return ret;
        }

        private void initializeUserMap(JobRequest request) {
            if(jobRequest.getIncrementStrategy().equals(IncrementStrategy.increasing)) {
                for (RegionRequest r : request.getRegions()) {
                    int numUsers = NumberUtils.toInt(r.getUsers());
                    if (numUsers > 0) {
                        userMap.put(r, numUsers);
                        numberOfMachines += JobVmCalculator.getMachinesForAgent(numUsers, request.getNumUsersPerAgent());
                    }
                }
            } else {
                Map<RegionRequest, Integer> regionAllocation = JobVmCalculator.getMachinesForAgentByUserPercentage(request.getNumUsersPerAgent(), request.getRegions());
                for (RegionRequest r : request.getRegions()) {
                    int numAgents = regionAllocation.get(r);
                    if (numAgents > 0) {
                        userMap.put(r, numAgents);
                        numberOfMachines += numAgents;
                        LOG.info("Nonlinear - initializeUserMap - Region " + r.getRegion().name() + " has " + numAgents + " agents");
                        LOG.info("Nonlinear - initializeUserMap - Region " + r.getRegion().name() + " has " + r.getUsers() + "% of users");
                        LOG.info("Nonlinear - initializeUserMap - current userMap keys: " + userMap.keySet());
                        LOG.info("Nonlinear - initializeUserMap - current userMap values: " + userMap.values());
                    }
                }
            }
        }
    }
}
