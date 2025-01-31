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
import jakarta.annotation.Nonnull;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ReportingConfig
 * 
 * @author dangleton
 * 
 */
public class ReportingConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(ReportingConfig.class);

    private static final String KEY_READER = "provider/reader";
    private static final String KEY_REPORTER = "provider/reporter";
    private static final String KEY_CONFIG = "provider/config";
    private static final String DEFAULT_REPORTER = "com.intuit.tank.reporting.db.DatabaseResultsReporter";
    private static final String DEFAULT_READER = "com.intuit.tank.reporting.db.DatabaseResultsReader";

    private HierarchicalConfiguration config;

    public ReportingConfig(@Nonnull HierarchicalConfiguration config) {
        this.config = config;
    }

    /**
     * @return the products
     */
    public String getReporterClass() {
        String ret = DEFAULT_REPORTER;
        if (config != null) {
            try {
                HierarchicalConfiguration configurationAt = config.configurationAt(KEY_REPORTER);
                String string = configurationAt.getString("");
                if (StringUtils.isNotBlank(string)) {
                    ret = string;
                } else {
                    LOG.warn("Reporter not configured. Using default of " + ret);
                }
            } catch (Exception e) {
                LOG.warn("Reporter specified more than once. Using default of " + ret);
            }
        }
        return ret;
    }

    /**
     * @return the products
     */
    public String getReaderClass() {
        String ret = DEFAULT_READER;
        if (config != null) {
            try {
                HierarchicalConfiguration configurationAt = config.configurationAt(KEY_READER);
                String string = configurationAt.getString("");
                if (StringUtils.isNotBlank(string)) {
                    ret = string;
                } else {
                    LOG.warn("Reader not configured. Using default of " + ret);
                }
            } catch (Exception e) {
                LOG.warn("Reader specified more than once. Using default of " + ret);
            }
        }
        return ret;
    }

    /**
     * @return the products
     */
    public HierarchicalConfiguration getProviderConfig() {
        HierarchicalConfiguration ret = null;
        if (config != null) {
            try {
                ret = config.configurationAt(KEY_CONFIG);
            } catch (Exception e) {
                LOG.error("Provider config not specified or specifed more than once.");
            }
        }
        return ret;
    }

}
