/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.reporting;

import com.intuit.tank.reporting.models.TankResult;

import java.util.Date;

public class TankResultBuilder extends TankResultBuilderBase<TankResultBuilder> {
    public static TankResultBuilder tankResult() {
        return new TankResultBuilder();
    }

    public TankResultBuilder() {
        super(new TankResult());
    }

    public TankResult build() {
        return getInstance();
    }
}

class TankResultBuilderBase<GeneratorT extends TankResultBuilderBase<GeneratorT>> {
    private TankResult instance;

    protected TankResultBuilderBase(TankResult aInstance) {
        instance = aInstance;
    }

    protected TankResult getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withResponseTime(int aValue) {
        instance.setResponseTime(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withJobId(String aValue) {
        instance.setJobId(aValue);

        return (GeneratorT) this;
    }
    @SuppressWarnings("unchecked")
    public GeneratorT withInstanceId(String aValue) {
        instance.setInstanceId(aValue);
        
        return (GeneratorT) this;
    }
    @SuppressWarnings("unchecked")
    public GeneratorT withTimestamp(Date aValue) {
        instance.setTimeStamp(aValue);
        
        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withStatusCode(int aValue) {
        instance.setStatusCode(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withResponseSize(int aValue) {
        instance.setResponseSize(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withError(boolean aValue) {
        instance.setError(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withRequestName(String aValue) {
        instance.setRequestName(aValue);

        return (GeneratorT) this;
    }

}
