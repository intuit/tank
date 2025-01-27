/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.reporting;

import com.intuit.tank.vm.settings.ReportingConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ReportingFactory {

    private static final Logger LOG = LogManager.getLogger(ReportingFactory.class);

    private ReportingFactory() {

    }

    /**
     * 
     * @return
     */
    public static final ResultsReporter getResultsReporter() {
        ResultsReporter ret = null;
        try {
            ReportingConfig config = new TankConfig().getReportingConfig();
            String providerClass = config.getReporterClass();
            ret = (ResultsReporter) Class.forName(providerClass).newInstance();
            HierarchicalConfiguration providerConfig = config.getProviderConfig();
            ret.config(providerConfig);
        } catch (Exception e) {
            LOG.error("Error instantiating reporter");
            ret = new DummyResultsReporter();
        }
        return ret;
    }
    /**
     * 
     * @return
     */
    public static final ResultsReader getResultsReader() {
        ResultsReader ret = null;
        try {
            ReportingConfig config = new TankConfig().getReportingConfig();
            String providerClass = config.getReaderClass();
            ret = (ResultsReader) Class.forName(providerClass).newInstance();
            HierarchicalConfiguration providerConfig = config.getProviderConfig();
            ret.config(providerConfig);
        } catch (Exception e) {
            LOG.error("Error instantiating reporter");
//            ret = new DummyResultsReader();
        }
        return ret;
    }

}
