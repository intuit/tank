/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.reporting;

import com.intuit.tank.reporting.models.TPSInfoContainer;
import com.intuit.tank.reporting.models.TankResult;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.List;

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
