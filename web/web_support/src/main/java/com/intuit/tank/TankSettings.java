package com.intuit.tank;

/*
 * #%L
 * JSF Support Beans
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
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * 
 * TAnkSettings applicationScoped settings like is standalone etc
 * 
 * @author dangleton
 * 
 */
@Named
@ApplicationScoped
public class TankSettings implements Serializable {

    private static final Logger LOG = LogManager.getLogger(TankSettings.class);
    private static final long serialVersionUID = 1L;
    private TankConfig config = new TankConfig();
    private List<VMRegion> regions;

    @PostConstruct
    public void init() {
        regions = new ArrayList<VMRegion>(config.getVmManagerConfig().getRegions());
    }

    /**
     * 
     * @return
     */
    public boolean isStandalone() {
        return config.getStandalone();
    }

    /**
     * 
     * @param region
     * @return
     */
    public boolean hasRegionConfigured(String region) {
        boolean ret = false;
        try {
            ret = regions.stream().anyMatch(r -> r.name().equalsIgnoreCase(region));
        } catch (Exception e) {
            LOG.error("Error finding region: " + e, e);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public List<VMRegion> getVmRegions() {
        return regions;
    }

    /**
     * 
     * @return
     */
    public String getControllerUrl() {
        return config.getControllerBase();
    }

}
