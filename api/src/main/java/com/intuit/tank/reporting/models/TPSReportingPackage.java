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

/**
 * TPSReportingPackage
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "TPSReportingPackage", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TPSReportingPackageType", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "jobId",
        "instanceId",
        "container"
})
public class TPSReportingPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "jobId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String jobId;

    @XmlElement(name = "instanceId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String instanceId;

    @XmlElement(name = "container", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private TPSInfoContainer container;

   
    /**
     * @FrameworkUseOnly
     */
    public TPSReportingPackage() {
    }

    

    public TPSReportingPackage(String jobId, String instanceId, TPSInfoContainer container) {
        super();
        this.jobId = jobId;
        this.instanceId = instanceId;
        this.container = container;
    }




    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }



    /**
     * @return the instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }



    /**
     * @return the container
     */
    public TPSInfoContainer getContainer() {
        return container;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this).append("jobId", jobId).append("instanceId", instanceId).toString();
    }

}
