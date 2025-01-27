/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.reporting;

import com.intuit.tank.reporting.models.TankResult;

import java.io.Serializable;
import java.util.List;

public class PagedTimingResults implements Serializable {

    private static final long serialVersionUID = 1L;

    private Object nextToken;
    private List<TankResult> results;

    public PagedTimingResults(Object nextToken, List<TankResult> results) {
        super();
        this.nextToken = nextToken;
        this.results = results;
    }

    /**
     * @return the nextToken
     */
    public Object getNextToken() {
        return nextToken;
    }

    /**
     * @return the results
     */
    public List<TankResult> getResults() {
        return results;
    }

    /**
     * returns if there are more results to fetch.
     * 
     * @return
     */
    public boolean hasMoreResults() {
        return nextToken != null;
    }
}
