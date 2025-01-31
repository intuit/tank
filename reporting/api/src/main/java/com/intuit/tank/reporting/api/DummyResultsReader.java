/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.reporting.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.HierarchicalConfiguration;

import com.intuit.tank.results.TankResult;

/**
 * DummyResultsReader
 * 
 * @author dangleton
 * 
 */
public class DummyResultsReader implements ResultsReader {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TankResult> getAllTimingResults(String jobId) {
        return new ArrayList<TankResult>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedTimingResults getPagedTimingResults(String jobId, Object nextToken) {
        return new PagedTimingResults(null, new ArrayList<TankResult>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasTimingData(String jobId) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTimingForJob(String jobId, boolean asynch) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Date, Map<String, TPSInfo>> getTpsMapForJob(Date minDate, String... jobId) {
        return new HashMap<Date, Map<String, TPSInfo>>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Date, Map<String, TPSInfo>> getTpsMapForInstance(Date minDate, String jobId, String instanceId) {
        return new HashMap<Date, Map<String, TPSInfo>>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void config(HierarchicalConfiguration config) {
        // no-op
    }

}
