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

import java.util.Set;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>DefaultUserCpTest</code> contains tests for the class <code>{@link DefaultUser}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class DefaultUserCpTest {
    /**
     * Run the DefaultUser(HierarchicalConfiguration) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testDefaultUser_1()
            throws Exception {
        HierarchicalConfiguration config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();

        DefaultUser result = new DefaultUser(config);

        assertNotNull(result);
        assertEquals(null, result.getPassword());
        assertEquals(null, result.getEmail());
        assertEquals(null, result.toString());
        assertEquals(null, result.getName());
        assertEquals(false, result.isAdmin());
    }

    /**
     * Run the DefaultUser(HierarchicalConfiguration) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testDefaultUser_2()
            throws Exception {
        HierarchicalConfiguration config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();

        DefaultUser result = new DefaultUser(config);

        assertNotNull(result);
        assertEquals(null, result.getPassword());
        assertEquals(null, result.getEmail());
        assertEquals(null, result.toString());
        assertEquals(null, result.getName());
        assertEquals(false, result.isAdmin());
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testEquals_1()
            throws Exception {
        DefaultUser fixture = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testEquals_2()
            throws Exception {
        DefaultUser fixture = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        Object obj = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testEquals_3()
            throws Exception {
        DefaultUser fixture = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());
        Object obj = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getEmail() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetEmail_1()
            throws Exception {
        DefaultUser fixture = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getEmail();

        assertEquals(null, result);
    }

    /**
     * Run the Set<String> getGroups() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetGroups_1()
            throws Exception {
        DefaultUser fixture = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        Set<String> result = fixture.getGroups();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        DefaultUser fixture = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getName();

        assertEquals(null, result);
    }

    /**
     * Run the String getPassword() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetPassword_1()
            throws Exception {
        DefaultUser fixture = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getPassword();

        assertEquals(null, result);
    }

    /**
     * Run the int hashCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testHashCode_1()
            throws Exception {
        DefaultUser fixture = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        int result = fixture.hashCode();

        assertEquals(1305, result);
    }

    /**
     * Run the boolean isAdmin() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testIsAdmin_1()
            throws Exception {
        DefaultUser fixture = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        boolean result = fixture.isAdmin();

        assertEquals(false, result);
    }

    /**
     * Run the boolean isAdmin() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testIsAdmin_2()
            throws Exception {
        DefaultUser fixture = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        boolean result = fixture.isAdmin();

        assertEquals(false, result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        DefaultUser fixture = new DefaultUser(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.toString();

        assertEquals(null, result);
    }
}