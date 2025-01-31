/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Nonnull;

import org.apache.commons.configuration2.HierarchicalConfiguration;

/**
 * ProductConfig
 * 
 * @author dangleton
 * 
 */
public class LocationsConfig implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String KEY_LOCATION = "location";
    private static final String KEY_NAME = "@name";
    private static final String KEY_DISPLAY_NAME = "@displayName";

    private List<SelectableItem> locations;
    private HierarchicalConfiguration config;

    public LocationsConfig(@Nonnull HierarchicalConfiguration config) {
        this.config = config;
        initConfig();
    }

    @SuppressWarnings("unchecked")
    private void initConfig() {
        locations = new ArrayList<SelectableItem>();
        if (config != null) {
            List<HierarchicalConfiguration> e = config.configurationsAt(KEY_LOCATION);
            if (e != null) {
                for (HierarchicalConfiguration c : e) {
                    locations.add(new SelectableItem(c.getString(KEY_DISPLAY_NAME), c.getString(KEY_NAME)));
                }
            }
        }
        Collections.sort(locations);
    }

    /**
     * @return the locations
     */
    public List<SelectableItem> getLocations() {
        return locations;
    }

}
