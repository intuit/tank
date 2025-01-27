package com.intuit.tank.harness;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.reporting.models.TPSInfo;
import com.intuit.tank.reporting.models.TPSInfoContainer;
import com.intuit.tank.vm.settings.TimeUtil;

public class TPSMonitor {

    @SuppressWarnings("unused")
    private static Logger LOG = LogManager.getLogger(TPSMonitor.class);

    private int period;

    private List<Counter> counters;
    private List<TPSInfo> infoList = new ArrayList<TPSInfo>();
    private Date minDate;

    public TPSMonitor(int period) {
        super();
        this.counters = Collections.synchronizedList(new ArrayList<TPSMonitor.Counter>());
        this.period = period;
    }

    public boolean isEnabled() {
        return period > 0;
    }

    /**
     * @return the tpsMap
     */
    public TPSInfoContainer getTPSInfo() {
        if (isEnabled()) {
            long now = TimeUtil.normalizeToPeriod(period, new Date()).getTime();
            long min = now;
            long max = 0;
            Map<Long, Map<String, Integer>> tpsMap = new ConcurrentHashMap<Long, Map<String, Integer>>();
            List<Counter> snapshot = new ArrayList<TPSMonitor.Counter>(counters);
            for (Counter counter : snapshot) {
                long entryTime = counter.time;
                if (now > (entryTime + (period * 4))) {
                    min = Math.min(min, entryTime);
                    max = Math.max(max, entryTime);
                    Map<String, Integer> map = tpsMap.computeIfAbsent(counter.time, k -> new HashMap<String, Integer>());
                    Integer integer = map.computeIfAbsent(counter.key, k -> 0);
                    map.put(counter.key, integer + 1);
                }
            }
            counters.removeAll(snapshot);

            for (Entry<Long, Map<String, Integer>> entry : tpsMap.entrySet()) {
                for (Entry<String, Integer> valueEntry : entry.getValue().entrySet()) {
                    infoList.add(
                            new TPSInfo(new Date(entry.getKey()), valueEntry.getKey(), valueEntry.getValue(), period)
                    );
                }
            }
            if (max == 0) { // no data yet
                return new TPSInfoContainer(new Date(min), new Date(min), period, infoList);
            } else {
                if (minDate == null) {
                    minDate = new Date(min);
                }
                return new TPSInfoContainer(minDate, new Date(max), period, infoList);
            }
        }
        return null;
    }

    public void addToMap(final String loggingKey, final BaseRequest req) {
        if (isEnabled()) {
            // LOG.info("Adding request " + req.getTimeStamp());
            new Thread( () -> {
                if (req != null && req.getTimeStamp() != null) {
                    counters.add(new Counter(loggingKey,
                            TimeUtil.normalizeToPeriod(period, req.getTimeStamp()))
                    );
                }
            }).start();
        }
    }

    private static class Counter {
        private String key;
        private long time;

        public Counter(String key, Date date) {
            super();
            this.key = key;
            this.time = date.getTime();
        }

    }

}
