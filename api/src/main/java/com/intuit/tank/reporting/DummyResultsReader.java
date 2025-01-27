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

import java.util.*;

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
