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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;

import org.apache.commons.configuration2.HierarchicalConfiguration;

/**
 * SecurityConfig <code>
 * 
 * @author dangleton
 * 
 */
public class SecurityConfig {

    private Set<String> groups = new HashSet<String>();
    private Set<String> defaultGroups = new HashSet<String>();

    private Set<DefaultUser> users = new HashSet<DefaultUser>();

    private Map<String, List<String>> restrictionMap = new HashMap<String, List<String>>();

    @SuppressWarnings("unchecked")
    public SecurityConfig(@Nonnull HierarchicalConfiguration config) {
        if (config != null) {
            List<HierarchicalConfiguration> sizes = config.configurationsAt("groups/group");
            for (HierarchicalConfiguration sizeConfig : sizes) {
                String group = sizeConfig.getString("");
                if (sizeConfig.getBoolean("@isDefault", false)) {
                    defaultGroups.add(group);
                }
                groups.add(group);
            }

            List<HierarchicalConfiguration> restrictionConfigs = config.configurationsAt("restrictions/restriction");
            for (HierarchicalConfiguration restrictionConfig : restrictionConfigs) {
                List<HierarchicalConfiguration> restrictionGroups = restrictionConfig.configurationsAt("groups/group");
                String key = restrictionConfig.getString("@operation");
                List<String> value = restrictionGroups.stream().map(restrictionGroupConfig -> restrictionGroupConfig.getString("")).collect(Collectors.toList());
                restrictionMap.put(key, value);
            }

            List<HierarchicalConfiguration> userConfigs = config.configurationsAt("users/user");
            for (HierarchicalConfiguration userConfig : userConfigs) {
                users.add(new DefaultUser(userConfig));
            }
        }
    }

    /**
     * 
     * @return the groups
     */
    public Set<String> getGroups() {
        return groups;
    }

    /**
     * @return the defaultGroups
     */
    public Set<String> getDefaultGroups() {
        return defaultGroups;
    }

    public Map<String, List<String>> getRestrictionMap() {
        return restrictionMap;
    }

    /**
     * return if the group is a default group (should be added to all new users.
     * 
     * @param groupName
     * @return
     */
    public boolean isDefaultGroup(String groupName) {
        return defaultGroups.contains(groupName);
    }

    /**
     * @return the users
     */
    public Set<DefaultUser> getDefaultUsers() {
        return users;
    }

}
