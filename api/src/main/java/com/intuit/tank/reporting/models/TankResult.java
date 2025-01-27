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
import java.util.Date;

/**
 * WatsResult represents a result to log or store to database.
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "TankResult", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TankResultType", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "jobId",
        "instanceId",
        "responseTime",
        "statusCode",
        "responseSize",
        "requestName",
        "error",
        "timeStamp"
})
public class TankResult implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "jobId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String jobId;

    @XmlElement(name = "instanceId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String instanceId;

    @XmlElement(name = "responseTime", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int responseTime;

    @XmlElement(name = "statusCode", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int statusCode;

    @XmlElement(name = "responseSize", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int responseSize;

    @XmlElement(name = "requestName", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String requestName;

    @XmlElement(name = "error", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private boolean error;

    @XmlElement(name = "timestamp", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Date timeStamp = new Date();

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId
     *            the jobId to set
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * @param instanceId
     *            the instanceId to set
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int connectionTime) {
        this.responseTime = connectionTime;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(int responseSize) {
        this.responseSize = responseSize;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestName() {
        return requestName;
    }

    /**
     * @return the timeStamp
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp
     *            the timeStamp to set
     */
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode
     *            the statusCode to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void add(TankResult result) {
        responseTime += result.getResponseTime();
        responseSize += result.getResponseSize();
        timeStamp = result.getTimeStamp();
        if (statusCode <= 0) {
            statusCode = result.getStatusCode();
        }
        if (result.error) {
            error = result.error;
        }
    }

	@Override
	public int compareTo(Object o) {
		return this.requestName.compareTo(((TankResult)o).getRequestName());
	}

}
