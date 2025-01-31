package com.intuit.tank.persistence.databases;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.settings.TankConfig;

import jakarta.annotation.Nonnull;

/**
 * WavefrontDatasource
 * 
 * @author Kevin McGoldrick
 * 
 */
public class WavefrontDatasource implements IDatabase {
    private static final Logger LOG = LogManager.getLogger(WavefrontDatasource.class);
	
    InetAddress ip;
    private String hostname = "fail";
	private String enviornemnt = "qa";
	private String metricString = "ctg.tank.transaction";
	private String host = "wavefrontproxy-test.intuit.net";
	private int port = 2878;
    
    private TankConfig config = new TankConfig();
	private HierarchicalConfiguration resultsProviderConfig = config.getVmManagerConfig().getResultsProviderConfig();

	@Override
	public void initNamespace(String tableName) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeNamespace(String tableName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteForJob(String tableName, String jobId, boolean asynch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasTable(String tableName) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean hasJobData(String tableName, String jobId) {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addTimingResults(String tableName, List<TankResult> results, boolean asynch) {
        if (resultsProviderConfig != null) {
            try {
            	hostname = ip.getHostName();
            	enviornemnt = config.getInstanceName();
            	if (StringUtils.equalsIgnoreCase(enviornemnt, "prod")) {
            		enviornemnt = "prd";
            	} else if (StringUtils.equalsIgnoreCase(enviornemnt, "qa")) {
            		enviornemnt = "qal";
            	} else if (StringUtils.equalsIgnoreCase(enviornemnt, "canada")) {
            		enviornemnt = "can";
            	} else {
            		enviornemnt = "prf";
            	}
            	metricString = resultsProviderConfig.getString("metricString");
            	host = resultsProviderConfig.getString("wavefrontHost");
            	String s = resultsProviderConfig.getString("wavefrontPort");
                if (NumberUtils.isDigits(s)) {
                	port = Integer.parseInt(s);
                }
            } catch (Exception e) {
                LOG.error("Failed to get Wavefront parameters " + e.toString());
            }
        }
		String jobId = results.get(0).getJobId();
		String instance = results.get(0).getInstanceId();
		long l = results.get(results.size()-1).getTimeStamp().getTime() / 1000;
		Collections.sort(results);

		StringBuilder sb = new StringBuilder();
		List<Long> groupResults = new ArrayList<Long>();
		String requestName = "";
		long sum = 0;
		for (TankResult metric: results) {
			if (StringUtils.equalsIgnoreCase(metric.getRequestName(), requestName)) { //Middle of the Group
				groupResults.add(Long.valueOf(metric.getResponseTime()));
				sum += metric.getResponseTime();
			} else if (StringUtils.isEmpty(requestName)) { // Handles the first time through //
				requestName = metric.getRequestName();
				sum = metric.getResponseTime();
				groupResults.add(Long.valueOf(sum));
			} else { // Handles the last time through of the group//
				int size = groupResults.size();
				Collections.sort(groupResults);
				Long[] sortedList = groupResults.toArray(new Long[size]);
				long average = sum / size;
				int fiftieth = (size/2);
				if (fiftieth >= 1) fiftieth--;
				float ninety = 0.9f;
				int ninetieth = Math.round(size * ninety);
				if (ninetieth >= 1) ninetieth--;
				float ninetynine = 0.99f;
				int ninetynineth = Math.round(size * ninetynine);
				if (ninetynineth >= 1) ninetynineth--;
				sb.append(metricString + ".resp_time.min " + sortedList[0] + " " + l)
					.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
					.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
					.append(metricString + ".resp_time.avg " + average + " " + l)
					.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
					.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
					.append(metricString + ".resp_time.max " + sortedList[size - 1] + " " + l)
					.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
					.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
					.append(metricString + ".resp_time.tp_50 " + sortedList[fiftieth] + " " + l)
					.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
					.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
					.append(metricString + ".resp_time.tp_90 " + sortedList[ninetieth] + " " + l)
					.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
					.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
					.append(metricString + ".resp_time.tp_99 " + sortedList[ninetynineth] + " " + l)
					.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
					.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
					.append(metricString + ".rpi " + size + " " + l)
					.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
					.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"));
				requestName = metric.getRequestName();
				groupResults.clear();
				groupResults.add(Long.valueOf(metric.getResponseTime()));
				sum = 0;
			}
		} // Get that last one //
		int size = groupResults.size();
		Collections.sort(groupResults);
		Long[] sortedList = groupResults.toArray(new Long[size]);
		long average = sum / size;
		int fiftieth = (size/2);
		if (fiftieth >= 1) fiftieth--;
		float ninety = 0.9f;
		int ninetieth = Math.round(size * ninety);
		if (ninetieth >= 1) ninetieth--;
		float ninetynine = 0.99f;
		int ninetynineth = Math.round(size * ninetynine);
		if (ninetynineth >= 1) ninetynineth--;
		sb.append(metricString + ".resp_time.min " + sortedList[0] + " " + l)
			.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
			.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
			.append(metricString + ".resp_time.avg " + average + " " + l)
			.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
			.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
			.append(metricString + ".resp_time.max " + sortedList[size - 1] + " " + l)
			.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
			.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
			.append(metricString + ".resp_time.tp_50 " + sortedList[fiftieth] + " " + l)
			.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
			.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
			.append(metricString + ".resp_time.tp_90 " + sortedList[ninetieth] + " " + l)
			.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
			.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
			.append(metricString + ".resp_time.tp_99 " + sortedList[ninetynineth] + " " + l)
			.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
			.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"))
			.append(metricString + ".rpi " + size + " " + l)
			.append(" source=" + hostname + " instanceid=" + instance + " transaction=" + requestName)
			.append(" bu=ctg app=tnk pool=agent env=" + enviornemnt + " jobid=" + jobId + System.getProperty("line.separator"));
		
		try {
			Socket socket = new Socket(host, port);
			OutputStream s = socket.getOutputStream();
			PrintWriter out = new PrintWriter(s, true);
			out.print(sb);
			out.flush();
			out.close();
			socket.close();
		} catch (UnknownHostException e) {
			LOG.error("Unknown host: " + host);
		} catch (IOException e) {
			LOG.error("Error while writing data to wavefront: " + e.getMessage(), e);
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
		return host;
	}

}
