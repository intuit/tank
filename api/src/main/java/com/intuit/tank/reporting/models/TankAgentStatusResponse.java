/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.reporting.models;

import java.io.Serializable;

public class TankAgentStatusResponse implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 230150628670367273L;

    private int numVirtualUsers;
    private int maxVirtualUsers;
    private int rampTimeLeft;
    private long runTime;

    public TankAgentStatusResponse(long time, int num, int max, int ramp) {
        runTime = time;
        numVirtualUsers = num;
        maxVirtualUsers = max;
        rampTimeLeft = ramp;
    }

    public int getRampTimeLeft() {
        return rampTimeLeft;
    }

    public long getRunTime() {
        return runTime;
    }

    public int getCurrentNumberUsers() {
        return numVirtualUsers;
    }

    public int getMaxVirtualUsers() {
        return maxVirtualUsers;
    }

}
