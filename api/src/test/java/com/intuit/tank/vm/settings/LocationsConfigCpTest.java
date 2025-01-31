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

import java.util.List;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.junit.jupiter.api.*;

import com.intuit.tank.vm.settings.LocationsConfig;
import com.intuit.tank.vm.settings.SelectableItem;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>LocationsConfigCpTest</code> contains tests for the class <code>{@link LocationsConfig}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class LocationsConfigCpTest {
    /**
     * Run the LocationsConfig(HierarchicalConfiguration) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testLocationsConfig_1()
            throws Exception {
        HierarchicalConfiguration config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();

        LocationsConfig result = new LocationsConfig(config);

        assertNotNull(result);
    }

    /**
     * Run the List<SelectableItem> getLocations() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetLocations_1()
            throws Exception {
        LocationsConfig fixture = new LocationsConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        List<SelectableItem> result = fixture.getLocations();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}