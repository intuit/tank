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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * 
 * InstanceType represents an agent instance.
 * 
 * @author dangleton
 * 
 */
public class VmInstanceType implements Serializable {

    private static final long serialVersionUID = 1L;

    // <type name="c3.large" cost=".105" users="500" cpus="2" ecus="7" mem="3.75" default="false" />
    private String name;
    private List<String> types;
    private String jvmArgs;
    private double cost;
    private double memory;
    private int users;
    private int cpus;
    private int ecus;
    private boolean isDefault;

    /**
     * protected constructor so only builder can create instances
     */
    protected VmInstanceType() {

    }

    /**
     * gets a builder for constructing InstanceType objects.
     * 
     * @return the builder
     */
    public static final Builder builder() {
        return new VmInstanceType.Builder();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the name
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * @return the memory
     */
    public double getMemory() {
        return memory;
    }

    /**
     * @return the users
     */
    public int getUsers() {
        return users;
    }

    /**
     * @return the cpus
     */
    public int getCpus() {
        return cpus;
    }

    /**
     * @return the ecus
     */
    public int getEcus() {
        return ecus;
    }

    /**
     * @return the jvmArgs
     */
    public String getJvmArgs() {
        return jvmArgs;
    }

    /**
     * @return the isDefault
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * InstanceType Builder fluent builder for InstanceTypes
     * 
     * @author dangleton
     * 
     */
    public static class Builder {
        private VmInstanceType instance;

        public Builder() {
            instance = new VmInstanceType();
        }

        public VmInstanceType build() {
            VmInstanceType ret = instance;
            instance = new VmInstanceType();
            return ret;
        }

        public Builder withName(String aValue) {
            instance.name = aValue;
            return this;
        }

        public Builder withTypes(List<Object> aValue) {
            instance.types = aValue.stream().map(Object::toString).toList();
            return this;
        }

        public Builder withJvmArgs(String aValue) {
            instance.jvmArgs = aValue;
            return this;
        }

        public Builder withCost(double aValue) {
            instance.cost = aValue;
            return this;
        }

        public Builder withMemory(double aValue) {
            instance.memory = aValue;
            return this;
        }

        public Builder withCpus(int aValue) {
            instance.cpus = aValue;
            return this;
        }

        public Builder withEcus(int aValue) {
            instance.ecus = aValue;
            return this;
        }

        public Builder withUsers(int aValue) {
            instance.users = aValue;
            return this;
        }

        public Builder withDefault(boolean aValue) {
            instance.isDefault = aValue;
            return this;
        }

    }

    public String getDisplay() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return name + " (types=" + types +
                " cpus=" + cpus +
                " ecus=" + ecus +
                " users=" + users +
                " memory=" + memory + "G" +
                " cost=" + nf.format(cost) + " / hour)";
    }

}
