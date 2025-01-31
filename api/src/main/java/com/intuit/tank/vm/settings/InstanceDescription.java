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

import com.intuit.tank.vm.api.enumerated.VMImageType;

/**
 * InstanceDescription
 * 
 * @author dangleton
 * 
 */
public class InstanceDescription extends InstanceDescriptionDefaults {

    public InstanceDescription(HierarchicalConfiguration config, HierarchicalConfiguration defaultInstance) {
        super(config, defaultInstance);
    }

    /**
     * @return the type
     */
    public VMImageType getType() {
        return VMImageType.valueOf(get("@name"));
    }

    /**
     * @return the ami
     */
    public String getAmi() {
        return get("ami");
    }

    /**
     * @return the ssm-ami
     */
    public String getSSMAmi() {
        return get("ssm-ami");
    }

    /**
     * @return the publicIp
     */
    public String getPublicIp() {
        return get("public-ip");
    }

}
