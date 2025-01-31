/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.proxy.config;

/*
 * #%L
 * proxy-extension
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ProxyConfiguration
 * 
 * @author dangleton
 * 
 */
public class CommonsProxyConfiguration implements ProxyConfiguration {

    private static final Logger LOG = LogManager.getLogger(CommonsProxyConfiguration.class);

    public static final String DEFAULT_CONFIG = "recording-proxy-config-default.xml";
    public static final String SUGGESTED_CONFIG_NAME = "recording-proxy-config.xml";

    private String configPath = "recording-proxy-config.xml";    
    private XMLConfiguration XMLConfig;

    private Set<ConfigInclusionExclusionRule> exclusions = new HashSet<ConfigInclusionExclusionRule>();
    private Set<ConfigInclusionExclusionRule> inclusions = new HashSet<ConfigInclusionExclusionRule>();

    private Set<ConfigInclusionExclusionRule> bodyInclusions = new HashSet<ConfigInclusionExclusionRule>();
    private Set<ConfigInclusionExclusionRule> bodyExclusions = new HashSet<ConfigInclusionExclusionRule>();

    /**
     * test constructor
     * 
     */
    public CommonsProxyConfiguration(String configPath) {
        this.configPath = configPath != null ? configPath : DEFAULT_CONFIG;
        readConfig();
    }

    /**
     * 
     */
    public CommonsProxyConfiguration() {
        this(null);
    }

