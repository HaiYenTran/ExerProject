package com.ericsson.becrux.iles.provisioning.impl;

import com.ericsson.becrux.base.common.provisioning.Provisioning;
import com.ericsson.becrux.base.common.provisioning.ProvisioningProperties;
import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.google.common.io.Files;
import org.codehaus.plexus.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Test {@link IlesProvisioning}
 */
public class IlesProvisioningTest {

    Provisioning provisioning;
    ProvisioningProperties properties;
    File tmpDir;
    File script;

    @Before
    public void setUp(){
        tmpDir = Files.createTempDir();
        script = FileUtils.createTempFile("DummyProvisionScript", ".txt", tmpDir);
        try {
            script.createNewFile();
        }
        catch (Exception ex){

        }
    }

    @After
    public void tearDown() {
        tmpDir.delete();
        provisioning = null;
        properties = null;
    }
    /*********************************************************************
     * Test Group 1: Test the Validation method
     ********************************************************************/
    @Test(expected = NullPointerException.class)
    public void testValidatePropertiesFail() throws Exception {
        properties = new ProvisioningProperties();
        provisioning = new IlesProvisioning(properties);
        properties.setScriptPath(script.getAbsolutePath());

        assertTrue(provisioning.validateProperties());
    }

    @Test
    public void testValidatePropertiesPass() throws Exception {
        properties = new ProvisioningProperties();
        provisioning = new IlesProvisioning(properties);
        properties.setViseChannel(new ViseChannel("201"));
        properties.setScriptPath(script.getAbsolutePath());

        assertTrue(provisioning.validateProperties());

    }

    /*********************************************************************
     * Test the Generate provisioning command
     ********************************************************************/
    @Test
    public void testGenerateExecuteCommand() throws Exception {
        properties = new ProvisioningProperties();
        provisioning = new IlesProvisioning(properties);
        properties.setViseChannel(new ViseChannel("201"));
        properties.setScriptPath(script.getAbsolutePath());
        properties.setNetconf(true);
        properties.setSslFilePath("ssl.txt");
        properties.setAdditionalArgs("-max_timeout 1000");
        String cmd = provisioning.generateExecuteCommand();

        assertTrue(cmd.contentEquals(properties.getScriptPath() + " -c delete -l 201 -n -s ssl.txt -max_timeout 1000" + " \n " + properties.getScriptPath() + " -c add -l 201 -n -s ssl.txt -max_timeout 1000"));

    }
}
