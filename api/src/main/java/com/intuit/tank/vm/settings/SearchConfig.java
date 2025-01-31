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

import java.io.File;

import jakarta.annotation.Nonnull;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;

public class SearchConfig {

    private static final long serialVersionUID = 1L;

    private static final String KEY_SEARCH_LOCATION = "index-location";

    private HierarchicalConfiguration config;

    private String searchLocation;

    public SearchConfig(@Nonnull HierarchicalConfiguration config) {
        this.config = config;
        initConfig();
    }

    private void initConfig() {
        if (config != null) {
            HierarchicalConfiguration searchLocationConfig = config.configurationAt(KEY_SEARCH_LOCATION);
            searchLocation = searchLocationConfig.getString("");
        }
        if (StringUtils.isEmpty(searchLocation)) {
            searchLocation = "./searchDirectory";
        }
        File file = new File(searchLocation);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * @return the searchLocation
     */
    public String getSearchLocation() {
        return searchLocation;
    }

}
