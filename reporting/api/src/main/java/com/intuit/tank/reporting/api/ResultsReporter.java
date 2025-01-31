package com.intuit.tank.reporting.api;

import java.util.List;

import org.apache.commons.configuration2.HierarchicalConfiguration;

import com.intuit.tank.results.TankResult;

/**
 * 
 * ResultsReporter interface for reporting results to controller. Default is DynamoDB but for standalone can use a rest
 * service or file system implementation.
 * 
 * @author dangleton
 * 
 */
public interface ResultsReporter {

    /**
     * Sends results to storage
     * 
     * @param jobId
     *            the job id
     * @param instanceId
     *            the instance id
     * @param container
     *            the tps infos
     * @param async
     *            true if the job should be run asynchronpously
     */
    public void sendTpsResults(String jobId, String instanceId, TPSInfoContainer container, boolean async);

    /**
     * Send the timing results to storage.
     * 
     * @param jobId
     *            the job id
     * @param instanceId
     *            the instance id
     * @param results
     *            the reulsts
     * @param async
     *            true if the job should be run asynchronpously
     */
    public void sendTimingResults(String jobId, String instanceId, List<TankResult> results, boolean async);

    /**
     * configure this service from config file.
     * 
     * @param config
     *            the config
     */
    public void config(HierarchicalConfiguration config);
}
