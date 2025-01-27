/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.reporting;

import com.intuit.tank.reporting.models.TPSInfo;
import com.intuit.tank.reporting.models.TankResult;
import org.apache.commons.configuration.HierarchicalConfiguration;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ResultsReader {

    /**
     * Gets all the timing results for the given job
     * 
     * @param jobId
     *            the job to get timing for
     * @return list of TankResults
     */
    @Nonnull
    public List<TankResult> getAllTimingResults(String jobId);

    /**
     * Gets the Results in a paged manner to limit the effect on memory heap.
     * 
     * @param jobId
     *            the job to get timing for
     * @param nextToken
     *            passing in a nonnull value will get results from the poing last retrieved.
     * @return PagedTimingResults containing the linst of tank results and a nexttoken that if not null indicates that
     *         there are more items to be fetched.
     */
    @Nonnull
    public PagedTimingResults getPagedTimingResults(String jobId, Object nextToken);

    /**
     * Tests if there is timing data for the specified job
     * 
     * @param jobId
     *            the job to get timing for
     * @return true if there is data
     */
    public boolean hasTimingData(String jobId);

    /**
     * deletes timing data from storage.
     * 
     * @param jobId
     *            the job to get timing for
     * @param asynch
     *            true to run deletion asynchronously
     */
    public void deleteTimingForJob(String jobId, boolean asynch);

    /**
     * Gets the TPS data as a map of maps for the list of jobs.
     * 
     * @param jobId
     *            the job to get timing for
     * @return Map of maps
     */
    @Nonnull
    public Map<Date, Map<String, TPSInfo>> getTpsMapForJob(Date minDate, String... jobId);

    /**
     * Gets the TPS data as a map of maps for the instance.
     * 
     * @param jobId
     *            the job to get timing for
     * @param instanceId
     *            the instance to fetch the tps data for.
     * @return Map of maps
     */
    @Nonnull
    public Map<Date, Map<String, TPSInfo>> getTpsMapForInstance(Date minDate, String jobId, String instanceId);

    /**
     * configure this service from config file.
     * 
     * @param config
     *            the config
     */
    public void config(HierarchicalConfiguration config);

}
