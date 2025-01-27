package com.intuit.tank.runner.method;

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.data.AssignmentData;
import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.HDResponse;
import com.intuit.tank.harness.data.Header;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.ValidationData;
import com.intuit.tank.harness.functions.FunctionHandler;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.HttpRequestFactory;
import com.intuit.tank.http.HttpResponseFactory;
import com.intuit.tank.http.xml.XMLRequest;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.reporting.TankResultBuilder;
import com.intuit.tank.runner.ErrorContainer;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.script.RequestDataPhase;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.vm.api.enumerated.ValidationType;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ValidationUtil;

public class RequestRunner implements Runner {

    private static Logger LOG = LogManager.getLogger(RequestRunner.class);

    private String uniqueName;
    private BaseRequest baseRequest;
    private BaseResponse baseResponse;
    private RequestStep testStep;
    private Variables variables;

    private String host;
    private String loggingKey;
    private String method;
    private String path;
    private String port;
    private String reqFormat;
    private String protocol;
    private String respFormat;
    private TestStepContext tsc;

    private HDResponse response;

    /**
     * @param context
     */
    RequestRunner(TestStepContext context) {
        this.tsc = context;
        variables = context.getVariables();
        testStep = (RequestStep) context.getTestStep();
        response = testStep.getResponse();
        uniqueName = context.getUniqueName();
        initialize();
    }

