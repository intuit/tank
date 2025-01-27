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
