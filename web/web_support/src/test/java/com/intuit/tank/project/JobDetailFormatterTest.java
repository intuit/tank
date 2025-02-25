package com.intuit.tank.project;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.intuit.tank.project.JobDetailFormatter;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.JobValidator;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.settings.TankConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JobDetailFormatterTest {

    @Test
    public void addError() {
        StringBuilder sb = new StringBuilder();
        JobDetailFormatter.addError(sb, "Error");
        assertFalse(sb.isEmpty());
    }

    @Test
    public void addProperty() {
        StringBuilder sb = new StringBuilder();
        JobDetailFormatter.addProperty(sb, "key", "value");
        assertTrue(sb.length() > 0);
    }


    @Test
    public void buildDetails() {
//        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void calculateCost() {
//        JobDetailFormatter.calculateCost(new TankConfig(), , regions, simulationTime)
//        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void createJobDetailsJobValidatorWorkloadJobInstance() {
//        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void createJobDetailsJobValidatorString() {
//        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void estimateCost() {
        BigDecimal estimateCost = JobDetailFormatter.estimateCost(2, BigDecimal.valueOf(1.5D), 600000);
        assertEquals(7.50D, estimateCost.doubleValue());
    }

    @Test
    public void getSimulationTime() {
        JobInstance ji = new JobInstance();
        ji.setSimulationTime(2000L);
//        JobDetailFormatter.getSimulationTime(proposedJobInstance, workload, validator)
//        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void getVmDetails() {
        String vmDetails = JobDetailFormatter.getVmDetails(new TankConfig(), "m.xlarge");
        assertEquals("m.xlarge (types=[m8g.xlarge, m7g.xlarge] cpus=4 ecus=16 memory=16.0 cost=$0.1795 per hour)", vmDetails);
    }

    /**
     * Run the JobDetailFormatter() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testJobDetailFormatter_1()
        throws Exception {
        JobDetailFormatter result = new JobDetailFormatter();
        assertNotNull(result);
    }

    /**
     * Run the void addError(StringBuilder,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testAddError_1()
        throws Exception {
        StringBuilder sb = new StringBuilder();
        String message = "";

        JobDetailFormatter.addError(sb, message);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobDetailFormatter.addError(JobDetailFormatter.java:350)
    }

    /**
     * Run the void addProperty(StringBuilder,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testAddProperty_1()
        throws Exception {
        StringBuilder sb = new StringBuilder();
        String key = "";
        String value = "";

        JobDetailFormatter.addProperty(sb, key, value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobDetailFormatter.addProperty(JobDetailFormatter.java:320)
    }

    /**
     * Run the String calculateCost(TankConfig,JobInstance,List<JobRegion>,long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCalculateCost_1()
        throws Exception {
        TankConfig config = new TankConfig();
        JobInstance proposedJobInstance = new JobInstance();
        List<JobRegion> regions = new LinkedList<>();
        long simulationTime = 1L;

        String result = JobDetailFormatter.calculateCost(config, proposedJobInstance, regions, simulationTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.settings.TankConfig.<clinit>(TankConfig.java:52)
        assertNotNull(result);
    }

    /**
     * Run the String calculateCost(TankConfig,JobInstance,List<JobRegion>,long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCalculateCost_2()
        throws Exception {
        TankConfig config = new TankConfig();
        JobInstance proposedJobInstance = new JobInstance();
        List<JobRegion> regions = new LinkedList<>();
        long simulationTime = 1L;

        String result = JobDetailFormatter.calculateCost(config, proposedJobInstance, regions, simulationTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }

    /**
     * Run the String calculateCost(TankConfig,JobInstance,List<JobRegion>,long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCalculateCost_3()
        throws Exception {
        TankConfig config = new TankConfig();
        JobInstance proposedJobInstance = new JobInstance();
        List<JobRegion> regions = new LinkedList<>();
        long simulationTime = 1L;

        String result = JobDetailFormatter.calculateCost(config, proposedJobInstance, regions, simulationTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }

    /**
     * Run the String calculateCost(TankConfig,JobInstance,List<JobRegion>,long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCalculateCost_4()
        throws Exception {
        TankConfig config = new TankConfig();
        JobInstance proposedJobInstance = new JobInstance();
        List<JobRegion> regions = new LinkedList<>();
        long simulationTime = 1L;

        String result = JobDetailFormatter.calculateCost(config, proposedJobInstance, regions, simulationTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }

    /**
     * Run the String createJobDetails(JobValidator,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCreateJobDetails_1()
        throws Exception {
        JobValidator validator = new JobValidator(new Script());
        String scriptName = "";

        String result = JobDetailFormatter.createJobDetails(validator, scriptName);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:60)
        assertNotNull(result);
    }

    /**
     * Run the String createJobDetails(JobValidator,Workload,JobInstance) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCreateJobDetails_2()
        throws Exception {
        JobValidator validator = new JobValidator(new Script());
        Workload workload = new Workload();
        JobInstance proposedJobInstance = new JobInstance();

        String result = JobDetailFormatter.createJobDetails(validator, workload, proposedJobInstance);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:60)
        assertNotNull(result);
    }

    /**
     * Run the BigDecimal estimateCost(int,BigDecimal,long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testEstimateCost_1()
        throws Exception {
        int numInstances = 1;
        BigDecimal costPerHour = new BigDecimal(1.0);
        long time = 1L;

        BigDecimal result = JobDetailFormatter.estimateCost(numInstances, costPerHour, time);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobDetailFormatter.estimateCost(JobDetailFormatter.java:300)
        assertNotNull(result);
    }

    /**
     * Run the long getSimulationTime(JobInstance,Workload,JobValidator) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSimulationTime_1()
        throws Exception {
        JobInstance proposedJobInstance = new JobInstance();
        Workload workload = new Workload();
        workload.setTestPlan(new LinkedList<>());
        JobValidator validator = new JobValidator(new Script());

        long result = JobDetailFormatter.getSimulationTime(proposedJobInstance, workload, validator);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.enumerated.IncrementStrategy.<init>(IncrementStrategy.java:23)
        //       at com.intuit.tank.api.enumerated.IncrementStrategy.<clinit>(IncrementStrategy.java:13)
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertEquals(0L, result);
    }

    /**
     * Run the long getSimulationTime(JobInstance,Workload,JobValidator) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSimulationTime_2()
        throws Exception {
        JobInstance proposedJobInstance = new JobInstance();
        Workload workload = new Workload();
        workload.setTestPlan(new LinkedList<>());
        JobValidator validator = new JobValidator(new Script());

        long result = JobDetailFormatter.getSimulationTime(proposedJobInstance, workload, validator);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertEquals(0L, result);
    }

    /**
     * Run the long getSimulationTime(JobInstance,Workload,JobValidator) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSimulationTime_3()
        throws Exception {
        JobInstance proposedJobInstance = new JobInstance();
        Workload workload = new Workload();
        JobValidator validator = new JobValidator(new Script());

        long result = JobDetailFormatter.getSimulationTime(proposedJobInstance, workload, validator);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobInstance.<init>(JobInstance.java:115)
        assertEquals(0L, result);
    }

    /**
     * Run the String getVmDetails(TankConfig,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVmDetails_1()
        throws Exception {
        TankConfig config = new TankConfig();
        String vmInstanceType = "";

        String result = JobDetailFormatter.getVmDetails(config, vmInstanceType);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }

    /**
     * Run the String getVmDetails(TankConfig,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVmDetails_2()
        throws Exception {
        TankConfig config = new TankConfig();
        String vmInstanceType = "";

        String result = JobDetailFormatter.getVmDetails(config, vmInstanceType);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }

    /**
     * Run the String getVmDetails(TankConfig,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVmDetails_3()
        throws Exception {
        TankConfig config = new TankConfig();
        String vmInstanceType = "";

        String result = JobDetailFormatter.getVmDetails(config, vmInstanceType);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }
}
