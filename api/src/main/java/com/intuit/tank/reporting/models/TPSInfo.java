/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.reporting.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;

/**
 * TPSInfo class representing a tps for a specific key and a specific period
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "TPSInfo", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TPSInfoType", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "timestamp",
        "key",
        "transactions",
        "period"
})
public class TPSInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "timestamp", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Date timestamp;

    @XmlElement(name = "key", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String key;

    @XmlElement(name = "transactions", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int transactions;

    @XmlElement(name = "period", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int period;

    public TPSInfo() {
    }

    /**
     * 
     * @param timestamp
     * @param key
     * @param transactions
     * @param period
     */
    public TPSInfo(Date timestamp, String key, int transactions, int period) {
        super();
        this.timestamp = timestamp;
        this.key = key;
        this.transactions = transactions;
        this.period = period;
    }

    public TPSInfo add(TPSInfo toAdd) {
        return new TPSInfo(timestamp, key, transactions + toAdd.transactions, period);
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the transactions
     */
    public int getTransactions() {
        return transactions;
    }

    /**
     * @return the period
     */
    public int getPeriodInSeconds() {
        return period;
    }

    /**
     * 
     * @return
     */
    public int getTPS() {
        int tps = 0;
        if (period != 0) {
            tps = Math.round(transactions / period);
        }
        return tps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("key", key).append("period", period).append("tps", getTPS())
                .append("timestamp", timestamp).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TPSInfo)) {
            return false;
        }
        TPSInfo entity = (TPSInfo) obj;
        return new EqualsBuilder().append(timestamp, entity.timestamp).append(key, entity.key).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(21, 43).append(timestamp).append(key).toHashCode();
    }

}
