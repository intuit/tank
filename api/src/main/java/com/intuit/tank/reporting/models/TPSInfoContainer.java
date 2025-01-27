/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.reporting.models;


import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;

/**
 * TPSInfoContainer
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "TPSInfoContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TPSInfoContainerType", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "minTime",
        "maxTime",
        "period",
        "totalTps",
        "tpsInfos"
})
public class TPSInfoContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "minTime", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Date minTime;

    @XmlElement(name = "maxTime", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Date maxTime;

    @XmlElement(name = "period", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int period;

    @XmlElement(name = "totalTps", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int totalTps;

    @XmlElementWrapper(name = "tpsInfos", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "tps", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private List<TPSInfo> tpsInfos = new ArrayList<TPSInfo>();

    /**
     * @FrameworkUseOnly
     */
    public TPSInfoContainer() {
    }

    public TPSInfoContainer(Date minTime, Date maxTime, int period, List<TPSInfo> tpsInfos) {
        super();
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.tpsInfos = tpsInfos;
        this.period = period;
        if (tpsInfos != null) {
            Map<Date, Integer> transactionMap = new HashMap<Date, Integer>();
            for (TPSInfo info : tpsInfos) {
                Integer tps = transactionMap.get(info.getTimestamp());
                if (tps == null) {
                    tps = 0;
                }
                tps += info.getTransactions();
                transactionMap.put(info.getTimestamp(), tps);
            }
            if (!transactionMap.isEmpty()) {
                ArrayList<Date> dates = new ArrayList<Date>(transactionMap.keySet());
                Collections.sort(dates);
                Date date = dates.get(dates.size() - 1);
                totalTps = transactionMap.get(date) / period;
            }
        }
    }

    /**
     * @return the period
     */
    public int getPeriod() {
        return period;
    }

    /**
     * 
     * @return the total TPS
     */
    public int getTotalTps() {
        return totalTps;
    }

    /**
     * @return the tpsInfos
     */
    public List<TPSInfo> getTpsInfos() {
        return tpsInfos;
    }

    /**
     * @return the minTime
     */
    public Date getMinTime() {
        return minTime;
    }

    /**
     * @return the maxTime
     */
    public Date getMaxTime() {
        return maxTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("period", period).append("minTime", minTime).append("maxTime", maxTime)
                // .append(tpsInfos)
                .toString();
    }

}
