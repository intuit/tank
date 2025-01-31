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

import java.io.*;
import java.net.URL;
import java.util.List;

import jakarta.annotation.Nonnull;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * BaseCommonsConfig
 * 
 * @author dangleton
 */
public abstract class BaseCommonsXmlConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(BaseCommonsXmlConfig.class);

    protected XMLConfiguration XMLConfig;

    protected File configFile;

    /**
     * Constructor pulls file out of the jar or reads from disk and sets up refresh policy.
     *
     */
    protected void readConfig() {
        try {
            ExpressionEngine expressionEngine = new XPathExpressionEngine();
            String configPath = getConfigName();

            File dataDirConfigFile = new File(configPath);
//            LOG.info("Reading settings from " + dataDirConfigFile.getAbsolutePath());
            if (!dataDirConfigFile.exists()) {
                // Load a default from the classpath:
                // Note: we don't let new XMLConfiguration() lookup the resource
                // url directly because it may not be able to find the desired
                // classloader to load the URL from.
                URL configResourceUrl = this.getClass().getClassLoader().getResource(configPath);
                if (configResourceUrl == null) {
                    throw new RuntimeException("unable to load resource: " + configPath);
                }

                XMLConfiguration tmpConfig = new ReloadingFileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
                        .configure(new Parameters().xml().setURL(configResourceUrl))
                        .getConfiguration();

                // Copy over a default configuration since none exists:
                // Ensure data dir location exists:
                if (dataDirConfigFile.getParentFile() != null && !dataDirConfigFile.getParentFile().exists()
                        && !dataDirConfigFile.getParentFile().mkdirs()) {
                    throw new RuntimeException("could not create directories.");
                }
                tmpConfig.write(new FileWriter(dataDirConfigFile));
                LOG.info("Saving settings file to " + dataDirConfigFile.getAbsolutePath());
            }

            if (dataDirConfigFile.exists()) {
                XMLConfig = new ReloadingFileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
                        .configure(new Parameters().xml().setFile(dataDirConfigFile))
                        .getConfiguration();
            } else {
                // extract from jar and write to
                throw new IllegalStateException("Config file does not exist or cannot be created");
            }
            if (expressionEngine != null) {
                XMLConfig.setExpressionEngine(expressionEngine);
            }
            configFile = dataDirConfigFile;
            initConfig(XMLConfig);
        } catch (ConfigurationException e) {
            LOG.error("Error reading settings file: " + e, e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getSourceConfigFile() {
        return configFile;
    }

    @SuppressWarnings("unchecked")
    public static HierarchicalConfiguration getChildConfigurationAt(HierarchicalConfiguration config, String key) {
        if (config == null) {
            return null;
        }
        List<HierarchicalConfiguration> configs = config.configurationsAt(key);
        if (configs.size() > 1) {
            LOG.warn("Child configuration with key " + key + " matches more than one node.");
        } else if (configs.size() == 0) {
            LOG.warn("Child configuration with key " + key + " has no entry in config file.");
        }
        return configs.size() != 0 ? configs.get(0) : null;
    }

    /**
     * @return the config
     */
    protected XMLConfiguration getConfig() {
        return XMLConfig;
    }

    /**
     * 
     * @return the name of the config to fetch. e.g. SitesConfig.xml
     */
    @Nonnull
    protected abstract String getConfigName();

    /**
     * initialize the configuration form the passed in config.
     * 
     * @param configuration
     *            the configuration to initialize from
     */
    protected abstract void initConfig(@Nonnull XMLConfiguration configuration);

}
