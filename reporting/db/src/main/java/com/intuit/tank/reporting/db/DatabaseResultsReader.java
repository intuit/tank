/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.reporting.db;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.persistence.databases.DataBaseFactory;
import com.intuit.tank.persistence.databases.DatabaseKeys;
import com.intuit.tank.reporting.api.PagedTimingResults;
import com.intuit.tank.reporting.api.ResultsReader;
import com.intuit.tank.reporting.api.TPSInfo;
import com.intuit.tank.reporting.databases.Attribute;
import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.results.TankResultBuilder;
import com.intuit.tank.vm.common.util.ReportUtil;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * DatabaseResultsReader
 * 
 * @author dangleton
 * 
 */
public class DatabaseResultsReader implements ResultsReader {

    private static final Logger LOG = LogManager.getLogger(DatabaseResultsReader.class);

    private IDatabase db;

    public DatabaseResultsReader() {
        db = DataBaseFactory.getDatabase();
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public List<TankResult> getAllTimingResults(String jobId) {
        Object nextToken = null;
        List<TankResult> ret = new ArrayList<TankResult>();
        do {
            PagedTimingResults results = getPagedTimingResults(jobId, nextToken);
            ret.addAll(results.getResults());
            nextToken = results.getNextToken();
        } while (nextToken != null);
        return ret;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public PagedTimingResults getPagedTimingResults(String jobId, Object nextToken) {
        String tableName = db.getDatabaseName(TankDatabaseType.timing, jobId);
        List<TankResult> results = new ArrayList<TankResult>();
        try {
            PagedDatabaseResult pagedItems = db.getPagedItems(tableName, nextToken, null, null, null, jobId);
            results = pagedItems.getItems().stream().map(this::ItemToTankResult).collect(Collectors.toList());
            nextToken = pagedItems.getNextToken();
        } catch (NullPointerException npe) { //Ignore
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
        return new PagedTimingResults(nextToken, results);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean hasTimingData(String jobId) {
        String tableName = db.getDatabaseName(TankDatabaseType.timing, jobId);
        return db.hasJobData(tableName, jobId);
    }

    private TankResult ItemToTankResult(Item item) {
        TankResultBuilder builder = TankResultBuilder.tankResult();
        for (Attribute attr : item.getAttributes()) {
            try {
                if (DatabaseKeys.TIMESTAMP_KEY.getShortKey().equals(attr.getName())) {
                    builder.withTimestamp(ReportUtil.parseTimestamp(attr.getValue()));
                } else if (DatabaseKeys.JOB_ID_KEY.getShortKey().equals(attr.getName())) {
                    builder.withJobId(attr.getValue());
                } else if (DatabaseKeys.INSTANCE_ID_KEY.getShortKey().equals(attr.getName())) {
                    builder.withInstanceId(attr.getValue());
                } else if (DatabaseKeys.LOGGING_KEY_KEY.getShortKey().equals(attr.getName())) {
                    builder.withRequestName(attr.getValue());
                } else if (DatabaseKeys.STATUS_CODE_KEY.getShortKey().equals(attr.getName())) {
                    builder.withStatusCode(Integer.parseInt(attr.getValue()));
                } else if (DatabaseKeys.RESPONSE_TIME_KEY.getShortKey().equals(attr.getName())) {
                    builder.withResponseTime(Integer.parseInt(attr.getValue()));
                } else if (DatabaseKeys.RESPONSE_SIZE_KEY.getShortKey().equals(attr.getName())) {
                    builder.withResponseSize(Integer.parseInt(attr.getValue()));
                } else if (DatabaseKeys.IS_ERROR_KEY.getShortKey().equals(attr.getName())) {
                    builder.withError(Boolean.valueOf(attr.getValue()));
                }
            } catch (Exception e) {
                LOG.warn("Error processing item: " + e);
            }
        }
        return builder.build();
    }

    @Override
    public void deleteTimingForJob(String jobId, boolean asynch) {

        String tableName = db.getDatabaseName(TankDatabaseType.timing, jobId);
        db.deleteForJob(tableName, jobId, asynch);

    }

    @Override
    public void config(HierarchicalConfiguration config) {
        // nothing to do
    }

    @Override
    public Map<Date, Map<String, TPSInfo>> getTpsMapForInstance(Date minDate, String jobId, String instanceId) {
        return getTpsMap(minDate, instanceId, jobId);
    }

    @Override
    public Map<Date, Map<String, TPSInfo>> getTpsMapForJob(Date minDate, String... jobId) {
        return getTpsMap(minDate, null, jobId);
    }

    private Map<Date, Map<String, TPSInfo>> getTpsMap(Date minDate, String instanceId, String... jobIds) {
        Map<Date, Map<String, TPSInfo>> ret = new HashMap<Date, Map<String, TPSInfo>>();
        try {
            String tpsTableName = new TankConfig().getInstanceName() + "_tps";
            if (db.hasTable(tpsTableName)) {

                if (jobIds != null && jobIds.length > 0) {

                    String dateString = null;
                    if (minDate != null) {
                        dateString = ReportUtil.getTimestamp(minDate);
                    }
                    List<Item> items = db.getItems(tpsTableName, dateString, null, instanceId, jobIds);
                    for (Item item : items) {
                        String loggingKey = null;
                        Date timestamp = null;
                        int transactions = 0;
                        int period = 0;

                        for (Attribute att : item.getAttributes()) {
                            if (DatabaseKeys.LOGGING_KEY_KEY.getShortKey().equals(att.getName())) {
                                loggingKey = att.getValue();
                            } else if (DatabaseKeys.TIMESTAMP_KEY.getShortKey().equals(att.getName())) {
                                try {
                                    timestamp = ReportUtil.parseTimestamp(att.getValue());
                                } catch (ParseException e) {
                                    LOG.error("Error processing timestamp " + att.getValue() + ":" + e);
                                }
                            } else if (DatabaseKeys.TRANSACTIONS_KEY.getShortKey().equals(att.getName())) {
                                transactions = Integer.parseInt(att.getValue());
                            } else if (DatabaseKeys.PERIOD_KEY.getShortKey().equals(att.getName())) {
                                period = Integer.parseInt(att.getValue());
                            }
                        }
                        TPSInfo info = new TPSInfo(timestamp, loggingKey, transactions, period);
                        Map<String, TPSInfo> map = ret.computeIfAbsent(timestamp, k -> new HashMap<String, TPSInfo>());
                        TPSInfo existing = map.get(loggingKey);
                        if (existing != null) {
                            info = existing.add(info);
                        }
                        map.put(loggingKey, info);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting TPS map: " + e, e);
        }
        return ret;
    }

}
