package com.intuit.tank.vm.settings;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>MailConfigCpTest</code> contains tests for the class <code>{@link MailConfig}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class MailConfigCpTest {
    /**
     * Run the MailConfig(HierarchicalConfiguration) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testMailConfig_1()
            throws Exception {
        HierarchicalConfiguration config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();

        MailConfig result = new MailConfig(config);

        assertNotNull(result);
        assertEquals("25", result.getSmtpPort());
        assertEquals("do_not_reply@localhost", result.getMailFrom());
        assertEquals("localhost", result.getSmtpHost());
    }

   

    /**
     * Run the String getMailFrom() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetMailFrom_1()
            throws Exception {
        MailConfig fixture = new MailConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getMailFrom();

        assertEquals("do_not_reply@localhost", result);
    }

    /**
     * Run the String getSmtpHost() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetSmtpHost_1()
            throws Exception {
        MailConfig fixture = new MailConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getSmtpHost();

        assertEquals("localhost", result);
    }

    /**
     * Run the String getSmtpPort() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetSmtpPort_1()
            throws Exception {
        MailConfig fixture = new MailConfig(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getSmtpPort();

        assertEquals("25", result);
    }
}