    /**
     * initializes the variables
     */
    private void initialize() {
        HDRequest request = testStep.getRequest();
        HDResponse response = testStep.getResponse();
        // set proxy if proxyVariables are set
        String proxy = variables.getVariable("TANK_HTTP_PROXY");
        if (StringUtils.isNotBlank(proxy)) {
            try {
                String[] proxyInfo = StringUtils.split(":");
                int proxyPort = 80;
                if (proxy.startsWith("http://")) {
                    proxy = proxy.substring(7);
                } else if (proxy.startsWith("https://")) {
                    proxy = proxy.substring(8);
                    proxyPort = 443;
                }
                if (proxyInfo.length > 2) {
                    LOG.error("Proxy should be specified as proxyHost:proxyPort.");
                } else {
                    if (proxyInfo.length == 2) {
                        proxyPort = NumberUtils.toInt(proxyInfo[1]);
                    }
                    String proxyHost = proxyInfo[0];
                    tsc.getHttpClient().setProxy(proxyHost, proxyPort);
                    
                }
            } catch (Exception e) {
                LOG.error("Error setting proxy " + proxy + ": " + e, e);
            }
        }

        host = processHost(request.getHost());
        loggingKey = request.getLoggingKey();
        method = request.getMethod();
        path = processPath(request.getPath());
        reqFormat = StringUtils.isEmpty(request.getReqFormat()) ? "nvp" : request.getReqFormat();
        protocol = processProtocol(request.getProtocol());
        port = processPort(request.getPort());
        if (port == null) {
            port = "-1";
        }

        respFormat = StringUtils.isEmpty(response.getRespFormat()) ? "json" : response.getRespFormat();

        baseRequest = HttpRequestFactory.getHttpRequest(reqFormat, tsc.getHttpClient());
        baseRequest.setHost(host);
        baseRequest.setProtocol(protocol);
        baseRequest.setPort(port);
        baseRequest.setPath(path);

        List<Header> postDatas = request.getPostDatas();
        List<Header> queryStringPairs = request.getQueryString();
        List<Header> requestHeaders = request.getRequestHeaders();
        processQueryString(queryStringPairs);
        populateHeaders(requestHeaders);
        baseResponse = HttpResponseFactory.getHttpResponse(respFormat);
        populatePostData(postDatas);
        if (!StringUtils.isBlank(request.getPayload()) && !reqFormat.equalsIgnoreCase(ScriptConstants.NVP_TYPE)) {
            String payload = request.getPayload();
            baseRequest.setBody(variables.evaluate(payload));
        }
        LogUtil.getLogEvent().setRequest(baseRequest);
        //unset proxy TODO: add proxy and noproxy requ3est objects
        tsc.getHttpClient().setProxy(null, -1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intuit.tank.runner.method.Runner#execute()
     */
    @Override
    public String execute() {
        String validation = TankConstants.HTTP_CASE_PASS;
        if (APITestHarness.getInstance().getAgentRunData().getActiveProfile() == LoggingProfile.TRACE) {
            LOG.info(LogUtil.getLogMessage("Executing step..."));
        }
        if (checkPreValidations()) {
            if (LOG.isDebugEnabled()
                    || APITestHarness.getInstance().getAgentRunData().getActiveProfile() == LoggingProfile.VERBOSE
                    || APITestHarness.getInstance().getAgentRunData().getActiveProfile() == LoggingProfile.TRACE) {
                LOG.info(LogUtil.getLogMessage("Skipping request because of Pre-Validation."));
            }
            return TankConstants.HTTP_CASE_PASS;
        }
        try {
            if (method.equalsIgnoreCase("GET")) {
                baseRequest.doGet(baseResponse);
            } else if (method.equalsIgnoreCase("POST")) {
                baseRequest.doPost(baseResponse);
            } else if (method.equalsIgnoreCase("PUT")) {
                baseRequest.doPut(baseResponse);
            } else if (method.equalsIgnoreCase("PATCH")) {
                baseRequest.doPatch(baseResponse);
            } else if (method.equalsIgnoreCase("DELETE")) {
                baseRequest.doDelete(baseResponse);
            } else if (method.equalsIgnoreCase("OPTIONS")) {
                baseRequest.doOptions(baseResponse);
            }
        } catch (Throwable e) {
            LOG.error(
                    LogUtil.getLogMessage("Unexpected Exception executing request: " + e.toString(), LogEventType.IO),
                    e);
        }
        if (APITestHarness.getInstance().getTPSMonitor().isEnabled()) {
            if (!StringUtils.isEmpty(loggingKey)) {
                APITestHarness.getInstance().getTPSMonitor().addToMap(loggingKey, baseRequest);
            }
        }
        validation = processValidations(variables, "", baseResponse);
        LogUtil.getLogEvent().setValidationStatus(validation);
        if (APITestHarness.getInstance().getAgentRunData().getActiveProfile() == LoggingProfile.VERBOSE) {
            LOG.info(LogUtil.getLogMessage("Made Call ..."));
        }
        processVariables(variables, baseResponse);
        if (APITestHarness.getInstance().getAgentRunData().getActiveProfile() == LoggingProfile.TRACE) {
            LOG.info(LogUtil.getLogMessage("Variables Processed. Exiting ..."));
        }
        if (!APITestHarness.getInstance().isDebug()) {
            processPerfResponse(validation, uniqueName, Integer.valueOf(variables.getVariable("THREAD_ID")),
                    baseRequest,
                    baseResponse);
        }
        tsc.setRequest(baseRequest);
        tsc.setResponse(baseResponse);
        return validation;
    }

    /**
     * 
     * Processes the perf response, logs the results.
     * 
     * @param result
     * @param uniqueName
     * @param threadNum
     * @param req
     * @param resp
     */
    private void processPerfResponse(String result, String uniqueName, int threadNum, BaseRequest req, BaseResponse resp) {
        try {
            TankResultBuilder builder = new TankResultBuilder();
            builder.withJobId(APITestHarness.getInstance().getAgentRunData().getJobId())
                    .withInstanceId(APITestHarness.getInstance().getAgentRunData().getInstanceId());
            if (resp != null) {
                builder.withResponseTime((int) resp.getResponseTime());
                builder.withStatusCode(resp.getHttpCode());
                builder.withResponseSize(resp.getResponseBytes() != null ? resp.getResponseBytes().length : 0);
            }
            if (!result.equalsIgnoreCase(TankConstants.HTTP_CASE_PASS)) {
                builder.withError(true);
            }
            if (!StringUtils.isEmpty(loggingKey)) {
                builder.withRequestName(loggingKey);
                APITestHarness.getInstance().queueTimingResult(builder.build());
            }
            // else {
            // APITestHarness.getInstance().getTPMonitor().addToMap(TPSMonitor.NOT_LOGGED_KEY, req);
            // }
            if (tsc.getTimerMap().isActive()) {
                this.tsc.getTimerMap().addResult(builder.build());
            }
        } catch (Exception e) {
            LOG.warn("Error: req" + req + " : resp=" + resp + "  : " + e, e);
        }
    }

    /**
     * Process the validation
     * 
     * @return
     */
    private boolean checkPreValidations() {
        boolean ret = false;
        List<ValidationData> validations = new ArrayList<ValidationData>(response.getValidation().getHeaderValidation());
        validations.addAll(response.getValidation().getBodyValidation());
        validations.addAll(response.getValidation().getCookieValidation());
        validations = filterPhase(RequestDataPhase.POST_REQUEST, validations);
        for (ValidationData item : validations) {
            item = item.copy();
            if (ValidationUtil.isVariable(item.getKey()) && variables.variableExists(item.getKey())) {
                item.setKey(variables.getVariable(item.getKey()));
            } else if (ValidationUtil.isFunction(item.getKey())) {
                item.setKey(FunctionHandler.executeFunction(item.getKey(), variables));
            }
            if (ValidationUtil.isVariable(item.getValue()) && variables.variableExists(item.getValue())) {
                item.setValue(variables.getVariable(item.getValue()));
            } else if (ValidationUtil.isFunction(item.getValue())) {
                item.setValue(FunctionHandler.executeFunction(item.getValue(), variables));
            }
            item.setKey(variables.evaluate(item.getKey()));
            item.setValue(variables.evaluate(item.getValue()));
            if (item.getKey() != null && item.getValue() != null) {
                ret = evaluateResult(item.getKey(), item.getValue(), item.getCondition(),
                        variables);
            }
        }
        return ret;
    }

    /**
     * Process the validation
     * 
     * @return
     */
    private String processValidations(Variables variables, String uniqueName, BaseResponse reqResponse) {

        String testStepResult = TankConstants.HTTP_CASE_PASS;
        List<ValidationData> headerValidation = filterPhase(RequestDataPhase.PRE_REQUEST, response.getValidation()
                .getHeaderValidation());
        List<ValidationData> bodyValidation = filterPhase(RequestDataPhase.PRE_REQUEST, response.getValidation()
                .getBodyValidation());
        List<ValidationData> cookieValidation = filterPhase(RequestDataPhase.PRE_REQUEST, response.getValidation()
                .getCookieValidation());

        if (!headerValidation.isEmpty() || !bodyValidation.isEmpty() || !cookieValidation.isEmpty()) {
            String testCaseResult = TankConstants.HTTP_CASE_PASS;
            if (reqResponse.getHttpCode() == -1) {
                LOG.error(LogUtil.getLogMessage("Failure due to IO Error."));
                return TankConstants.HTTP_CASE_FAIL;
            }

            for (ValidationData item : headerValidation) {
                ValidationData original = item.copy();
                item = item.copy();
                if (ValidationUtil.isVariable(item.getValue()) && variables.variableExists(item.getValue())) {
                    item.setValue(variables.getVariable(item.getValue()));
                } else if (ValidationUtil.isFunction(item.getValue())) {
                    item.setValue(FunctionHandler.executeFunction(item.getValue(), variables));
                }
                item.setValue(variables.evaluate(item.getValue()));

                if (item.getKey().toUpperCase().startsWith(TankConstants.HTTP_CASE_HTTP)) {
                    testCaseResult = this.validateHTTP(original, item, variables, reqResponse.getHttpCode(),
                            reqResponse.getHttpMsg(), uniqueName);
                } else {
                    testCaseResult = this.validateHeader(original, item, variables, reqResponse, uniqueName);
                }
                if (!testCaseResult.equalsIgnoreCase(TankConstants.HTTP_CASE_PASS)) {
                    testStepResult = TankConstants.HTTP_CASE_FAIL;
                }
            }

            for (ValidationData item : bodyValidation) {
                ValidationData original = item.copy();
                item = item.copy();
                if (ValidationUtil.isVariable(item.getValue()) && variables.variableExists(item.getValue())) {
                    item.setValue(variables.getVariable(item.getValue()));
                } else if (ValidationUtil.isFunction(item.getValue())) {
                    item.setValue(FunctionHandler.executeFunction(item.getValue(), variables));
                }
                item.setValue(variables.evaluate(item.getValue()));

                testCaseResult = validateBody(original, item, variables, reqResponse, uniqueName);
                if (!testCaseResult.equalsIgnoreCase(TankConstants.HTTP_CASE_PASS)) {
                    testStepResult = TankConstants.HTTP_CASE_FAIL;
                }
            }

            for (ValidationData item : cookieValidation) {
                ValidationData original = item.copy();
                item = item.copy();
                if (ValidationUtil.isVariable(item.getValue()) && variables.variableExists(item.getValue())) {
                    item.setValue(variables.getVariable(item.getValue()));
                } else if (ValidationUtil.isFunction(item.getValue())) {
                    item.setValue(FunctionHandler.executeFunction(item.getValue(), variables));
                }
                item.setValue(variables.evaluate(item.getValue()));

                testCaseResult = validateCookie(original, item, variables, reqResponse, uniqueName);
                if (!testCaseResult.equalsIgnoreCase(TankConstants.HTTP_CASE_PASS)) {
                    testStepResult = TankConstants.HTTP_CASE_FAIL;
                }
            }
        }
        return testStepResult;
    }

    /**
     * @param phase
     * @param list
     * @return
     */
    private List<ValidationData> filterPhase(RequestDataPhase phase, List<ValidationData> list) {
        return list.stream().filter(d -> d.getPhase() != phase).collect(Collectors.toList());
    }

    /**
     * Populate the function data
     * 
     * @param value
     *            The function name
     * @return The function value
     */
    private String processFunction(String value, Variables variables) {
        String returnValue = value;
        if (FunctionHandler.validFunction(returnValue)) {
            returnValue = FunctionHandler.executeFunction(returnValue, variables);
        }
        return returnValue != null ? returnValue : "";
    }

    /**
     * Process the assignment defined in the response content and update the Variables set with updated values.
     * 
     * @param variables
     * @param reqResponse
     */
    private void processVariables(Variables variables, BaseResponse reqResponse) {
        List<AssignmentData> bodyVariable = response.getAssignment().getBodyVariable();
        List<AssignmentData> headerVariable = this.response.getAssignment().getHeaderVariable();
        List<AssignmentData> cookieVariable = this.response.getAssignment().getCookieVariable();
        if (reqResponse.getHttpCode() == -1) {
            return;
        }
        for (AssignmentData assignmentData : headerVariable) {
            String headerKey = stripEquals(assignmentData.getValue());
            if (ValidationUtil.isVariable(headerKey)) {
                headerKey = variables.getVariable(headerKey);
            } else if (ValidationUtil.isFunction(headerKey)) {

                headerKey = FunctionHandler.executeFunction(headerKey, variables);
            }
            headerKey = variables.evaluate(headerKey);
            String realValue = reqResponse.getHttpHeader(headerKey);
            if (realValue == null) {
                realValue = "";
            }
            LOG.debug("Setting variable " + assignmentData.getKey() + "=" + realValue);
            variables.addVariable(assignmentData.getKey(), realValue);
        }

        for (AssignmentData assignmentData : cookieVariable) {
            String cookieKey = stripEquals(assignmentData.getValue());
            if (ValidationUtil.isVariable(cookieKey)) {
                cookieKey = variables.getVariable(cookieKey);
            } else if (ValidationUtil.isFunction(cookieKey)) {
                cookieKey = FunctionHandler.executeFunction(cookieKey, variables);
            }
            cookieKey = variables.evaluate(cookieKey);
            String realValue = reqResponse.getCookie(cookieKey);
            if (realValue == null) {
                realValue = "";
            }
            variables.addVariable(assignmentData.getKey(), realValue);
            LOG.debug("Setting variable " + assignmentData.getKey() + "=" + realValue);
        }

        for (AssignmentData assignmentData : bodyVariable) {
            String value = stripEquals(assignmentData.getValue());
            String realValue = null;
            if (ValidationUtil.isFunction(value)) {
                realValue = FunctionHandler.executeFunction(value, variables, reqResponse.getResponseBody());
            } else {
                value = variables.evaluate(value);
                realValue = reqResponse.getValue(value);
            }
            if (realValue == null) {
                realValue = "";
            }
            variables.addVariable(assignmentData.getKey(), realValue);
            LOG.debug("Setting variable " + assignmentData.getKey() + "=" + realValue);
        }
    }

    private String stripEquals(String value) {
        if (value.length() > 0 && value.startsWith("=")) {
            value = value.substring(1);
        }
        return value;
    }

    /**
     * Populate the variable data
     * 
     * @param value
     *            The variable name
     * @return The variable value
     */
    private String processVariable(Variables variables, String value) {
        if (value == null) {
            return null;
        } else if (StringUtils.isEmpty(value)) {
            return "";
        }
        String returnValue = value;
        if (ValidationUtil.isVariable(returnValue)) {
            returnValue = variables.getVariable(returnValue);
        }
        return returnValue != null ? returnValue : "";
    }

    /**
     * Validate the raw HTTP data
     * 
     * @param item
     *            The validation item
     * @return TRUE if matches; FALSE otherwise
     */
    private String validateHTTP(ValidationData original, ValidationData item, Variables variables, int httpCode,
            String httpMsg, String uniqueName) {
        String actualValue = null;

        if (item.getKey().equalsIgnoreCase("HTTPRESPONSECODE")) {
            actualValue = "" + httpCode;
        } else if (item.getKey().equalsIgnoreCase("HTTPRESPONSEMESSAGE")) {
            actualValue = httpMsg;
        }

        boolean result = this.evaluateResult(actualValue, item.getValue(), item.getCondition(),
                variables);
        if (result) {
            return TankConstants.HTTP_CASE_PASS;
        }
        String msg = "Failed http validation: value = " + actualValue;
        LOG.error(LogUtil.getLogMessage(item.toString() + " " + msg, LogEventType.Validation, LoggingProfile.VERBOSE));
        tsc.addError(new ErrorContainer("HTTP_CODE", original, item, msg));
        return TankConstants.HTTP_CASE_FAIL;
    }

    /**
     * Validate an item against the response header
     * 
     * @param item
     *            The validation item
     * @return TRUE if matches; FALSE otherwise
     */
    private String validateHeader(ValidationData original, ValidationData item, Variables variables,
            BaseResponse reqResponse, String uniqueName) {
        String actualValue = reqResponse.getHttpHeader(item.getKey());
        boolean result = this.evaluateResult(actualValue, item.getValue(), item.getCondition(),
                variables);
        if (result) {
            return TankConstants.HTTP_CASE_PASS;
        }
        String msg = "Failed header validation: header value = " + actualValue;
        LOG.error(LogUtil.getLogMessage(item.toString() + " " + msg, LogEventType.Validation, LoggingProfile.VERBOSE));
        tsc.addError(new ErrorContainer("HEADER", original, item, msg));
        return TankConstants.HTTP_CASE_FAIL;
    }

    /**
     * Validate an item against the response body
     * 
     * @param item
     *            The validation item
     * @return TRUE if matches; FALSE otherwise
     */
    private String validateBody(ValidationData original, ValidationData item, Variables variables,
            BaseResponse reqResponse, String uniqueName) {
        String actualValue = reqResponse.getValue(item.getKey());
        LOG.debug("Body compare actual value: " + actualValue);
        boolean result = evaluateResult(actualValue, item.getValue(), item.getCondition(),
                variables);
        if (result) {
            return TankConstants.HTTP_CASE_PASS;
        }
        String msg = "Failed body validation: body value = " + actualValue;
        LOG.error(LogUtil.getLogMessage("Validation Failed: " + item.toString() + " " + msg, LogEventType.Validation,
                LoggingProfile.VERBOSE));
        tsc.addError(new ErrorContainer("BODY", original, item, msg));
        return TankConstants.HTTP_CASE_FAIL;
    }

    /**
     * Validate an item against the cookies
     * 
     * @param item
     *            The validation item
     * @param item
     * @return TRUE if matches; FALSE otherwise
     */
    private String validateCookie(ValidationData original, ValidationData item, Variables variables,
            BaseResponse reqResponse, String uniqueName) {
        String actualValue = reqResponse.getCookie(item.getKey());
        LOG.debug("Cookie compare actual value: " + actualValue);
        boolean result = evaluateResult(actualValue, item.getValue(), item.getCondition(),
                variables);
        if (result) {
            return TankConstants.HTTP_CASE_PASS;
        }
        String msg = "Failed cookie validation: cookie value = " + actualValue;
        LOG.error(LogUtil.getLogMessage(item.toString() + " " + msg, LogEventType.Validation, LoggingProfile.VERBOSE));
        tsc.addError(new ErrorContainer("COOKIE", original, item, msg));
        return TankConstants.HTTP_CASE_FAIL;
    }

    /**
     * Evaluate the validation information
     * 

     * @param actualValue
     *            The actual value from the response
     * @param expectedValue
     *            The expected value
     * @param condition
     *            The condition
     * @param variables
     * @return TRUE if matches; FALSE otherwise
     */
    private boolean evaluateResult(String actualValue, String expectedValue, String condition,
            Variables variables) {
        if (condition == null) {
            return false;
        }

        boolean testResult = true;
        expectedValue = processVariable(variables, expectedValue);
        expectedValue = processFunction(expectedValue, variables);
        expectedValue = variables.evaluate(expectedValue);

        ValidationType validationType = ValidationType.getValidationType(condition);

        if (validationType == ValidationType.empty) { // Process EMPTY
            testResult = StringUtils.isEmpty(actualValue);
        } else if (validationType == ValidationType.notempty) { // Process NOTEMPTY
            testResult = !StringUtils.isEmpty(actualValue);
        } else if (validationType == ValidationType.equals) { // Process Equals
            testResult = expectedValue.equalsIgnoreCase(actualValue);
        } else if (validationType == ValidationType.notequals) { // Process Equals
            testResult = !expectedValue.equalsIgnoreCase(actualValue);
        } else if (validationType == ValidationType.contains) { // Process Equals
            testResult = !StringUtils.isEmpty(actualValue)
                    && actualValue.toLowerCase().contains(expectedValue.toLowerCase());
        } else if (validationType == ValidationType.doesnotcontain) { // Process Equals
            testResult = actualValue == null || !actualValue.toLowerCase().contains(expectedValue.toLowerCase());
        } else if (validationType == ValidationType.lessthan) { // Process less than
            testResult = compare(actualValue, expectedValue) < 0;
        } else if (validationType == ValidationType.greaterthan) { // Process greater than
            testResult = compare(actualValue, expectedValue) > 0;
        } else {
            testResult = false;
        }
        return testResult;
    }

    private int compare(String actualValue, String expected) {
        if (NumberUtils.isCreatable(actualValue) && NumberUtils.isCreatable(expected)) {
            Double a = NumberUtils.toDouble(actualValue);
            Double e = NumberUtils.toDouble(expected);
            return a.compareTo(e);
        }
        return actualValue.compareTo(expected);
    }

    /**
     * Adds the post data to the base request
     * 
     * @param postDatas
     */
    private void populatePostData(List<Header> postDatas) {
        for (int i = postDatas.size(); --i >= 0;) {
            Header header = postDatas.get(i);
            String value = header.getValue();
            if ((baseRequest instanceof XMLRequest) && header.getKey().startsWith("ns:")) {
                baseRequest.setNamespace(header.getKey().substring(3), value);
                postDatas.remove(i);
            }
        }
        for (Header header : postDatas) {
            if (header.getAction().equalsIgnoreCase("remove")) {
                // this.request.item.getName());
            } else if (header.getAction().equalsIgnoreCase("add") || header.getAction().equalsIgnoreCase("set")) {
                String value = header.getValue();
                if (ValidationUtil.isFunction(value)) {
                    value = FunctionHandler.executeFunction(value, variables);
                } else if (ValidationUtil.isVariable(value)) {
                    value = variables.getVariable(value);
                }
                value = variables.evaluate(value);
                baseRequest.setKey(header.getKey(), value);
            }
        }
    }

    /**
     * Adds the headers to the baseRequest.
     * 
     * @param requestHeaders
     */
    private void populateHeaders(List<Header> requestHeaders) {
        for (Header header : requestHeaders) {
            if (header.getAction().equalsIgnoreCase("remove")) {
                baseRequest.removeHeader(header.getKey());
            } else if (header.getAction().equalsIgnoreCase("add") || header.getAction().equalsIgnoreCase("set")) {
                String value = header.getValue();
                if (ValidationUtil.isFunction(value)) {
                    value = FunctionHandler.executeFunction(value, variables);
                } else if (ValidationUtil.isVariable(value)) {
                    value = variables.getVariable(value);
                }
                value = variables.evaluate(value);
                if (header.getKey().equalsIgnoreCase("Content-Type")) {
                    baseRequest.setContentType(value.split(";")[0]);
                    if (value.split(";").length == 2 && (value.split(";")[1].contains("charset="))) {
                        baseRequest.setContentTypeCharSet(value.split("charset=")[1]);
                    }
                } else {
                    baseRequest.addHeader(header.getKey(), value);
                }
            }
        }
        // now add configured headers
        Map<String, String> headerMap = tsc.getParent().getHeaderMap();
        if (headerMap != null) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                baseRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Adds the query string to the baseRequest
     * 
     * @param queryString
     */
    private void processQueryString(List<Header> queryString) {
        for (Header header : queryString) {
            if (header.getAction().equalsIgnoreCase("remove")) {
                baseRequest.removeURLVariable(header.getKey());
            } else if (header.getAction().equalsIgnoreCase("add") || header.getAction().equalsIgnoreCase("set")) {
                String value = header.getValue();
                if (ValidationUtil.isFunction(value)) {
                    value = FunctionHandler.executeFunction(value, variables);
                } else if (ValidationUtil.isVariable(value)) {
                    value = variables.getVariable(value);
                }
                value = variables.evaluate(value);
                baseRequest.addURLVariable(header.getKey(), value);
            }
        }
    }

    /**
     * process the value of the host and returns the actual value if it is a function or a variable definition.
     * 
     * @param host
     * @return
     */
    private String processHost(String host) {
        if (host != null) {
            if (ValidationUtil.isFunction(host)) {
                host = FunctionHandler.executeFunction(host, variables);
            } else if (ValidationUtil.isVariable(host)) {
                host = variables.getVariable(host);
            }
            host = variables.evaluate(host);
        }
        return host;
    }

    /**
     * process the value of the path and returns the actual value if it is a function.
     * 
     * @param path
     * @return
     */
    private String processPath(String path) {
        if (ValidationUtil.isFunction(path)) {
            path = FunctionHandler.executeFunction(path, variables);
        } else if (ValidationUtil.isVariable(path)) {
            path = variables.getVariable(path);
        }
        if (StringUtils.isBlank(path)) {
            path = "/";
        }
        path = variables.evaluate(path);
        return path;
    }

    /**
     * process the value of the host and returns the actual value if it is a function definition.
     * 
     * @param port
     * @return
     */
    private String processPort(String port) {
        if (ValidationUtil.isFunction(port)) {
            port = FunctionHandler.executeFunction(port, variables);
        }
        return port;
    }

    private String processProtocol(String protocol) {
        if (protocol != null) {
            if (ValidationUtil.isFunction(protocol)) {
                protocol = FunctionHandler.executeFunction(protocol, variables);
            } else if (ValidationUtil.isVariable(protocol)) {
                protocol = variables.getVariable(protocol);
            }
            protocol = variables.evaluate(protocol);
        }
        return protocol;
    }

    public void setBaseResponse(BaseResponse newBaseResponse){
        this.baseResponse = newBaseResponse;
    }
}
