package com.intuit.tank.persistence.databases;

/*
 * #%L
 * Reporting database support
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.reporting.databases.Attribute;
import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.common.util.MethodTimer;
import com.intuit.tank.vm.common.util.ReportUtil;
import com.intuit.tank.vm.settings.CloudCredentials;
import com.intuit.tank.vm.settings.CloudProvider;
import com.intuit.tank.vm.settings.TankConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator;
import software.amazon.awssdk.services.dynamodb.model.Condition;
import software.amazon.awssdk.services.dynamodb.model.ConsumedCapacity;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputDescription;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;
import software.amazon.awssdk.services.dynamodb.model.UpdateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

public class AmazonDynamoDatabaseDocApi implements IDatabase {

    private static final int MAX_NUMBER_OF_RETRIES = 5;
    private final TankConfig config = new TankConfig();
    private DynamoDbClient dynamoDbClient;

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(50), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    protected static final int BATCH_SIZE = 25;
    private static final long MAX_WRITE_UNITS = 1500L;

    private static Logger LOG = LogManager.getLogger(AmazonDynamoDatabaseDocApi.class);

    /**
     *
     */
    public AmazonDynamoDatabaseDocApi() {
        CloudCredentials creds = new TankConfig().getVmManagerConfig().getCloudCredentials(CloudProvider.amazon);
        if (creds != null && StringUtils.isNotBlank(creds.getKey()) && StringUtils.isNotBlank(creds.getKeyId())) {
            AwsCredentials credentials = AwsBasicCredentials.create(creds.getKeyId(), creds.getKey());
            this.dynamoDbClient = DynamoDbClient.builder().credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
        } else {
            this.dynamoDbClient = DynamoDbClient.builder().build();
        }
    }

    /**
     * 
     * @param dynamoDbClient
     */
    public AmazonDynamoDatabaseDocApi(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void initNamespace(String tableName) {
        try {
            if (!hasTable(tableName)) {
                LOG.info("Creating table: " + tableName);
                HierarchicalConfiguration resultsProviderConfig = config.getVmManagerConfig()
                        .getResultsProviderConfig();
                long readCapacity = getCapacity(resultsProviderConfig, "read-capacity", 10L);
                long writeCapacity = getCapacity(resultsProviderConfig, "write-capacity", 50L);
                ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
                attributeDefinitions.add(AttributeDefinition.builder().attributeName(
                        DatabaseKeys.JOB_ID_KEY.getShortKey()).attributeType(ScalarAttributeType.S).build());
                attributeDefinitions.add(AttributeDefinition.builder().attributeName(
                        DatabaseKeys.REQUEST_NAME_KEY.getShortKey()).attributeType(ScalarAttributeType.S).build());
                ProvisionedThroughput provisionedThroughput = ProvisionedThroughput.builder().readCapacityUnits(
                        readCapacity).writeCapacityUnits(writeCapacity).build();
                KeySchemaElement hashKeyElement = KeySchemaElement.builder().attributeName(
                        DatabaseKeys.JOB_ID_KEY.getShortKey()).keyType(KeyType.HASH).build();
                KeySchemaElement rangeKeyElement = KeySchemaElement.builder().attributeName(
                        DatabaseKeys.REQUEST_NAME_KEY.getShortKey()).keyType(KeyType.RANGE).build();
                CreateTableRequest request = CreateTableRequest.builder()
                        .tableName(tableName)
                        .keySchema(hashKeyElement, rangeKeyElement)
                        .attributeDefinitions(attributeDefinitions)
                        .provisionedThroughput(provisionedThroughput)
                        .build();

                CreateTableResponse response = dynamoDbClient.createTable(request);
                waitForStatus(tableName, TableStatus.ACTIVE);
                LOG.info("Created table: " + response.tableDescription().tableName());
            }
        } catch (Exception t) {
            LOG.error(t, t);
            throw new RuntimeException(t);
        }
    }

    private long getCapacity(HierarchicalConfiguration resultsProviderConfig, String key, long defaultValue) {
        if (resultsProviderConfig != null) {
            try {
                String string = resultsProviderConfig.getString(key);
                if (NumberUtils.isDigits(string)) {
                    return Long.parseLong(string);
                }
            } catch (Exception e) {
                LOG.error(e.toString());
            }
        }
        return defaultValue;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void removeNamespace(String tableName) {
        try {
            if (hasTable(tableName)) {
                LOG.info("Deleting table: " + tableName);
                DeleteTableRequest deleteTableRequest = DeleteTableRequest.builder().tableName(tableName).build();
                DeleteTableResponse response = dynamoDbClient.deleteTable(deleteTableRequest);
                LOG.info("Deleted table: " + response.tableDescription().tableName());
                waitForDelete(tableName);
            }
        } catch (Exception t) {
            LOG.error(t, t);
            throw new RuntimeException(t);
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean hasTable(String tableName) {
        String nextTableName = null;
        do {
            ListTablesResponse listTables =
                    dynamoDbClient.listTables(ListTablesRequest.builder().exclusiveStartTableName(nextTableName).build());
            for (String name : listTables.tableNames()) {
                if (tableName.equalsIgnoreCase(name)) {
                    return true;
                }
            }
            nextTableName = listTables.lastEvaluatedTableName();
        } while (nextTableName != null);
        return false;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void addTimingResults(final @Nonnull String tableName, final @Nonnull List<TankResult> results,
            boolean async) {
        if (!results.isEmpty()) {
            Runnable task = new Runnable() {
                public void run() {
                    MethodTimer mt = new MethodTimer(LOG, this.getClass(), "addTimingResults (" + results + ")");
                    List<WriteRequest> requests;
                    try {
                        requests = results.stream()
                                .map(result -> getTimingAttributes(result))
                                .map(item -> PutRequest.builder().item(item).build())
                                .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
                                .collect(Collectors.toList());
                        sendBatch(tableName, requests);
                    } catch (Exception t) {
                        LOG.error("Error adding results: " + t.getMessage(), t);
                        throw new RuntimeException(t);
                    }
                    mt.endAndLog();
                }
            };
            if (async) {
                EXECUTOR.execute(task);
            } else {
                task.run();
            }
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Set<String> getTables(String regex) {
        Set<String> result = new HashSet<String>();
        String nextTableName = null;
        do {
            ListTablesResponse listTables = dynamoDbClient.listTables(ListTablesRequest.builder()
                    .exclusiveStartTableName(nextTableName).build());
            for (String s : listTables.tableNames()) {
                if (s.matches(regex)) {
                    result.add(s);
                }
            }
            nextTableName = listTables.lastEvaluatedTableName();
        } while (nextTableName != null);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public PagedDatabaseResult getPagedItems(String tableName, Object nextToken, String minRange,
            String maxRange, String instanceId, String jobId) {
        Map<String, AttributeValue> lastKeyEvaluated = (Map<String, AttributeValue>) nextToken;
        ScanRequest.Builder scanRequest = ScanRequest.builder().tableName(tableName);
        Map<String, Condition> conditions = new HashMap<String, Condition>();
        if (jobId != null) {
            Condition jobIdCondition = Condition.builder().comparisonOperator(ComparisonOperator.EQ)
                    .attributeValueList(AttributeValue.builder().s(jobId).build()).build();
            conditions.put(DatabaseKeys.JOB_ID_KEY.getShortKey(), jobIdCondition);
        }
        if (StringUtils.isNotBlank(instanceId)) {
            // add a filter
            Condition filter = Condition.builder()
                    .comparisonOperator(ComparisonOperator.EQ)
                    .attributeValueList(AttributeValue.builder().s(instanceId).build())
                    .build();
            Map<String, Condition> map = new HashMap<>();
            map.put(DatabaseKeys.INSTANCE_ID_KEY.getShortKey(), filter);
            scanRequest.scanFilter(map);
        }
        Condition.Builder rangeKeyCondition = Condition.builder();
        if (minRange != null && maxRange != null) {
            rangeKeyCondition.comparisonOperator(ComparisonOperator.BETWEEN.toString())
                    .attributeValueList(AttributeValue.builder().s(minRange).build())
                    .attributeValueList(AttributeValue.builder().s(maxRange).build());
        } else if (minRange != null) {
            rangeKeyCondition.comparisonOperator(ComparisonOperator.GE.toString())
                    .attributeValueList(AttributeValue.builder().s(minRange).build());
        } else if (maxRange != null) {
            rangeKeyCondition.comparisonOperator(ComparisonOperator.LT.toString())
                    .attributeValueList(AttributeValue.builder().s(maxRange).build());
        } else {
            rangeKeyCondition = null;
        }
        if (rangeKeyCondition != null) {
            conditions.put(DatabaseKeys.REQUEST_NAME_KEY.getShortKey(), rangeKeyCondition.build());
        }
        scanRequest.scanFilter(conditions);
        scanRequest.exclusiveStartKey(lastKeyEvaluated);

        ScanResponse response = dynamoDbClient.scan(scanRequest.build());
        return new PagedDatabaseResult(
                response.items().stream().map(this::getItemFromResult).collect(Collectors.toList()),
                response.lastEvaluatedKey());
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<Item> getItems(String tableName, String minRange, String maxRange, String instanceId,
            String... jobIds) {
        List<Item> ret = new ArrayList<Item>();
        for (String jobId : jobIds) {
            Object lastKeyEvaluated = null;
            do {
                PagedDatabaseResult pagedItems = getPagedItems(tableName, lastKeyEvaluated, minRange, maxRange,
                        instanceId, jobId);
                ret.addAll(pagedItems.getItems());
                lastKeyEvaluated = pagedItems.getNextToken();
            } while (lastKeyEvaluated != null);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addItems(final String tableName, List<Item> itemList, final boolean asynch) {
        if (!itemList.isEmpty()) {
            final List<Item> items = new ArrayList<Item>(itemList);
            Runnable task = new Runnable() {
                public void run() {
                    MethodTimer mt = new MethodTimer(LOG, this.getClass(), "addItems (" + items + ")");
                    List<WriteRequest> requests;
                    try {
                        requests = items.stream()
                                .map(item -> itemToMap(item))
                                .map(toInsert -> PutRequest.builder().item(toInsert).build())
                                .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
                                .collect(Collectors.toList());
                        sendBatch(tableName, requests);
                    } catch (Exception t) {
                        LOG.error("Error adding results: " + t.getMessage(), t);
                        throw new RuntimeException(t);
                    }
                    mt.endAndLog();
                }
            };
            if (asynch) {
                EXECUTOR.execute(task);
            } else {
                task.run();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteForJob(final String tableName, final String jobId, final boolean asynch) {
        Runnable task = new Runnable() {
            public void run() {
                MethodTimer mt = new MethodTimer(LOG, this.getClass(), "deleteForJob (" + jobId + ")");

                List<Item> items = getItems(tableName, null, null, null, jobId);
                if (!items.isEmpty()) {
                    List<WriteRequest> requests = new ArrayList<WriteRequest>();
                    try {
                        for (Item item : items) {
                            String id = item.getAttributes().stream().filter(attr -> DatabaseKeys.REQUEST_NAME_KEY.getShortKey().equals(attr.getName())).findFirst().map(Attribute::getValue).orElse(null);
                            if (id != null) {
                                Map<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();
                                keyMap.put(DatabaseKeys.REQUEST_NAME_KEY.getShortKey(), AttributeValue.builder().s(id).build());
                                keyMap.put(DatabaseKeys.JOB_ID_KEY.getShortKey(), AttributeValue.builder().s(jobId).build());
                                DeleteRequest deleteRequest = DeleteRequest.builder().key(keyMap).build();
                                WriteRequest writeRequest = WriteRequest.builder().deleteRequest(deleteRequest).build();
                                requests.add(writeRequest);
                            }
                        }
                        sendBatch(tableName, requests);
                    } catch (Exception t) {
                        LOG.error("Error adding results: " + t.getMessage(), t);
                        throw new RuntimeException(t);
                    }
                }
                mt.endAndLog();
            }
        };
        if (asynch) {
            EXECUTOR.execute(task);
        } else {
            task.run();
        }

    }

    @Override
    public String getDatabaseName(TankDatabaseType type, String jobId) {
        return type.name() + "_" + new TankConfig().getInstanceName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasJobData(String tableName, String jobId) {
        if (hasTable(tableName)) {
            Map<String, Condition> keyConditions = new HashMap<String, Condition>();
            Condition jobIdCondition = Condition.builder().comparisonOperator(ComparisonOperator.EQ)
                    .attributeValueList(AttributeValue.builder().s(jobId).build()).build();
            keyConditions.put(DatabaseKeys.JOB_ID_KEY.getShortKey(), jobIdCondition);
            QueryRequest queryRequest = QueryRequest.builder().tableName(tableName).keyConditions(keyConditions)
                    .limit(1).build();

            return dynamoDbClient.query(queryRequest).count() > 0;
        }
        return false;
    }

    /**
     * @param attributeMap
     * @return
     */
    private Item getItemFromResult(Map<String, AttributeValue> attributeMap) {
        List<Attribute> attrs = new ArrayList<Attribute>();
        Item ret = new Item(null, attrs);
        for (Map.Entry<String, AttributeValue> item : attributeMap.entrySet()) {
            Attribute a = new Attribute(item.getKey(), item.getValue().s());
            attrs.add(a);
            if (a.getName().equalsIgnoreCase(DatabaseKeys.LOGGING_KEY_KEY.getShortKey())) {
                ret.setName(a.getValue());
            }
        }
        return ret;
    }

    private Map<String, AttributeValue> getTimingAttributes(TankResult result) {
        Map<String, AttributeValue> attributes = new HashMap<String, AttributeValue>();
        String timestamp = ReportUtil.getTimestamp(result.getTimeStamp());
        addAttribute(attributes, DatabaseKeys.TIMESTAMP_KEY.getShortKey(), timestamp);
        addAttribute(attributes, DatabaseKeys.REQUEST_NAME_KEY.getShortKey(), timestamp + "-"
                + UUID.randomUUID().toString());
        addAttribute(attributes, DatabaseKeys.JOB_ID_KEY.getShortKey(), result.getJobId());
        addAttribute(attributes, DatabaseKeys.LOGGING_KEY_KEY.getShortKey(), result.getRequestName());
        addAttribute(attributes, DatabaseKeys.STATUS_CODE_KEY.getShortKey(), String.valueOf(result.getStatusCode()));
        addAttribute(attributes, DatabaseKeys.RESPONSE_TIME_KEY.getShortKey(),
                String.valueOf(result.getResponseTime()));
        addAttribute(attributes, DatabaseKeys.RESPONSE_SIZE_KEY.getShortKey(), String.valueOf(result.getResponseSize()));
        addAttribute(attributes, DatabaseKeys.INSTANCE_ID_KEY.getShortKey(), String.valueOf(result.getInstanceId()));
        addAttribute(attributes, DatabaseKeys.IS_ERROR_KEY.getShortKey(), String.valueOf(result.isError()));
        return attributes;
    }

    private void addAttribute(Map<String, AttributeValue> attributes, String key, String value) {
        if (value == null) {
            value = "";
        }
        attributes.put(key, AttributeValue.builder().s(value).build());
    }

    private void addItemsToTable(String tableName, final BatchWriteItemRequest request) {

        boolean shouldRetry;
        int retries = 0;

        do {
            shouldRetry = false;
            try {
                BatchWriteItemResponse response = dynamoDbClient.batchWriteItem(request);
                if (response != null) {
                    try {
                        List<ConsumedCapacity> consumedCapacity = response.consumedCapacity();
                        for (ConsumedCapacity cap : consumedCapacity) {
                            LOG.info(cap.capacityUnits());
                        }
                    } catch (Exception e) {
                        // ignore this
                    }
                }
            } catch (AwsServiceException e) {
                if (e instanceof ProvisionedThroughputExceededException) {
                    try {
                        DescribeTableResponse response =
                                dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
                        ProvisionedThroughputDescription oldThroughput = response.table().provisionedThroughput();
                        LOG.info("ProvisionedThroughputExceeded throughput = " + oldThroughput);
                        ProvisionedThroughput newThroughput = ProvisionedThroughput.builder()
                                .readCapacityUnits(response.table().provisionedThroughput().readCapacityUnits())
                                .writeCapacityUnits(getIncreasedThroughput(response.table().provisionedThroughput()
                                        .readCapacityUnits())).build();

                        if (!oldThroughput.equals(newThroughput)) {
                            LOG.info("Updating throughput to " + newThroughput);
                            dynamoDbClient.updateTable(UpdateTableRequest.builder().provisionedThroughput(newThroughput).build());
                        }
                    } catch (Exception e1) {
                        LOG.error("Error increasing capacity: " + e, e);
                    }
                }
                int status = e.statusCode();
                if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR
                        || status == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    shouldRetry = true;
                    long delay = (long) (Math.random() * (Math.pow(4, retries++) * 100L));
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException iex) {
                        LOG.error("Caught InterruptedException exception", iex);
                    }
                } else {
                    LOG.error("Error writing to DB: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        } while (shouldRetry && retries < MAX_NUMBER_OF_RETRIES);

    }

    private Long getIncreasedThroughput(Long readCapacityUnits) {
        long ret = readCapacityUnits * 2;
        if (ret > MAX_WRITE_UNITS) {
            ret = MAX_WRITE_UNITS;
        }
        return ret;
    }

    private void waitForStatus(String tableName, TableStatus status) {
        LOG.info("Waiting for " + tableName + " to become " + status.toString() + "...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (10 * 60 * 1000);
        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(1000 * 2);
            } catch (Exception e) {
            }
            try {
                DescribeTableRequest request = DescribeTableRequest.builder().tableName(tableName).build();
                TableDescription tableDescription = dynamoDbClient.describeTable(request).table();
                String tableStatus = tableDescription.tableStatusAsString();
                LOG.debug("  - current state: " + tableStatus);
                if (tableStatus.equals(status.toString()))
                    return;
            } catch (AwsServiceException ase) {
                if (!ase.awsErrorDetails().errorCode().equalsIgnoreCase("ResourceNotFoundException"))
                    throw ase;
            }
        }

        throw new RuntimeException("Table " + tableName + " never went " + status.toString());
    }

    private void waitForDelete(String tableName) {
        LOG.info("Waiting for " + tableName + " to become deleted...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (10 * 60 * 1000);
        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(1000 * 2);
            } catch (Exception e) {
            }
            try {
                if (!hasTable(tableName)) {
                    return;
                }
            } catch (AwsServiceException ase) {
                if (!ase.awsErrorDetails().errorCode().equalsIgnoreCase("ResourceNotFoundException"))
                    throw ase;
            }
        }

        throw new RuntimeException("Table " + tableName + " never deleted");
    }

    /**
     * @param item
     * @return
     */
    private Map<String, AttributeValue> itemToMap(Item item) {
        Map<String, AttributeValue> attributes = new HashMap<String, AttributeValue>();
        for (Attribute attr : item.getAttributes()) {
            addAttribute(attributes, attr.getName(), attr.getValue());
        }
        if (!attributes.containsKey(DatabaseKeys.JOB_ID_KEY.getShortKey())) {
            throw new RuntimeException("Item does not contain a job ID");
        } else if (!attributes.containsKey(DatabaseKeys.REQUEST_NAME_KEY.getShortKey())) {
            AttributeValue attVal = attributes.get(DatabaseKeys.TIMESTAMP_KEY.getShortKey());
            String timestamp = attVal != null ? attVal.s() : ReportUtil.getTimestamp(new Date());
            if (attVal == null) {
                addAttribute(attributes, DatabaseKeys.TIMESTAMP_KEY.getShortKey(), timestamp);
            }
            addAttribute(attributes, DatabaseKeys.REQUEST_NAME_KEY.getShortKey(), timestamp + "-"
                    + UUID.randomUUID().toString());
        }

        return attributes;
    }

    /**
     * @param tableName
     * @param requests
     */
    private void sendBatch(final String tableName, List<WriteRequest> requests) {
        int numBatches = (int) Math.ceil(requests.size() / (BATCH_SIZE * 1D));
        for (int i = 0; i < numBatches; i++) {
            Map<String, List<WriteRequest>> requestItems = new HashMap<String, List<WriteRequest>>();
            List<WriteRequest> batch = requests.subList(i * BATCH_SIZE,
                    Math.min(i * BATCH_SIZE + BATCH_SIZE, requests.size()));
            requestItems.put(tableName, batch);
            addItemsToTable(tableName, BatchWriteItemRequest.builder().requestItems(requestItems).build());
        }
    }
}
