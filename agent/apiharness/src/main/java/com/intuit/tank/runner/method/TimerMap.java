package com.intuit.tank.runner.method;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.reporting.models.TankResult;
import com.intuit.tank.reporting.TankResultBuilder;

public class TimerMap {
    private Map<String, TankResult> resultsMap = new HashMap<String, TankResult>();

    /**
     * 
     * @param result
     */
    public void addResult(TankResult result) {
        for (TankResult r : resultsMap.values()) {
            r.add(result);
        }
    }

    /**
     * 
     * @return
     */
    public boolean isActive() {
        return !resultsMap.isEmpty();
    }

    /**
     * 
     * @param name
     */
    public void start(String name) {
        TankResult result = TankResultBuilder.tankResult()
                .withJobId(APITestHarness.getInstance().getAgentRunData().getJobId()).withRequestName(name)
                .withInstanceId(APITestHarness.getInstance().getAgentRunData().getInstanceId())
                .withStatusCode(-1)
                .build();
        resultsMap.put(name, result);

    }

    /**
     * 
     * @param name
     * @return
     */
    public TankResult end(String name) {
        return resultsMap.remove(name);
    }
}
