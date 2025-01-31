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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

/**
 * CnrComConfig configurator for the beans config file.
 * 
 * <br>
 * Patterns: Singleton
 * 
 * @author dangleton
 */
@Named
@ApplicationScoped
public class TankConfig extends BaseCommonsXmlConfig {
    private static final Logger LOG = LogManager.getLogger(TankConfig.class);

    private static final long serialVersionUID = 1L;
    private static final String KEY_VM_MANAGER_NODE = "vm-manager";
    private static final String KEY_AGENT_NODE = "agent-config";
    private static final String KEY_CONTROLLER_BASE = "controller-base-url";
    private static final String KEY_INSTANCE_NAME = "instance-name";
    private static final String KEY_DATA_FILE_STORAGE = "data-file-storage";
    private static final String KEY_REST_SECURITY_ENABLED = "rest-security-enabled";
    private static final String KEY_TMP_FILE_STORAGE = "tmp-file-storage";
    private static final String KEY_JAR_FILE_STORAGE = "jar-file-storage";
    private static final String KEY_TIMING_FILE_STORAGE = "timing-file-storage";
    private static final String KEY_MAIL_NODE = "mail";
    private static final String KEY_STANDALONE = "standalone";
    private static final String KEY_ENCRYPT_S3 = "s3-encrypt";
    private static final String CONFIG_NAME = "settings.xml";
    private static final String KEY_PRODUCTS_NODE = "products";
    private static final String KEY_SECURITY_NODE = "security";
    private static final String KEY_LOCATIONS_NODE = "locations";
    private static final String KEY_LOGIC_STEP_NODE = "logic-step";
    private static final String KEY_REPORTING_NODE = "reporting";
    private static final String KEY_OIDC_SSO_NODE = "oidc-sso";

    private static final String KEY_BANNER_TEXT = "banner-text";

    private static String configName = CONFIG_NAME;

    static {
        File file = new File(configName);
        LOG.info(new ObjectMessage(ImmutableMap.of("Message", "checking file " + file.getAbsolutePath() + ": exists=" + file.exists())));
        if (!file.exists()) {
            LOG.info("System.getenv('WATS_PROPERTIES') = '" + System.getenv("WATS_PROPERTIES") + "'");
            LOG.info("System.getProperty('WATS_PROPERTIES') = '" + System.getProperty("WATS_PROPERTIES") + "'");
            if (System.getenv("WATS_PROPERTIES") != null) {
                configName = System.getenv("WATS_PROPERTIES") + "/" + CONFIG_NAME;
            } else if (System.getProperty("WATS_PROPERTIES") != null) {
                configName = System.getProperty("WATS_PROPERTIES") + "/" + CONFIG_NAME;
            }
        }
        LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Tank Configuration location = " + configName)));
    }

    private String configPath = configName;
    private AgentConfig agentConfig;
    private VmManagerConfig vmManagerConfig;
    private ProductConfig productConfig;
    private LocationsConfig locationsConfig;
    private MailConfig mailConfig;
    private SecurityConfig securityConfig;
    private LogicStepConfig logicStepConfig;
    private ReportingConfig reportingConfig;
    private OidcSsoConfig oidcSsoConfig;

    /**
     * protected constructor to implement the singleton pattern
     * 
     * @param configPath
     *            the configuration path
     */
    TankConfig(String configPath) {
        this.configPath = configPath;
        readConfig();
    }

    /**
     * private constructor to implement the singleton pattern
     * 
     */
    public TankConfig() {
        readConfig();
    }

    /**
     * @return the Datafile storage root dir
     */
    public String getDataFileStorageDir() {
        return XMLConfig.getString(KEY_DATA_FILE_STORAGE, "/tmp/tank/datafiles");
    }

    /**
     * @return true if rest security is enabled
     */
    public boolean isRestSecurityEnabled() {
        return XMLConfig.getBoolean(KEY_REST_SECURITY_ENABLED, false);
    }

    /**
     * @return banner text
     */
    public String getTextBanner() {
        return XMLConfig.getString(KEY_BANNER_TEXT, "");
    }

