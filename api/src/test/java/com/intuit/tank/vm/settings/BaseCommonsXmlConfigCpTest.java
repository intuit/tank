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

import java.io.File;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>BaseCommonsXmlConfigCpTest</code> contains tests for the class
 * <code>{@link BaseCommonsXmlConfig}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class BaseCommonsXmlConfigCpTest {
    /**
     * Run the void checkReload() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testCheckReload_1()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");
        
        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void checkReload() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testCheckReload_3()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = null;
        fixture.configFile = new File("");

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the HierarchicalConfiguration getChildConfigurationAt(HierarchicalConfiguration,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetChildConfigurationAt_1()
            throws Exception {
        HierarchicalConfiguration config = null;
        String key = "";

        HierarchicalConfiguration result = BaseCommonsXmlConfig.getChildConfigurationAt(config, key);

        assertEquals(null, result);
    }

    /**
     * Run the HierarchicalConfiguration getChildConfigurationAt(HierarchicalConfiguration,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetChildConfigurationAt_2()
            throws Exception {
        HierarchicalConfiguration config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();
        String key = "";

        HierarchicalConfiguration result = BaseCommonsXmlConfig.getChildConfigurationAt(config, key);

        assertNotNull(result);
        assertEquals(true, result.isEmpty());
    }

    /**
     * Run the HierarchicalConfiguration getChildConfigurationAt(HierarchicalConfiguration,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetChildConfigurationAt_3()
            throws Exception {
        HierarchicalConfiguration config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();
        String key = "";

        HierarchicalConfiguration result = BaseCommonsXmlConfig.getChildConfigurationAt(config, key);

        assertNotNull(result);
        assertEquals(true, result.isEmpty());
    }

    /**
     * Run the HierarchicalConfiguration getChildConfigurationAt(HierarchicalConfiguration,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetChildConfigurationAt_4()
            throws Exception {
        HierarchicalConfiguration config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();
        String key = "";

        HierarchicalConfiguration result = BaseCommonsXmlConfig.getChildConfigurationAt(config, key);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Run the XMLConfiguration getConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetConfig_1()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        XMLConfiguration result = fixture.getConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
        assertNotNull(result);
    }

    /**
     * Run the File getSourceConfigFile() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetSourceConfigFile_1()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        File result = fixture.getSourceConfigFile();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
        assertNotNull(result);
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_1()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_2()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_3()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_4()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_5()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_6()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_7()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_8()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_9()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_10()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_11()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_12()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_13()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }

    /**
     * Run the void readConfig() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReadConfig_14()
            throws Exception {
        MailMessageConfig fixture = new MailMessageConfig();
        fixture.XMLConfig = new XMLConfiguration();
        fixture.configFile = new File("");

        fixture.readConfig();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.SecurityException: Cannot write to files while generating test cases
        // at
        // com.instantiations.assist.eclipse.junit.CodeProJUnitSecurityManager.checkWrite(CodeProJUnitSecurityManager.java:76)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:209)
        // at java.io.FileOutputStream.<init>(FileOutputStream.java:171)
        // at org.apache.commons.configuration.AbstractFileConfiguration.save(AbstractFileConfiguration.java:490)
        // at
        // org.apache.commons.configuration.AbstractHierarchicalFileConfiguration.save(AbstractHierarchicalFileConfiguration.java:204)
        // at com.intuit.tank.settings.BaseCommonsXmlConfig.readConfig(BaseCommonsXmlConfig.java:63)
        // at com.intuit.tank.settings.MailMessageConfig.<init>(MailMessageConfig.java:71)
    }
}