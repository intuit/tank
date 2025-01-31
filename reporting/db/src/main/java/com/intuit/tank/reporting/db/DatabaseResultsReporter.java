/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.reporting.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.persistence.databases.DataBaseFactory;
import com.intuit.tank.persistence.databases.DatabaseKeys;
import com.intuit.tank.reporting.api.ResultsReporter;
import com.intuit.tank.reporting.api.TPSInfo;
import com.intuit.tank.reporting.api.TPSInfoContainer;
import com.intuit.tank.reporting.databases.Attribute;
import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.common.util.ReportUtil;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.logging.log4j.message.ObjectMessage;

/**
 * DatabaseResultsReporter
 * 
 * @author dangleton
 * 
 */
public class DatabaseResultsReporter implements ResultsReporter {

    private static final Logger LOG = LogManager.getLogger(DatabaseResultsReporter.class);

    private static final ThreadPoolExecutor EXECUTOR =
            new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(50), Executors.defaultThreadFactory(),
                    new ThreadPoolExecutor.DiscardOldestPolicy());

    private String tpsTableName;
    private String timingTableName;
    private IDatabase db;

    public DatabaseResultsReporter() {
        db = DataBaseFactory.getDatabase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendTpsResults(final String jobId, final String instanceId, final TPSInfoContainer container,
            boolean async) {

        Runnable task = () -> {
            try {
                List<Item> items = container.getTpsInfos().stream()
                        .map(info -> createItem(jobId, instanceId, info))
                        .collect(Collectors.toList());
                if (!items.isEmpty()) {
                    String tableName = getTpsTableName(db);
                    db.addItems(tableName, items, false);
                }
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
    public void sendTimingResults(String jobId, String instanceId, List<TankResult> results, boolean async) {
        String tableName = getTimingTableName(db, jobId);
        if (results.size() != 0 && tableName != null) {
            final List<TankResult> list;
            synchronized (results) {
                list = new ArrayList<TankResult>(results);
                results.clear();
            }
            DataBaseFactory.getDatabase().addTimingResults(tableName, list, async);
        }

    }

    private Item createItem(String jobId, String instanceId, TPSInfo info) {
        Item item = new Item();
        List<Attribute> attributes = new ArrayList<Attribute>();
        String ts = ReportUtil.getTimestamp(info.getTimestamp());
        addAttribute(attributes, DatabaseKeys.TIMESTAMP_KEY.getShortKey(), ts);
        addAttribute(attributes, DatabaseKeys.JOB_ID_KEY.getShortKey(), jobId);
        addAttribute(attributes, DatabaseKeys.INSTANCE_ID_KEY.getShortKey(), instanceId);
        addAttribute(attributes, DatabaseKeys.LOGGING_KEY_KEY.getShortKey(), info.getKey());
        addAttribute(attributes, DatabaseKeys.PERIOD_KEY.getShortKey(), Integer.toString(info.getPeriodInSeconds()));
        addAttribute(attributes, DatabaseKeys.TRANSACTIONS_KEY.getShortKey(), Integer.toString(info.getTransactions()));
        item.setAttributes(attributes);
        String name = instanceId
                + "_" + jobId
                + "_" + info.getKey()
                + "_" + ts;
        item.setName(name);
        return item;
    }

    public static void addAttribute(List<Attribute> attributes, String key, String value) {
        if (value == null) {
            value = "";
        }
        attributes.add(new Attribute(key, value));
    }

    @Override
    public void config(HierarchicalConfiguration config) {
        // nothing to configure
    }

    private String getTimingTableName(IDatabase db, String jobId) {
        if (StringUtils.isBlank(timingTableName)) {
            timingTableName = db.getDatabaseName(TankDatabaseType.timing, jobId);
            db.initNamespace(timingTableName);
        }
        return timingTableName;
    }

    private String getTpsTableName(IDatabase db) {
        if (StringUtils.isBlank(tpsTableName)) {
            tpsTableName = new TankConfig().getInstanceName() + "_tps";
            db.initNamespace(tpsTableName);
        }
        return tpsTableName;
    }

}