    /**
     * @return true if rest security is enabled
     */
    public boolean isS3EncryptionEnabled() {
        return XMLConfig.getBoolean(KEY_ENCRYPT_S3, false);
    }

    /**
     * @return the Datafile storage root dir
     */
    public boolean getStandalone() {
        return XMLConfig.getBoolean(KEY_STANDALONE, false);
    }

    /**
     * @return the Datafile storage root dir
     */
    public String getTmpDir() {
        return XMLConfig.getString(KEY_TMP_FILE_STORAGE, "/tmp/tank/tmpfiles");
    }

    /**
     * @return the Datafile storage root dir
     */
    public String getJarDir() {
        return XMLConfig.getString(KEY_JAR_FILE_STORAGE, "/tmp/tank/jars");
    }

    /**
     * @return the Datafile storage root dir
     */
    public String getTimingDir() {
        return XMLConfig.getString(KEY_TIMING_FILE_STORAGE, "/tmp/tank/timing");
    }

    /**
     * @return the controller base url
     */
    public String getControllerBase() {
        return XMLConfig.getString(KEY_CONTROLLER_BASE, "http://tank.controller/");
    }

    /**
     * @return the controller base url
     */
    public String getInstanceName() {
        return XMLConfig.getString(KEY_INSTANCE_NAME, "prod");
    }

    /**
     * @return the vmManagerConfig
     */
    public VmManagerConfig getVmManagerConfig() {
        return vmManagerConfig;
    }

    /**
     * @return the vmManagerConfig
     */
    public AgentConfig getAgentConfig() {
        return agentConfig;
    }

    /**
     * @return the vmManagerConfig
     */
    public ProductConfig getProductConfig() {
        return productConfig;
    }

    /**
     * @return the vmManagerConfig
     */
    public LogicStepConfig getLogicStepConfig() {
        return logicStepConfig;
    }

    /**
     * @return the vmManagerConfig
     */
    public MailConfig getMailConfig() {
        return mailConfig;
    }

    /**
     * @return the locationsConfig
     */
    public LocationsConfig getLocationsConfig() {
        return locationsConfig;
    }

    /**
     * @return the vmManagerConfig
     */
    public SecurityConfig getSecurityConfig() {
        return securityConfig;
    }

    /**
     * @return the reportingConfig
     */
    public ReportingConfig getReportingConfig() {
        return reportingConfig;
    }

    /**
     * @return the oidcSsoConfig
     */
    public OidcSsoConfig getOidcSsoConfig() {
        return oidcSsoConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getConfigName() {
        return configPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initConfig(XMLConfiguration configuration) {
        vmManagerConfig = new VmManagerConfig(BaseCommonsXmlConfig.getChildConfigurationAt(configuration,
                KEY_VM_MANAGER_NODE));
        agentConfig = new AgentConfig(BaseCommonsXmlConfig.getChildConfigurationAt(configuration, KEY_AGENT_NODE));
        productConfig = new ProductConfig(
                BaseCommonsXmlConfig.getChildConfigurationAt(configuration, KEY_PRODUCTS_NODE));
        locationsConfig = new LocationsConfig(BaseCommonsXmlConfig.getChildConfigurationAt(configuration,
                KEY_LOCATIONS_NODE));
        mailConfig = new MailConfig(BaseCommonsXmlConfig.getChildConfigurationAt(configuration, KEY_MAIL_NODE));
        securityConfig = new SecurityConfig(BaseCommonsXmlConfig.getChildConfigurationAt(configuration,
                KEY_SECURITY_NODE));
        logicStepConfig = new LogicStepConfig(BaseCommonsXmlConfig.getChildConfigurationAt(configuration,
                KEY_LOGIC_STEP_NODE));
        reportingConfig = new ReportingConfig(BaseCommonsXmlConfig.getChildConfigurationAt(configuration,
                KEY_REPORTING_NODE));
        oidcSsoConfig = new OidcSsoConfig(BaseCommonsXmlConfig.getChildConfigurationAt(configuration, KEY_OIDC_SSO_NODE));
    }

}
