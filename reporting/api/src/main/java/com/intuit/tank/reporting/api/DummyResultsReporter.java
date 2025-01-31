/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.reporting.api;

import java.util.List;

import org.apache.commons.configuration2.HierarchicalConfiguration;

import com.intuit.tank.results.TankResult;

/**
 * DummyResultsReporter
 * 
 * @author dangleton
 * 
 */
public class DummyResultsReporter implements ResultsReporter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendTpsResults(String jobId, String instanceId, TPSInfoContainer container, boolean async) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendTimingResults(String jobId, String instanceId, List<TankResult> results, boolean asynch) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void config(HierarchicalConfiguration config) {
        // do nothing

    }

}
