/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.io.Serializable;

import jakarta.annotation.Nonnull;

import org.apache.commons.configuration2.HierarchicalConfiguration;

/**
 * MailConfig
 * 
 * 
 * @author dangleton
 * 
 */
public class MailConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String KEY_MAIL_FROM = "mail-from";
    private static final String KEY_HOST = "mail.smtp.host";
    private static final String KEY_PORT = "mail.smtp.port";

    private HierarchicalConfiguration config;

    public MailConfig(@Nonnull HierarchicalConfiguration config) {
        this.config = config;
    }

    /**
     * @return the mailFrom
     */
    public String getMailFrom() {
        return config.getString(KEY_MAIL_FROM, "do_not_reply@localhost");
    }

    /**
     * @return the smtpHost
     */
    public String getSmtpHost() {
        return config.getString(KEY_HOST, "localhost");
    }

    /**
     * @return the smtpPort
     */
    public String getSmtpPort() {
        return config.getString(KEY_PORT, "25");
    }


}
