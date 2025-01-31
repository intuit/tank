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

import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.settings.InstanceDescription;

/**
 * The class <code>InstanceDescriptionCpTest</code> contains tests for the class
 * <code>{@link InstanceDescription}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class InstanceDescriptionCpTest {
    /**
     * Run the InstanceDescription(HierarchicalConfiguration,HierarchicalConfiguration) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testInstanceDescription_1()
            throws Exception {
        HierarchicalConfiguration config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();
        HierarchicalConfiguration defaultInstance = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();

        InstanceDescription result = new InstanceDescription(config, defaultInstance);

        assertNotNull(result);
        assertEquals(null, result.getAmi());
        assertEquals(null, result.getPublicIp());
        assertEquals(null, result.getZone());
        assertEquals(null, result.getLocation());
        assertEquals(null, result.getSecurityGroup());
//        assertEquals(null, result.getSubnetIds().get(0));
        assertEquals(null, result.getKeypair());
    }

    /**
     * Run the String getAmi() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetAmi_1()
            throws Exception {
        InstanceDescription fixture = new InstanceDescription(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getAmi();

        assertEquals(null, result);
    }

    /**
     * Run the String getPublicIp() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetPublicIp_1()
            throws Exception {
        InstanceDescription fixture = new InstanceDescription(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getPublicIp();

        assertEquals(null, result);
    }

}