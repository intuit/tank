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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * VmManagerConfig
 * 
 * @author dangleton
 * 
 */
public class VmManagerConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(VmManagerConfig.class);

    private HierarchicalConfiguration config;
    private Set<String> reservedElasticIps = new HashSet<String>();
    private Map<CloudProvider, CloudCredentials> credentialsMap = new HashMap<CloudProvider, CloudCredentials>();
    private Map<VMRegion, Map<VMImageType, InstanceDescription>> regionMap = new HashMap<VMRegion, Map<VMImageType, InstanceDescription>>();
    private List<VmInstanceType> instanceTypes = new ArrayList<VmInstanceType>();
    private List<InstanceTag> tagList = new ArrayList<InstanceTag>();

    /**
     * 
     * @param config
     */
    @SuppressWarnings("unchecked")
    public VmManagerConfig(@Nonnull HierarchicalConfiguration config) {
        this.config = config;
        if (this.config != null) {
            List<HierarchicalConfiguration> creds = config.configurationsAt("credentials");
            if (creds != null) {
                for (HierarchicalConfiguration credentialsConfig : creds) {
                    CloudCredentials cloudCredentials = new CloudCredentials(credentialsConfig);
                    credentialsMap.put(cloudCredentials.getType(), cloudCredentials);
                }
            }
            
            List<HierarchicalConfiguration> tags = config.configurationsAt("tags/tag");
            if (tags != null) {
                for (HierarchicalConfiguration tagsConfig : tags) {
                    InstanceTag tag = new InstanceTag(tagsConfig.getString("@name"), tagsConfig.getString(""));
                    if (tag.isValid()) {
                        tagList.add(tag);
                    }
                }
            }

            HierarchicalConfiguration defaultInstance = config.configurationAt("default-instance-description");
            List<HierarchicalConfiguration> regionConfigs = config.configurationsAt("instance-descriptions");
            if (regionConfigs != null) {
                for (HierarchicalConfiguration regionConfig : regionConfigs) {
                    VMRegion region = VMRegion.valueOf(regionConfig.getString("@region"));
                    Map<VMImageType, InstanceDescription> instanceMap = new HashMap<VMImageType, InstanceDescription>();
                    regionMap.put(region, instanceMap);
                    List<HierarchicalConfiguration> instanceConfigs = regionConfig
                            .configurationsAt("instance-description");
                    for (HierarchicalConfiguration instanceConfig : instanceConfigs) {
                        InstanceDescription description = new InstanceDescription(instanceConfig, defaultInstance);
                        instanceMap.put(description.getType(), description);
                    }

                }
            }

            // get reserved elastic ips
            List<HierarchicalConfiguration> eipConfig = config.configurationsAt("reserved-elastic-ips/eip");
            if (eipConfig != null) {
                for (HierarchicalConfiguration eip : eipConfig) {
                    reservedElasticIps.add(eip.getString(""));
                }
            }
            // get instance type definitions
            List<HierarchicalConfiguration> instanceTypesConfig = config.configurationsAt("instance-types/type");
            instanceTypes = new ArrayList<VmInstanceType>();
            if (instanceTypesConfig != null) {
                for (HierarchicalConfiguration instanceTypeConfig : instanceTypesConfig) {
                    // example: <type name="m.large" types="m6g.large" cost=".105" users="500" cpus="2" ecus="7" mem="3.75" />
                    VmInstanceType type = VmInstanceType.builder()
                            .withName(instanceTypeConfig.getString("@name"))
                            .withTypes(instanceTypeConfig.getList("@types"))
                            .withCost(instanceTypeConfig.getDouble("@cost", 0D))
                            .withMemory(instanceTypeConfig.getDouble("@mem", 0D))
                            .withJvmArgs(instanceTypeConfig.getString("@jvmArgs"))
                            .withEcus(instanceTypeConfig.getInt("@ecus", 0))
                            .withCpus(instanceTypeConfig.getInt("@cpus", 0))
                            .withDefault(instanceTypeConfig.getBoolean("@default", false))
                            .withUsers(instanceTypeConfig.getInt("@users", 0)).build();
                    instanceTypes.add(type);

                }
            }
        }
    }

    /**
     * @return the instanceTypes
     */
    public List<VmInstanceType> getInstanceTypes() {
        return instanceTypes;
    }

    /**
     * @param instanceTypes
     *            the instanceTypes to set
     */
    public void setInstanceTypes(List<VmInstanceType> instanceTypes) {
        this.instanceTypes = instanceTypes;
    }

    /**
     * 
     * @return
     */
    @Nonnull
    public Set<String> getReservedElasticIps() {
        return reservedElasticIps;
    }

    /**
     * 
     * @param region
     * @return
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public Map<VMImageType, InstanceDescription> getInstancesForRegion(@Nonnull VMRegion region) {
        Map<VMImageType, InstanceDescription> result = regionMap.get(region);
        return (Map<VMImageType, InstanceDescription>) (result != null ? result : Collections.emptyMap());
    }

    /**
     * 
     * @param region
     * @param type
     * @return
     */
    @Nullable
    public InstanceDescription getInstanceForRegionAndType(@Nonnull VMRegion region, @Nonnull VMImageType type) {
        return getInstancesForRegion(region).get(type);
    }

    /**
     * 
     * @return
     */
    @Nonnull
    public List<VMRegion> getConfiguredRegions() {
        return regionMap.keySet().stream().filter(r -> getInstanceForRegionAndType(r, VMImageType.AGENT) != null).collect(Collectors.toList());
    }

    /**
     * 
     * @param name
     * @return
     */
    public VmInstanceType getInstanceType(String name) {
        VmInstanceType ret = null;
        for (VmInstanceType type : instanceTypes) {
            if (type.getName().equalsIgnoreCase(name)) {
                ret = type;
                break;
            }
            if (type.isDefault()) {
                ret = type;
            }
        }
        return ret;
    }

    /**
     * 
     * @param defaultMills
     * @return
     */
    public long getMaxAgentStartMills(long defaultMills) {
        String string = config.getString("watchdog/max-time-for-agent-start");
        if (string != null) {
            try {
                return TimeUtil.parseTimeString(string);
            } catch (Exception e) {
                LOG.error(e.toString());
            }
        }
        return defaultMills;
    }

    /**
     * 
     * @return the provider classname. should be an instance of IDatabase
     */
    public String getResultsProvider() {
        String string = config.getString("results/provider");
        if (string != null) {
            try {
                return string;
            } catch (Exception e) {
                LOG.error(e.toString());
            }
        }
        return "com.intuit.tank.persistence.databases.AmazonDynamoDatabaseDocApi";
    }

    /**
     * @return the read capacity for dynamoDB tables.
     */
    public HierarchicalConfiguration getResultsProviderConfig() {

        return config.configurationAt("results/config");
    }

    /**
     * @return the write capacity for dynamoDB tables.
     */
    public long getResultsWriteCapacity() {
        String string = config.getString("results/write-capacity");
        if (string != null) {
            try {
                return Long.parseLong(string);
            } catch (Exception e) {
                LOG.error(e.toString());
            }
        }
        return 50L;
    }

    /**
     * 
     * @param defaultMills
     * @return
     */
    public long getMaxAgentReportMills(long defaultMills) {
        String string = config.getString("watchdog/max-time-for-agent-report");
        if (string != null) {
            try {
                return TimeUtil.parseTimeString(string);
            } catch (Exception e) {
                LOG.error(e.toString());
            }
        }
        return defaultMills;
    }

    /**
     * 
     * @param defaultMills
     * @return
     */
    public long getWatchdogSleepTime(long defaultMills) {
        String string = config.getString("watchdog/sleep-time-between-check");
        if (string != null) {
            try {
                return TimeUtil.parseTimeString(string);
            } catch (Exception e) {
                LOG.error(e.toString());
            }
        }
        return defaultMills;
    }

    /**
     * 
     * @param defaultResult
     * @return
     */
    public boolean isUseElasticIps() {
        return config.getBoolean("use-agent-elastic-ips", false);
    }

    /**
     * 
     * @param defaultResult
     * @return
     */
    public int getMaxRestarts(int defaultResult) {
        return config.getInt("watchdog/max-restarts", defaultResult);
    }

    /**
     * Gets the credentials for the specified cloud implementation.
     * 
     * @param provider
     *            the provider to fetch for.
     * @return the credentials or null if no credentials are configured forthe provider.
     */
    @Nullable
    public CloudCredentials getCloudCredentials(@Nonnull CloudProvider provider) {
        return credentialsMap.get(provider);
    }

    /**
     * 
     * @return
     */
    InstanceDescriptionDefaults getInstanceDefaults() {
        SubnodeConfiguration c = config.configurationAt("default-instance-description");
        return new InstanceDescriptionDefaults(c, c);
    }

    public VMRegion getDefaultRegion() {
        VMRegion ret = VMRegion.US_EAST;
        String string = config.getString("default-region");
        if (string != null) {
            try {
                ret = VMRegion.valueOf(string);
            } catch (Exception e) {
                LOG.warn("Cannot parse " + string + " into VMRegion");
            }
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Set<VMRegion> getRegions() {
        return regionMap.keySet();
    }

    public List<InstanceTag> getTags() {
        return tagList;
    }

}
