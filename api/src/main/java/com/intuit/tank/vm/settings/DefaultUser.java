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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DefaultUser 
 * 
 * @author dangleton
 * 
 */
public class DefaultUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String password;
    private String email;
    private boolean admin;
    private Set<String> groups = new HashSet<String>();

    @SuppressWarnings("unchecked")
    public DefaultUser(HierarchicalConfiguration config) {
        this.name = config.getString("name");
        this.email = config.getString("email");
        this.password = config.getString("password");
        this.admin = config.getBoolean("admin", false);
        for (HierarchicalConfiguration c : (List<HierarchicalConfiguration>) config.configurationsAt("group")) {
            groups.add(c.getString(""));
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * @return the groups
     */
    public Set<String> getGroups() {
        return groups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DefaultUser)) {
            return false;
        }
        DefaultUser o = (DefaultUser) obj;
        return new EqualsBuilder().append(o.getName(), getName()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getName()).toHashCode();
    }

}
