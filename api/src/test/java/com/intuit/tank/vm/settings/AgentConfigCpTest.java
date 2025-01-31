package com.intuit.tank.vm.settings;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>AgentConfigCpTest</code> contains tests for the class <code>{@link AgentConfig}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class AgentConfigCpTest {
    /**
     * Run the AgentConfig(HierarchicalConfiguration) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testAgentConfig_1()
            throws Exception {
        HierarchicalConfiguration config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();

        AgentConfig result = new AgentConfig(config);
        Map<String, String> empty = new HashMap<String, String>();

        assertNotNull(result);
        assertEquals(Long.valueOf(40000L), result.getConnectionTimeout());
        assertEquals(false, result.getLogVariables());
        assertEquals(false, result.getLogPostResponse());
        assertEquals(8090, result.getAgentPort());
        assertEquals(false, result.getLogPostRequest());
        assertEquals(90000L, result.getSSLTimeout());
        assertEquals(5000L, result.getMaxAgentResponseTime());
        assertEquals(180000L, result.getMaxAgentWaitTime());
        assertEquals("/tmp", result.getAgentDataFileStorageDir());
        assertEquals(15, result.getTPSPeriod());
        assertEquals(7200000L, result.getOverSimulationMaxTime());
        assertEquals(5000, result.getMaxBodyReportSize());
        assertEquals(null, result.getDefaultResultProvider());
        assertEquals(empty, result.getRequestHeaderMap());
        assertEquals("Apache HttpClient 4.5", result.getTankClientDefault());
        assertEquals("com.intuit.tank.httpclient4.TankHttpClient4", result.getTankClientClassDefault());
        assertEquals("Apache HttpClient 4.5", result.getTankClientName("test"));
        empty.put("Apache HttpClient 4.5", "com.intuit.tank.httpclient4.TankHttpClient4");
        empty.put("JDK Http Client", "com.intuit.tank.httpclientjdk.TankHttpClientJDK");
        empty.put("Apache HttpClient 3.1", "com.intuit.tank.httpclient3.TankHttpClient3");
        assertEquals(empty, result.getTankClientMap());
    }

    /**
     * Run the String getAgentDataFileStorageDir() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetAgentDataFileStorageDir_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        String result = fixture.getAgentDataFileStorageDir();

        assertEquals("/tmp", result);
    }

    /**
     * Run the int getAgentPort() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetAgentPort_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        int result = fixture.getAgentPort();

        assertEquals(8090, result);
    }

    /**
     * Run the Long getConnectionTimeout() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetConnectionTimeout_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        Long result = fixture.getConnectionTimeout();

        assertNotNull(result);
        assertEquals("40000", result.toString());
        assertEquals((byte) 64, result.byteValue());
        assertEquals((short) -25536, result.shortValue());
        assertEquals(40000, result.intValue());
        assertEquals(40000L, result.longValue());
        assertEquals(40000.0f, result.floatValue(), 1.0f);
        assertEquals(40000.0, result.doubleValue(), 1.0);
    }

    /**
     * Run the String getDefaultResultProvider() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetDefaultResultProvider_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        String result = fixture.getDefaultResultProvider();

        assertEquals(null, result);
    }

    /**
     * Run the boolean getLogPostRequest() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetLogPostRequest_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        boolean result = fixture.getLogPostRequest();

        assertEquals(false, result);
    }

    /**
     * Run the boolean getLogPostRequest() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetLogPostRequest_2()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        boolean result = fixture.getLogPostRequest();

        assertEquals(false, result);
    }

    /**
     * Run the boolean getLogPostResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetLogPostResponse_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        boolean result = fixture.getLogPostResponse();

        assertEquals(false, result);
    }

    /**
     * Run the boolean getLogPostResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetLogPostResponse_2()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        boolean result = fixture.getLogPostResponse();

        assertEquals(false, result);
    }

    /**
     * Run the boolean getLogVariables() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetLogVariables_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        boolean result = fixture.getLogVariables();

        assertEquals(false, result);
    }

    /**
     * Run the boolean getLogVariables() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetLogVariables_2()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        boolean result = fixture.getLogVariables();

        assertEquals(false, result);
    }

    /**
     * Run the long getMaxAgentResponseTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetMaxAgentResponseTime_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        long result = fixture.getMaxAgentResponseTime();

        assertEquals(5000L, result);
    }

    /**
     * Run the long getMaxAgentWaitTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetMaxAgentWaitTime_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        long result = fixture.getMaxAgentWaitTime();

        assertEquals(180000L, result);
    }

    /**
     * Run the int getMaxBodyReportSize() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetMaxBodyReportSize_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        int result = fixture.getMaxBodyReportSize();

        assertEquals(5000, result);
    }

    /**
     * Run the long getOverSimulationMaxTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetOverSimulationMaxTime_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        long result = fixture.getOverSimulationMaxTime();

        assertEquals(7200000L, result);
    }

    /**
     * Run the Range getRange(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetRange_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());
        String type = "";

        Range result = fixture.getRange(type);

        assertNotNull(result);
        assertEquals(50L, result.getMin());
        assertEquals(300L, result.getMax());
    }

    /**
     * Run the String getResultsProviderClass(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetResultsProviderClass_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());
        String providerKey = "";

        String result = fixture.getResultsProviderClass(providerKey);

        assertEquals(null, result);
    }

    /**
     * Run the Map<String, String> getResultsTypeMap() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetResultsTypeMap_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        Map<String, String> result = fixture.getResultsTypeMap();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the long getSSLTimeout() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetSSLTimeout_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        long result = fixture.getSSLTimeout();

        assertEquals(90000L, result);
    }

    /**
     * Run the long getStatusReportIntervalMilis(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetStatusReportIntervalMilis_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());
        long pollTime = 1L;

        long result = fixture.getStatusReportIntervalMilis(pollTime);

        assertEquals(1L, result);
    }

    /**
     * Run the int getTPSPeriod() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetTPSPeriod_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        int result = fixture.getTPSPeriod();

        assertEquals(15, result);
    }

    /**
     * Run the Collection<String> getTextMimeTypeRegex() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetTextMimeTypeRegex_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());

        Collection<String> result = fixture.getTextMimeTypeRegex();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setResultsTypeMap(Map<String,String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testSetResultsTypeMap_1()
            throws Exception {
        AgentConfig fixture = new AgentConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        fixture.setResultsTypeMap(new HashMap<String,String>());
        Map<String, String> resultsTypeMap = new HashMap<String,String>();

        fixture.setResultsTypeMap(resultsTypeMap);

    }
}