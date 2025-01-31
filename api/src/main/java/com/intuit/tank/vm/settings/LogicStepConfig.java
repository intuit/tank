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

/**
 * <logic-step> <insert-before></insert-before> <append-after></append-after> </logic-step>
 * 
 * @author dangleton
 * 
 */
public class LogicStepConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String KEY_INSERT_BEFORE = "insert-before";
    private static final String KEY_APPEND_AFTER = "append-after";

    private HierarchicalConfiguration config;

    public LogicStepConfig(@Nonnull HierarchicalConfiguration config) {
        this.config = config;
    }

    public String getInsertBefore() {
        return config.getString(KEY_INSERT_BEFORE, "");
    }

    public String getAppendAfter() {
        return config.getString(KEY_APPEND_AFTER, "");
    }

}
