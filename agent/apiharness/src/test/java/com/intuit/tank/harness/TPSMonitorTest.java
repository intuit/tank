package com.intuit.tank.harness;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.reporting.models.TPSInfoContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The class <code>TPSMonitorTest</code> contains tests for the class <code>{@link TPSMonitor}</code>.
 * 
 * @generatedBy CodePro at 9/15/14 4:03 PM
 */
public class TPSMonitorTest {
    /**
     * Run the TPSMonitor(int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/15/14 4:03 PM
     */
    @Test
    public void testTPSMonitor_1()
            throws Exception {
        int period = 15;

        TPSMonitor result = new TPSMonitor(period);

        assertNotNull(result);
    }

    /**
     * Run the void addToMap(String,BaseRequest) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/15/14 4:03 PM
     */
    @Test
    public void testAddToMap_1()
            throws Exception {
        TPSMonitor fixture = new TPSMonitor(1);
        String loggingKey = "Test";
        BaseRequest req = new MockRequest();
        fixture.addToMap(loggingKey, req);
        Thread.sleep(1000);
        TPSInfoContainer tpsInfo = fixture.getTPSInfo();
        int totalTps = tpsInfo.getTotalTps();
        System.out.println(totalTps);
        assertEquals(1, totalTps);
    }

    /**
     * Run the boolean isEnabled() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/15/14 4:03 PM
     */
    @Test
    public void testIsEnabled_1()
            throws Exception {
        TPSMonitor fixture = new TPSMonitor(1);

        boolean result = fixture.isEnabled();

        assertTrue(result);
    }

    /**
     * Run the boolean isEnabled() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/15/14 4:03 PM
     */
    @Test
    public void testIsEnabled_2()
            throws Exception {
        TPSMonitor fixture = new TPSMonitor(0);

        boolean result = fixture.isEnabled();

        assertFalse(result);
    }
}