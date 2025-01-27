package com.intuit.tank.runner.method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.Test;

import com.intuit.tank.reporting.models.TankResult;

/**
 * The class <code>TimerMapTest</code> contains tests for the class <code>{@link TimerMap}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class TimerMapTest {
    /**
     * Run the TimerMap() constructor test.
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testTimerMap_1()
        throws Exception {
        TimerMap result = new TimerMap();
        assertNotNull(result);
    }

    /**
     * Run the void addResult(TankResult) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testAddResult_1()
        throws Exception {
        TimerMap fixture = new TimerMap();
        TankResult result = new TankResult();

        fixture.addResult(result);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.runner.method.TimerMap.addResult(TimerMap.java:21)
    }

    /**
     * Run the void addResult(TankResult) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testAddResult_2()
        throws Exception {
        TimerMap fixture = new TimerMap();
        TankResult result = new TankResult();

        fixture.addResult(result);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.runner.method.TimerMap.addResult(TimerMap.java:21)
    }

    /**
     * Run the TankResult end(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testEnd_1()
        throws Exception {
        TimerMap fixture = new TimerMap();
        String name = "";

        TankResult result = fixture.end(name);
    }

    /**
     * Run the boolean isActive() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testIsActive_1()
        throws Exception {
        TimerMap fixture = new TimerMap();

        boolean result = fixture.isActive();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.runner.method.TimerMap.isActive(TimerMap.java:29)
        assertTrue(!result);
    }

    /**
     * Run the boolean isActive() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testIsActive_2()
        throws Exception {
        TimerMap fixture = new TimerMap();

        boolean result = fixture.isActive();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.runner.method.TimerMap.isActive(TimerMap.java:29)
        assertTrue(!result);
    }

    /**
     * Run the void start(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testStart_1()
        throws Exception {
        TimerMap fixture = new TimerMap();
        String name = "";

        fixture.start(name);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.runner.method.TimerMap.start(TimerMap.java:42)
    }
}