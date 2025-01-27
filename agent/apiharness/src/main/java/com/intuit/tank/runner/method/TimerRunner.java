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

import org.apache.commons.lang3.math.NumberUtils;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.data.TimerStep;
import com.intuit.tank.reporting.models.TankResult;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

/**
 * 
 * @author dangleton
 * 
 */
class TimerRunner implements Runner {
    private TestStepContext tsc;

    /**
     * 
     * @param tsc
     */
    TimerRunner(TestStepContext tsc) {
        this.tsc = tsc;
    }

    /**
	 * 
	 */
    public String execute() {
        TimerStep testStep = (TimerStep) tsc.getTestStep();
        if (testStep.isStart()) {
            tsc.getTimerMap().start(testStep.getValue());
        } else {
            TankResult tankResult = tsc.getTimerMap().end(
                    testStep.getValue());
            if (tankResult != null
                    && NumberUtils.isDigits(APITestHarness.getInstance().getAgentRunData().getJobId())) {
                APITestHarness.getInstance().queueTimingResult(tankResult);
            }
        }
        return TankConstants.HTTP_CASE_PASS;
    }

}
