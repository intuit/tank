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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.annotation.Nonnull;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * VmManagerConfig
 * 
 * @author dangleton
 * 
 */
public class AgentConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(AgentConfig.class);

    private static final String KEY_AGENT_DATA_FILE_STORAGE = "agent-data-file-storage";
    private static final String KEY_TANK_CLIENT_DEFUALT = "default-tank-client";

    private static final String KEY_MAX_BODY_REPORT_SIZE = "max-body-report-size";
    private static final String KEY_SSL_TIMEOUT = "ssl-timeout";
    private static final String KEY_MAX_RESPONSE_TIME = "max-response-time";
    private static final String KEY_TPS_PERIOD = "tps-period";

    private static final String KEY_VALID_MIME_TYPES = "valid-mime-types/mime-type-regex";
    private static final String KEY_TANK_HTTP_CLIENTS = "tank-http-clients/tank-client";
    private static final String KEY_POLL_TIME_MILIS = "status_report_interval_milis";
    private static final String KEY_MAX_FAILED_WAIT_TIME = "max-failed-wait-time";
    private static final String KEY_OVER_SIMULATION_MAX_TIME = "over-simulation-max-time";

    private static final String KEY_AGENT_PORT = "agent-port";
    private static final String KEY_AGENT_TOKEN = "agent-token";
    private static final String KEY_LOG_POST_RESPONSE = "log-post-response";
    private static final String KEY_LOG_POST_REQUEST = "log-post-request";
    private static final String KEY_LOG_VARIABLES = "log-variables";
    private static final String KEY_CONNECTION_TIMEOUT = "connection-timeout";

    private static final String KEY_REQUEST_HEADERS = "request-headers/header";
    // private static final String KEY_RESULT_PROVIDERS =
    // "result-providers/provider";
    private static final String KEY_HEADER_KEY = "@key";
    // private static final String KEY_DISPLAY_NAME = "@displayName";
    // private static final String KEY_DEFUALT = "@isDefault";

    private static final String KEY_DURATION_SIMULATION = "duration-simulation/simulation";
    private static final String KEY_MIN = "@min";
    private static final String KEY_MAX = "@max";
    private static final String KEY_FOR = "@for";
    private static final String KEY_NAME = "@name";

    private HierarchicalConfiguration config;
    private Set<String> validMimeTypes;
    private Map<String, String> resultsProviderMap;
    private Map<String, String> tankClientMap;
    private Map<String, String> requestHeaderMap;
    private Map<String, String> resultsTypeMap;
    private String defaultResultProvider;
    private Map<String, Range> minMaxMap;
    private Range defaultRange = new Range(50L, 300L);

    @SuppressWarnings("unchecked")
    public AgentConfig(@Nonnull HierarchicalConfiguration config) {
        this.config = config;
        validMimeTypes = new HashSet<String>();
        List<HierarchicalConfiguration> validMimes = config.configurationsAt(KEY_VALID_MIME_TYPES);
        if (validMimes != null) {
            for (HierarchicalConfiguration c : validMimes) {
                validMimeTypes.add(c.getString(""));
            }
        }
        tankClientMap = new HashMap<String, String>();
        // <tank-http-clients>
        // <tank-client name="Apache HttpClient
        // 3.1">com.intuit.tank.httpclient3.TankHttpClient3</tank-client>
        // <tank-client name="apache HttpClient
        // 4.5">com.intuit.tank.httpclient4.TankHttpClient4</tank-client>
        // <tank-http-clients>
        List<HierarchicalConfiguration> tankClients = config.configurationsAt(KEY_TANK_HTTP_CLIENTS);
        if (tankClients != null) {
            for (HierarchicalConfiguration c : tankClients) {
                tankClientMap.put(c.getString(KEY_NAME), c.getString(""));
            }
        }
        if (tankClientMap.isEmpty()) {
            tankClientMap.put("Apache HttpClient 3.1", "com.intuit.tank.httpclient3.TankHttpClient3");
            tankClientMap.put("Apache HttpClient 4.5", "com.intuit.tank.httpclient4.TankHttpClient4");
            tankClientMap.put("JDK Http Client", "com.intuit.tank.httpclientjdk.TankHttpClientJDK");
        }
        resultsProviderMap = new HashMap<String, String>();
        resultsTypeMap = new HashMap<String, String>();
        requestHeaderMap = new HashMap<String, String>();
        List<HierarchicalConfiguration> requestHeaders = config.configurationsAt(KEY_REQUEST_HEADERS);
        if (requestHeaders != null) {
            for (HierarchicalConfiguration c : requestHeaders) {
                String key = c.getString(KEY_HEADER_KEY);
                requestHeaderMap.put(key, c.getString(""));
            }
        }
        minMaxMap = new HashMap<String, Range>();
        List<HierarchicalConfiguration> durations = config.configurationsAt(KEY_DURATION_SIMULATION);
        if (durations != null) {
            for (HierarchicalConfiguration c : durations) {
                try {
                    String key = c.getString(KEY_FOR).trim();
                    int min = c.getInt(KEY_MIN);
                    int max = c.getInt(KEY_MAX);
                    Range r = new Range(min, max);
                    minMaxMap.put(key.toLowerCase(), r);
                } catch (Exception e) {
                    LOG.warn("Error parsing duration: " + e);
                }
            }
        }
        if (!minMaxMap.containsKey("post")) {
            minMaxMap.put("post", new Range(500L, 1000L));
        }
        if (!minMaxMap.containsKey("get")) {
            minMaxMap.put("get", new Range(50L, 300L));
        }
        if (!minMaxMap.containsKey("process")) {
            minMaxMap.put("process", new Range(10L, 50L));
        }
    }

    /**
     * @return the requestHeaderMap
     */
    public Map<String, String> getRequestHeaderMap() {
        return requestHeaderMap;
    }

    public Range getRange(String type) {
        Range ret = minMaxMap.get(type.toLowerCase());
        return ret != null ? ret : defaultRange;
    }

    /**
     * @return the defaultResultProvider
     */
    public String getDefaultResultProvider() {
        return defaultResultProvider;
    }

    /**
     * @return the resultsTypeMap
     */
    public Map<String, String> getResultsTypeMap() {
        return resultsTypeMap;
    }

    /**
     * @param resultsTypeMap
     *            the resultsTypeMap to set
     */
    public void setResultsTypeMap(Map<String, String> resultsTypeMap) {
        this.resultsTypeMap = resultsTypeMap;
    }

    /**
     * @param providerKey
     */
    public String getResultsProviderClass(String providerKey) {
        return resultsProviderMap.get(providerKey);
    }

    /**
     * 
     * @return
     */
    public Long getConnectionTimeout() {
        return config.getLong(KEY_CONNECTION_TIMEOUT, 40000L);
    }

    /**
     * 
     * @return
     */
    public int getTPSPeriod() {
        return config.getInt(KEY_TPS_PERIOD, 15);
    }

    /**
     * @return the agent port
     */
    public int getAgentPort() {
        return config.getInt(KEY_AGENT_PORT, 8090);
    }

    /**
     * @return the agent token
     */
    public String getAgentToken() {
        return config.getString(KEY_AGENT_TOKEN, "");
    }

    /**
     * 
     * @return whether to log each post request info.
     */
    public boolean getLogPostRequest() {
        return config.getBoolean(KEY_LOG_POST_REQUEST, false);
    }

    /**
     * 
     * @return whether to log each post request info.
     */
    public boolean getLogVariables() {
        return config.getBoolean(KEY_LOG_VARIABLES, false);
    }

    /**
     * 
     * @return
     */
    public boolean getLogPostResponse() {
        return config.getBoolean(KEY_LOG_POST_RESPONSE, false);
    }

    /**
     * @return the Datafile storage root dir
     */
    public String getAgentDataFileStorageDir() {
        return config.getString(KEY_AGENT_DATA_FILE_STORAGE, "/tmp");
    }

    /**
     * @return the SSL Timeout
     */
    public long getSSLTimeout() {
        return config.getLong(KEY_SSL_TIMEOUT, 90000L);
    }

    /**
     * @return the Max amount of time to wait for a response to a request
     */
    public long getMaxAgentResponseTime() {
        return config.getLong(KEY_MAX_RESPONSE_TIME, 5000L);
    }

    /**
     * @return the Max amount of time to wait for a response to a request
     */
    public long getMaxAgentWaitTime() {
        return config.getLong(KEY_MAX_FAILED_WAIT_TIME, 180000L);
    }

    /**
     * @return the Max amount of time to allow a thread to run after simulation
     *         time has been met
     */
    public long getOverSimulationMaxTime() {
        return config.getLong(KEY_OVER_SIMULATION_MAX_TIME, (1000 * 60 * 60 * 2));
    }

    /**
     * @return
     */
    public int getMaxBodyReportSize() {
        return config.getInt(KEY_MAX_BODY_REPORT_SIZE, 5000);
    }

    /**
     * Gets the valid regex patterns for text mime types.
     * 
     * @return
     */
    public Collection<String> getTextMimeTypeRegex() {
        return validMimeTypes;
    }
    
    
    /**
     * @return the Datafile storage root dir
     */
    public String getTankClientDefault() {
        return config.getString(KEY_TANK_CLIENT_DEFUALT, "Apache HttpClient 4.5");
    }
    
    /**
     * @return the Datafile storage root dir
     */
    public String getTankClientClassDefault() {
        String ret = getTankClientMap().get(getTankClientDefault());
        return ret != null ? ret : "com.intuit.tank.httpclient4.TankHttpClient4";
    }
    
    /**
     * @return the Datafile storage root dir
     */
    public String getTankClientName(String  clientClass) {
        return getTankClientMap().entrySet().stream().filter(entry -> entry.getValue().equals(clientClass)).findFirst().map(Entry::getKey).orElseGet(this::getTankClientDefault);
    }
    

    /**
     * @return the tankClientMap map of Tank Client name and class
     */
    public Map<String, String> getTankClientMap() {
        return tankClientMap;
    }

    /**
     * @param pollTime
     * @return
     */
    public long getStatusReportIntervalMilis(long pollTime) {
        return config.getLong(KEY_POLL_TIME_MILIS, pollTime);
    }

}
