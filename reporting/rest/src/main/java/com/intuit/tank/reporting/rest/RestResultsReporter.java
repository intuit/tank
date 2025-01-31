/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.reporting.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.client.v1.report.ReportServiceClientV1;
import com.intuit.tank.reporting.api.ResultsReporter;
import com.intuit.tank.reporting.api.TPSInfoContainer;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * DatabaseResultsReporter
 * 
 * @author dangleton
 * 
 */
public class RestResultsReporter implements ResultsReporter {

    private static final Logger LOG = LogManager.getLogger(RestResultsReporter.class);

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(50), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private ReportServiceClientV1 client;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendTpsResults(final String jobId, final String instanceId, final TPSInfoContainer container,
            boolean async) {
        Runnable task = () -> {
            try {
                getClient().postTpsResults(jobId, instanceId, container);
            } catch (Exception t) {
                LOG.error("Error adding results: " + t.getMessage(), t);
                throw new RuntimeException(t);
            }
        };
        if (async) {
            EXECUTOR.execute(task);
        } else {
            task.run();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendTimingResults(final String jobId, final String instanceId, List<TankResult> results, boolean asynch) {
        if (results != null && !results.isEmpty()) {
            final List<TankResult> list = new ArrayList<TankResult>(results);
            Runnable task = () -> {
                try {
                    getClient().postTimingResults(jobId, instanceId, list);
                } catch (Exception t) {
                    LOG.error("Error adding results: " + t.getMessage(), t);
                    throw new RuntimeException(t);
                }
            };
            if (asynch) {
                EXECUTOR.execute(task);
            } else {
                task.run();
            }
        }
    }

    @Override
    public void config(HierarchicalConfiguration config) {
        try {
        } catch (Exception e) {
            LOG.error("Config not correct. Using default options.");
        }
    }

    private ReportServiceClientV1 getClient() {
        if (client == null) {
            TankConfig config = new TankConfig();
            config.getControllerBase();
            client = new ReportServiceClientV1(config.getControllerBase());
        }
        return client;

    }

}
