package com.intuit.tank.http;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class BaseResponse {

    private static final char NEWLINE = '\n';
    private static final Logger LOG = LogManager.getLogger(BaseResponse.class);

    protected String response;
    protected long responseTime = -1;
    protected long proxyResponseTime = -1;
    protected int httpCode = -1;
    protected String rspMessage = "";
    protected HashMap<String, String> headers = new HashMap<String, String>();
    protected HashMap<String, String> cookies = new HashMap<String, String>();
    protected String responseLogMsg;

    protected byte[] responseByteArray;

    /**
     * 
     */
    public abstract String getValue(String key); // returns value for the given
                                                 // path

    /**
     * @return the responseLogMsg
     */
    public String getLogMsg() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("RESPONSE HTTP CODE: ").append(this.httpCode).append(NEWLINE)
                    .append("RESPONSE HTTP MSG: ").append(this.rspMessage).append(NEWLINE)
                    .append("RESPONSE TIME: ").append(responseTime).append(NEWLINE)
                    .append("PROXY RESPONSE TIME: ").append(proxyResponseTime).append(NEWLINE)
                    .append("RESPONSE SIZE: ").append(getResponseSize()).append(NEWLINE);
            for (Entry<String, String> mapEntry : headers.entrySet()) {
                sb.append("RESPONSE HEADER: ")
                        .append(mapEntry.getKey()).append(" = ")
                        .append(mapEntry.getValue()).append(NEWLINE);
            }
            for (Entry<String, String> entry : cookies.entrySet()) {
                sb.append("RESPONSE COOKIE: ")
                        .append(entry.getKey()).append(" = ")
                        .append(entry.getValue()).append(NEWLINE);
            }
            if (response != null) {
                String contentType = this.headers.get("content-type");
                if (isDataType(contentType)) {
                    sb.append("RESPONSE BODY: ").append(this.response).append(NEWLINE);
                } else {
                    sb.append("RESPONSE BODY: not logged because ")
                            .append(contentType).append(" is not a content-type.").append(NEWLINE);
                }
            }
            this.responseLogMsg = sb.toString();
        } catch (Exception ex) {
            LOG.error("Error processing response: {}", ex.getMessage(), ex);
        }
        return responseLogMsg;
    }

    public String convertToCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.httpCode).append(",");
        sb.append(this.rspMessage).append(",");
        sb.append(responseTime).append(",");
        sb.append(proxyResponseTime).append(",");
        sb.append(getResponseSize()).append(",");
        headers.forEach((key, value) -> sb.append(key).append(" = ").append(value.replace(",", "")).append(","));
        cookies.forEach((key, value) -> sb.append(key).append(" = ").append(value).append(","));
        return sb.toString();
    }

    /**
     * Common codes are 200 OK, 202 accepted, 204 no content, 400 bad request,
     * 404 not found, 500 internal server error, 503 Service Unavailable
     * 
     * @return int - The integer representing the HTTP code for this response.
     *         (200, 404, 503 etc)
     */
    public int getHttpCode() {
        return this.httpCode;
    }

    public void setHttpCode(int code) {
        this.httpCode = code;
    }

    public void setHttpMessage(String msg) {
        this.rspMessage = msg;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key.toLowerCase(), value);
    }

    /**
     * 
     * @return String - The response message associated with this response.
     */
    public String getHttpMsg() {
        return this.rspMessage;
    }

    /**
     * 
     * @param header
     *            String - The key for the header to lookup in the header
     *            hashmap.
     * @return The value associated with the key, or null if it doesn't exist.
     */
    public String getHttpHeader(String header) {
        return this.headers.get(header.toLowerCase());
    }

    /**
     * Returns entire header as a Hashmap for this response.
     * 
     * @return a HashMap<String,String> of the entire response header.
     */
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * Sets the response time.
     * 
     * @param time
     *            - long
     */
    public void setResponseTime(long time) {
        this.responseTime = time;
    }

    /**
     * Gets the response time for this response. The returned value is whatever
     * was set by setResponseTime().
     * 
     * @return
     */
    public long getResponseTime() {
        return this.responseTime;
    }

    /**
     * Set the response body for this response. Inherited classes should
     * overwrite this to their specific needs.
     * 
     * @param body
     */
    public void setResponseBody(String body) {
        this.response = body;
    }

    public long getProxyResponseTime() {
        return this.proxyResponseTime;
    }

    public void setProxyResponseTime(long proxyResponseTime) {
        this.proxyResponseTime = proxyResponseTime;
    }

    /**
     * 
     * @param byteArray
     */
    public void setResponseBody(byte[] byteArray) {
        this.responseByteArray = byteArray;
        this.response = new String(byteArray);
    }

    /**
     * Returns the response body for this response.
     * 
     * @return String, the body of this response.
     */
    public String getResponseBody() {
        return this.response;
    }

    /**
     * Returns the response body for this response.
     * 
     * @return String, the body of this response.
     */
    public String getBody() {
        return this.response;
    }

    /**
     * Returns the response body for this response.
     * 
     * @return String, the body of this response.
     */
    public byte[] getResponseBytes() {
        return this.responseByteArray;
    }

    public int getResponseSize() {
        return this.responseByteArray != null ? this.responseByteArray.length : this.response != null ? this.response.length() : -1;
    }

    /**
     * Lookup a cookies value
     * 
     * @param key
     *            Cookie name to lookup
     * @return Entire cookie string as it is return to browser in Set-Cookie
     *         header
     */
    public String getCookie(String key) {
        return cookies.get(key.toLowerCase());
    }

    public void setCookie(String key, String value) {
        this.cookies.put(key.toLowerCase(), value);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public static final boolean isDataType(String contentType) {
        if (!StringUtils.isBlank(contentType)) {
            contentType = contentType.toLowerCase();
            return  contentType.contains("html") ||
                    contentType.contains("text") ||
                    contentType.contains("json") ||
                    contentType.contains("xml");
        }
        return false;
    }
}
