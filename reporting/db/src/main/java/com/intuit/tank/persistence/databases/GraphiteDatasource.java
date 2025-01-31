package com.intuit.tank.persistence.databases;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
 * GraphiteDatasource
 * 
 * @author Kevin McGoldrick
 * 
 */
public class GraphiteDatasource implements IDatabase {
    private static final Logger LOG = LogManager.getLogger(GraphiteDatasource.class);
	
	private String enviornemnt = "qa";
	private String host = "doubleshot.perf.a.intuit.com";
	private int port = 2003;
    private int interval = 15; // SECONDS
    
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
            	enviornemnt = config.getInstanceName();
            	host = resultsProviderConfig.getString("graphiteHost");
            	String s = resultsProviderConfig.getString("graphitePort");
                if (NumberUtils.isDigits(s)) {
                	port = Integer.parseInt(s);
                }
            } catch (Exception e) {
                LOG.error("Failed to get Graphite parameters " + e.toString());
            }
        }
		String jobId = results.get(0).getJobId();
		long l = results.get(results.size()-1).getTimeStamp().getTime() / 1000;
		Collections.sort(results);
		try {
			Socket socket = new Socket(host, port);
			OutputStream s = socket.getOutputStream();
			PrintWriter out = new PrintWriter(s, true);
			List<Long> groupResults = new ArrayList<Long>();
			String requestName = "";
			long sum = 0;
			//int count = 0;
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
					long tps = size / interval;
					int fiftieth = (size/2);
					if (fiftieth >= 1) fiftieth--;
					float ninety = 0.9f;
					int ninetieth = Math.round(size * ninety);
					if (ninetieth >= 1) ninetieth--;
					float ninetynine = 0.99f;
					int ninetynineth = Math.round(size * ninetynine);
					if (ninetynineth >= 1) ninetynineth--;
					out.printf("tank.%s.%s.%s.ResponseTime.MIN %d %d%n", enviornemnt, jobId, requestName, sortedList[0], l );
					out.printf("tank.%s.%s.%s.ResponseTime.AVG %d %d%n", enviornemnt, jobId, requestName, average, l );
					out.printf("tank.%s.%s.%s.ResponseTime.MAX %d %d%n", enviornemnt, jobId, requestName, sortedList[size - 1], l );
					out.printf("tank.%s.%s.%s.ResponseTime.50th %d %d%n", enviornemnt, jobId, requestName, sortedList[fiftieth], l );
					out.printf("tank.%s.%s.%s.ResponseTime.90th %d %d%n", enviornemnt, jobId, requestName, sortedList[ninetieth], l );
					out.printf("tank.%s.%s.%s.ResponseTime.99th %d %d%n", enviornemnt, jobId, requestName, sortedList[ninetynineth], l );
					out.printf("tank.%s.%s.%s.TPS %d %d%n", enviornemnt, jobId, requestName, tps, l );
					out.printf("tank.%s.%s.%s.count %d %d%n", enviornemnt, jobId, requestName, size, l );
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
			long tps = size / interval;
			int fiftieth = (size/2);
			if (fiftieth >= 1) fiftieth--;
			float ninety = 0.9f;
			int ninetieth = Math.round(size * ninety);
			if (ninetieth >= 1) ninetieth--;
			float ninetynine = 0.99f;
			int ninetynineth = Math.round(size * ninetynine);
			if (ninetynineth >= 1) ninetynineth--;
			out.printf("tank.%s.%s.%s.ResponseTime.MIN %d %d%n", enviornemnt, jobId, requestName, sortedList[0], l );
			out.printf("tank.%s.%s.%s.ResponseTime.AVG %d %d%n", enviornemnt, jobId, requestName, average, l );
			out.printf("tank.%s.%s.%s.ResponseTime.MAX %d %d%n", enviornemnt, jobId, requestName, sortedList[size - 1], l );
			out.printf("tank.%s.%s.%s.ResponseTime.50th %d %d%n", enviornemnt, jobId, requestName, sortedList[fiftieth], l );
			out.printf("tank.%s.%s.%s.ResponseTime.90th %d %d%n", enviornemnt, jobId, requestName, sortedList[ninetieth], l );
			out.printf("tank.%s.%s.%s.ResponseTime.99th %d %d%n", enviornemnt, jobId, requestName, sortedList[ninetynineth], l );
			out.printf("tank.%s.%s.%s.TPS %d %d%n", enviornemnt, jobId, requestName, tps, l );
			out.printf("tank.%s.%s.%s.count %d %d%n", enviornemnt, jobId, requestName, size, l );
			out.close();
			socket.close();
		} catch (UnknownHostException e) {
			LOG.error("Unknown host: " + host);
		} catch (IOException e) {
			LOG.error("Error while writing data to graphite: " + e.getMessage(), e);
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
