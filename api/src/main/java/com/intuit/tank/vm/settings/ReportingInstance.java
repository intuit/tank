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
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * InstanceDescription
 * 
 * @author dangleton
 * 
 */
public class ReportingInstance extends InstanceDescription {

    public ReportingInstance(HierarchicalConfiguration config, HierarchicalConfiguration defaultInstance) {
        super(config, defaultInstance);
    }

    /**
     * 
     * @return
     */
    public VMRegion getRegion() {
        VMRegion ret = VMRegion.US_EAST;
        String region = get("region");
        if (region != null) {
            ret = VMRegion.valueOf(region);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public String getReportingMode() {
        String ret = null;
        String mode = get("reporting-mode");
        if (mode != null) {
            ret = mode;
        }
        return ret;
    }

    /**
     * @return whether to reuse stopped instances or to start new ones.
     */
    public boolean getReuseInstances() {
        boolean ret = false;
        String reuse = get("reuse-instances");
        if (reuse != null) {
            ret = Boolean.valueOf(reuse);
        }
        return ret;
    }

    /**
     * @return whether to reuse stopped instances or to start new ones. dependant
     */
    public boolean isRegionDependent() {
        boolean ret = false;
        String regionDependent = get("region-dependent");
        if (regionDependent != null) {
            ret = Boolean.valueOf(regionDependent);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public String getZone() {
        return get("zone");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