    /**
     * {@inheritDoc}
     */
    public int getPort() {
        return XMLConfig.getInt("proxy-port", 8888);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFollowRedirects() {
        return XMLConfig.getBoolean("follow-redirects", true);
    }

    /**
     * {@inheritDoc}
     */
    public String getOutputFile() {
        return XMLConfig.getString("output-file", new File("recordedOutput.xml").getAbsolutePath());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCertificateAuthorityPath() {
    	return XMLConfig.getString("certificate-authority-path", new File("auto_generated_ca.p12").getAbsolutePath());
    }

    /**
     * {@inheritDoc}
     */
    public Set<ConfigInclusionExclusionRule> getExclusions() {
        return exclusions;
    }

    /**
     * {@inheritDoc}
     */
    public Set<ConfigInclusionExclusionRule> getInclusions() {
        return inclusions;
    }

    /**
     * {@inheritDoc}
     */
    public Set<ConfigInclusionExclusionRule> getBodyInclusions() {
        return bodyInclusions;
    }

    /**
     * {@inheritDoc}
     */
    public Set<ConfigInclusionExclusionRule> getBodyExclusions() {
        return bodyExclusions;
    }

    /**
     * Constructor pulls file out of the jar or reads from disk and sets up refresh policy.
     *
     */
    private void readConfig() {
        try {
            XPathExpressionEngine expressionEngine = new XPathExpressionEngine();

            File configFile = new File(configPath);
            System.out.println(configFile.getAbsolutePath());
            if (configFile.exists() && configFile.isFile()) {
                try {
                    XMLConfig = new ReloadingFileBasedConfigurationBuilder<>(XMLConfiguration.class)
                            .configure(new Parameters().xml().setFile(configFile).setExpressionEngine(expressionEngine))
                            .getConfiguration();
                } catch (Exception e) {
                    LOG.error("Error parsing configFile " + configFile.getAbsolutePath() + ": " + e, e);
                }
            }
            if (XMLConfig == null) {
                // Load a default from the classpath:
                LOG.info("Reading default configuration " + DEFAULT_CONFIG + " from classpath...");
                // Note: we don't let new XMLConfiguration() lookup the resource
                // url directly because it may not be able to find the desired
                // classloader to load the URL from.
                URL configResourceUrl = this.getClass().getClassLoader().getResource(DEFAULT_CONFIG);
                if (configResourceUrl == null) {
                    throw new RuntimeException("unable to load resource: " + configPath);
                }
                XMLConfig = new ReloadingFileBasedConfigurationBuilder<>(XMLConfiguration.class)
                        .configure(new Parameters().xml().setURL(configResourceUrl).setExpressionEngine(expressionEngine))
                        .getConfiguration();
            }
            initConfig();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig(File configFile) throws ConfigurationException, IOException {
        XMLConfig.write(new FileWriter(configFile));
    }

    /**
     * 
     */
    private void initConfig() {
        exclusions = parseInclusionExclusions("exclusions");
        inclusions = parseInclusionExclusions("inclusions");
        bodyExclusions = parseInclusionExclusions("body-exclusions");
        bodyInclusions = parseInclusionExclusions("body-inclusions");
    }

    /**
     * @param key
     * @return
     */
    private Set<ConfigInclusionExclusionRule> parseInclusionExclusions(String key) {
        HierarchicalConfiguration<ImmutableNode> groupConfig = XMLConfig.configurationAt(key);
        if (groupConfig != null) {
            List<HierarchicalConfiguration<ImmutableNode>> list = groupConfig.configurationsAt("rule");
            return list.stream()
                    .map(c -> new ConfigInclusionExclusionRule(getTransactionPart(c), c.getString("@header", "all"),
                            getMatchType(c), c.getString(""))).collect(Collectors.toSet());
        }
        return Set.of();
    }

    /**
     * @param c
     * @return
     */
    private MatchType getMatchType(HierarchicalConfiguration c) {
        MatchType ret = MatchType.contains;
        String string = c.getString("@match");
        if (string != null) {
            try {
                ret = MatchType.valueOf(string);
            } catch (Exception e) {
                LOG.warn("Illegal MatchType value: " + string);
            }
        }
        return ret;
    }

    /**
     * @param c
     * @return
     */
    private TransactionPart getTransactionPart(HierarchicalConfiguration c) {
        TransactionPart ret = TransactionPart.both;
        String string = c.getString("@check");
        if (string != null) {
            try {
                ret = TransactionPart.valueOf(string);
            } catch (Exception e) {
                LOG.warn("Illegal TransactionPart value: " + string);
            }
        }
        return ret;
    }

    public static boolean save(int port, boolean followRedirect, String outputFile,
            Set<ConfigInclusionExclusionRule> inclusions,
            Set<ConfigInclusionExclusionRule> exclusions,
            Set<ConfigInclusionExclusionRule> bodyInclusions,
            Set<ConfigInclusionExclusionRule> bodyExclusions,
            String fileName) {

        ImmutableNode node = getConfNode("recording-proxy-config", "", false);
        ImmutableNode portNode = getConfNode("proxy-port", String.valueOf(port), false);
        ImmutableNode followRedirectNode = getConfNode("follow-redirects", Boolean.toString(followRedirect), false);
        ImmutableNode outputFileNode = getConfNode("output-file", outputFile, false);
        ImmutableNode inclusionsNode = getConfNode("inclusions", "", false);
        ImmutableNode exclusionsNode = getConfNode("exclusions", "", false);
        ImmutableNode bodyInclusionsNode = getConfNode("body-inclusions", "", false);
        ImmutableNode bodyExclusionsNode = getConfNode("body-exclusions", "", false);

        updateRuleParentNode(inclusions, inclusionsNode);
        updateRuleParentNode(exclusions, exclusionsNode);
        updateRuleParentNode(bodyInclusions, bodyInclusionsNode);
        updateRuleParentNode(bodyExclusions, bodyExclusionsNode);

        node.addChild(portNode);
        node.addChild(followRedirectNode);
        node.addChild(outputFileNode);
        node.addChild(inclusionsNode);
        node.addChild(exclusionsNode);
        node.addChild(bodyInclusionsNode);
        node.addChild(bodyExclusionsNode);

        XMLConfiguration xmlConfiguration = new XMLConfiguration();
        xmlConfiguration.addNodes("root", Collections.singleton(node));

        try {

            xmlConfiguration.write(new FileWriter(fileName));
        } catch (ConfigurationException | IOException e) {
            e.printStackTrace();
        }
        return true;

    }

    public static void updateRuleParentNode(Set<ConfigInclusionExclusionRule> rule, ImmutableNode parentNode) {
        for (ConfigInclusionExclusionRule configInclusionExclusionRule : rule) {
            ImmutableNode ruleNode = getConfNode("rule", configInclusionExclusionRule.getValue(), false);
            ImmutableNode checkNode = getConfNode("check", configInclusionExclusionRule.getTransactionPart().toString(), true);
            ImmutableNode matchNode = getConfNode("match", configInclusionExclusionRule.getMatch().toString(), true);
            ImmutableNode headerNode = getConfNode("header", configInclusionExclusionRule.getHeader(), true);

            ruleNode.addChild(checkNode);
            ruleNode.addChild(matchNode);
            ruleNode.addChild(headerNode);

            parentNode.addChild(ruleNode);
        }
    }

    private static ImmutableNode getConfNode(String name, String value, boolean attributeFlag) {
        return new ImmutableNode.Builder().addAttribute(name, value).create();
    }

}
