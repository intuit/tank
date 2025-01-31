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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Nonnull;

import org.apache.commons.configuration2.HierarchicalConfiguration;

/**
 * ProductConfig
 * 
 * @author dangleton
 * 
 */
public class ProductConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String KEY_PRODUCT = "product";

    private List<SelectableItem> products;
    private HierarchicalConfiguration config;

    public ProductConfig(@Nonnull HierarchicalConfiguration config) {
        this.config = config;
        initConfig();
    }

    @SuppressWarnings("unchecked")
    private void initConfig() {
        products = new ArrayList<SelectableItem>();
        if (config != null) {
            List<HierarchicalConfiguration> productEntries = config.configurationsAt(KEY_PRODUCT);
            for (HierarchicalConfiguration c : productEntries) {
                products.add(new SelectableItem(c.getString(""), c.getString("@name")));
            }
        }
        Collections.sort(products);
    }

    /**
     * @return the products
     */
    public List<SelectableItem> getProducts() {
        return new ArrayList<SelectableItem>(products);
    }

}
