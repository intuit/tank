package com.intuit.tank.persistence.databases;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.settings.TankConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import jakarta.annotation.Nonnull;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * S3Datasource
 * 
 * @author Kevin McGoldrick
 * 
 */
public class S3Datasource implements IDatabase {
	private static final Logger LOG = LogManager.getLogger(S3Datasource.class);

	private S3AsyncClient s3Client;
	private String hostname = "fail";
    private String metricString = "";
	private String tags = "";
	private String bucketName = "";

	private HierarchicalConfiguration resultsProviderConfig =
			new TankConfig().getVmManagerConfig().getResultsProviderConfig();

	public S3Datasource() {
		CloudCredentials creds = new TankConfig().getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
		if (creds != null && StringUtils.isNotBlank(creds.getKey()) && StringUtils.isNotBlank(creds.getKeyId())) {
			AwsCredentials credentials = AwsBasicCredentials.create(creds.getKeyId(), creds.getKey());
			this.s3Client = S3AsyncClient.builder().credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
		} else {
			this.s3Client = S3AsyncClient.builder().build();
		}
	}

	@Override
	public void initNamespace(String tableName) {}

	@Override
	public void removeNamespace(String tableName) {}

	@Override
	public void deleteForJob(String tableName, String jobId, boolean asynch) {}

	@Override
	public boolean hasTable(String tableName) {
		return true;
	}

	@Override
	public boolean hasJobData(String tableName, String jobId) {
		return true;
	}

	@Override
	public void addTimingResults(String tableName, List<TankResult> results, boolean asynch) {
		LOG.trace("Starting addTimingResults with " + results.size() + " items");
		if (resultsProviderConfig != null) {
			try {
				metricString = resultsProviderConfig.getString("metricString","test.tank.transaction");
				tags = resultsProviderConfig.getString("tags", ""); //Example "bu=ctg app=tto pool=agent service=tank env=prf"
				bucketName = resultsProviderConfig.getString("bucket", "tank-test");
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				LOG.error("Failed to get hostname " + e.toString(), e);
			}
		} else {
			LOG.error("Results Provider Config is Empty. Please update settings.xml to include S3Datasource Configuration");
		}

		String region = new DefaultAwsRegionProviderChain().getRegion().toString();

		if (StringUtils.endsWith(bucketName, "-")) {
			bucketName = bucketName.concat(region);
		}

		StringBuilder sb = new StringBuilder();
		String jobId = results.get(0).getJobId();
		String instance = results.get(0).getInstanceId();
		long timestamp = results.get(results.size()-1).getTimeStamp().getTime() / 1000;
		
		try {
			String tagsComplete = " source=" + hostname +
					" instanceid=" + instance +  
					" location=" + region +
					" " + tags +
					" jobid=" + jobId + 
					System.getProperty("line.separator");

			Map<String, List<Integer>> grouped = results.stream()
					.collect(groupingBy(TankResult::getRequestName,
							Collectors.mapping(TankResult::getResponseTime, toList())));
			LOG.trace("Sorted into " + grouped.size() + " request name groups");

			for (Map.Entry<String,List<Integer>> entry : grouped.entrySet()) {
				String requestName = entry.getKey();
				List<Integer> groupResults = entry.getValue();
				int size = groupResults.size();
				long sum = groupResults.stream().mapToInt(Integer::intValue).sum();
				Collections.sort(groupResults);
				Integer[] sortedList = groupResults.toArray(new Integer[0]);
				long average = sum / size;
				int fiftieth = (size/2);
				if (fiftieth >= 1) fiftieth--;
				int ninetieth = Math.round(size * 0.9f);
				if (ninetieth >= 1) ninetieth--;
				int ninetynineth = Math.round(size * 0.99f);
				if (ninetynineth >= 1) ninetynineth--;
				sb.append(metricString + ".resp_time.min " + sortedList[0] + " " + timestamp + " transaction=" + requestName)
					.append(tagsComplete)
					.append(metricString + ".resp_time.avg " + average + " " + timestamp + " transaction=" + requestName)
					.append(tagsComplete)
					.append(metricString + ".resp_time.max " + sortedList[size - 1] + " " + timestamp + " transaction=" + requestName)
					.append(tagsComplete)
					.append(metricString + ".resp_time.tp_50 " + sortedList[fiftieth] + " " + timestamp + " transaction=" + requestName)
					.append(tagsComplete)
					.append(metricString + ".resp_time.tp_90 " + sortedList[ninetieth] + " " + timestamp + " transaction=" + requestName)
					.append(tagsComplete)
					.append(metricString + ".resp_time.tp_99 " + sortedList[ninetynineth] + " " + timestamp + " transaction=" + requestName)
					.append(tagsComplete)
					.append(metricString + ".rpi " + size + " " + timestamp + " transaction=" + requestName)
					.append(tagsComplete);
			}
			LOG.trace("Sending to S3: " + sb.toString());
			PutObjectRequest.Builder request = PutObjectRequest.builder()
					.bucket(bucketName)
					.key("TANK-AgentData-" + UUID.randomUUID() + ".log")
					.acl(ObjectCannedACL.BUCKET_OWNER_FULL_CONTROL);
			s3Client.putObject(request.build(), AsyncRequestBody.fromString(sb.toString()));
		} catch (SdkClientException ase) {
			LOG.error("SdkClientException: which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason. bucket=" + bucketName +
                    "," + ase.getMessage(), ase);
		} catch (S3Exception ace) {
			LOG.error("S3Exception: which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network. bucket=" +
                    bucketName + "," + ace.getMessage(), ace);
		} catch (Exception e) {
			LOG.error("Error: " + e.getMessage(), e);
		}
	}

	@Override
	public Set<String> getTables(String regex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addItems(String tableName, List<Item> items, boolean asynch) {
		// TODO Auto-generated method stub
		
	}

	@Nonnull
	@Override
	public List<Item> getItems(String tableName, String minRange, String maxRange, String instanceId, String... jobId) {
		return new ArrayList<>();
	}

	@Override
	public PagedDatabaseResult getPagedItems(String tableName,
			Object nextToken, String minRange, String maxRange,
			String instanceId, String jobId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatabaseName(TankDatabaseType type, String jobId) {
		// TODO Auto-generated method stub
		return bucketName;
	}

}
