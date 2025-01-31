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

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;

import com.intuit.tank.harness.AmazonUtil;

/**
 * VmCredentials
 * 
 * @author dangleton
 * 
 */
public class CloudCredentials {

    private HierarchicalConfiguration config;

    /**
     * 
     * @param config
     */
    public CloudCredentials(HierarchicalConfiguration config) {
        this.config = config;
    }

    /**
     * @return the type
     */
    public CloudProvider getType() {
        return CloudProvider.valueOf(config.getString("@type", CloudProvider.amazon.name()));
    }

    /**
     * @return the keyId
     */
    public String getKeyId() {
        // try to get from property
        String key = config.getString("secret-key-id-property", "AWS_SECRET_KEY_ID");
        String ret = System.getProperty(key);
        if (StringUtils.isBlank(ret)) {
            ret = System.getenv(key);
        }
        if (StringUtils.isBlank(ret)) {
            // finally get straight from config
            ret = config.getString("secret-key-id");
        }
        return ret;
    }

    /**
     * @return the key
     */
    public String getKey() {
        // try to get from property
        String key = config.getString("secret-key-property", "AWS_SECRET_KEY");
        String ret = System.getProperty(key);
        if (StringUtils.isBlank(ret)) {
            ret = System.getenv(key);
        }
        if (StringUtils.isBlank(ret)) {
            // finally get straight from config
            ret = config.getString("secret-key");
        }
        return ret;
    }

    /**
     * @return the key
     */
    public String getProxyHost() {
        return config.getString("proxy-host");
    }

    /**
     * @return the key
     */
    public String getProxyPort() {
        return config.getString("proxy-port");
    }

}
