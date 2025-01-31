package com.intuit.tank.vm.settings;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Purpose:
 *
 * @author : atayal
 **/
public class ReportingConfigTest {
    private final HierarchicalConfiguration config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();
    private ReportingConfig reportingConfig = null;

    public ReportingConfigTest() throws ConfigurationException {
    }

    @BeforeEach
    public void init() {
        reportingConfig = new ReportingConfig(config);
    }

    @Test
    public void testReportingConfig() {
        assertEquals("com.intuit.tank.reporting.db.DatabaseResultsReporter", reportingConfig.getReporterClass());
        assertEquals("com.intuit.tank.reporting.db.DatabaseResultsReader", reportingConfig.getReaderClass());
        assertNull(reportingConfig.getProviderConfig());
    }
}
