/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

/*
 * #%L
 * JSF Support Beans
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

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

/**
 * RestExceptionHandler
 * 
 * @author dangleton
 * 
 */
@Named
@Dependent
public class ExceptionHandler implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Messages messages;

    public void handle(Throwable t) {
        Throwable root = getRoot(t);
        if (root instanceof ConstraintViolationException) {
            ConstraintViolationException c = (ConstraintViolationException) root;
            for (@SuppressWarnings("rawtypes") ConstraintViolation v : c.getConstraintViolations()) {
                String sb = WordUtils.capitalize(v.getPropertyPath().iterator().next().getName()) +
                        ' ' + v.getMessage() + '.';
                messages.error(sb);
            }
        } else if (!StringUtils.isEmpty(root.getMessage())) {
            messages.error(root.getMessage());
        } else {
            messages.error(root.toString());
        }
    }

    /**
     * @param t
     * @return
     */
    private Throwable getRoot(Throwable t) {
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t;
    }
}
