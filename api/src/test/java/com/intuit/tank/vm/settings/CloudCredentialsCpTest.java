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

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>CloudCredentialsCpTest</code> contains tests for the class <code>{@link CloudCredentials}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class CloudCredentialsCpTest {
    /**
     * Run the CloudCredentials(HierarchicalConfiguration) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testCloudCredentials_1()
            throws Exception {
        HierarchicalConfiguration config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();

        CloudCredentials result = new CloudCredentials(config);

        assertNotNull(result);
        assertNull(result.getKey());
        assertNull(result.getProxyHost());
        assertNull(result.getProxyPort());
        assertNull(result.getKeyId());
    }

    /**
     * Run the String getKey() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetKey_1()
            throws Exception {
        CloudCredentials fixture = new CloudCredentials(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getKey();

        assertNull(result);
    }

    /**
     * Run the String getKeyId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetKeyId_1()
            throws Exception {
        CloudCredentials fixture = new CloudCredentials(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getKeyId();

        assertNull(result);
    }

    /**
     * Run the String getProxyHost() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetProxyHost_1()
            throws Exception {
        CloudCredentials fixture = new CloudCredentials(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getProxyHost();

        assertNull(result);
    }

    /**
     * Run the String getProxyPort() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetProxyPort_1()
            throws Exception {
        CloudCredentials fixture = new CloudCredentials(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getProxyPort();

        assertNull(result);
    }

    /**
     * Run the CloudProvider getType() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetType_1()
            throws Exception {
        CloudCredentials fixture = new CloudCredentials(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        CloudProvider result = fixture.getType();

        assertNotNull(result);
        assertEquals("amazon", result.name());
        assertEquals("amazon", result.toString());
        assertEquals(0, result.ordinal());
    }
}