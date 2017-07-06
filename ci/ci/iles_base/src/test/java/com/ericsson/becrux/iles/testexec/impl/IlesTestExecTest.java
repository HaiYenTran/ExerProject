package com.ericsson.becrux.iles.testexec.impl;

import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.iles.data.Int;
import com.ericsson.becrux.iles.testexec.IlesTestExecProperties;
import hudson.FilePath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Test {@link IlesTestExec}.
 */

/******************************************************************************
 * Class under test: {@link IlesTestExec}
 *
 * Description: <Short description of tests. Description of any obscurities.>
 *****************************************************************************/
public class IlesTestExecTest {

    IlesTestExec testExec;
    private FilePath directory;
    private IlesTestExecProperties properties;

    @Before
    public void mkDir() throws IOException {
        directory = new FilePath( Files.createTempDirectory(null).toFile() );

    }

    @After
    public void rmDir() throws IOException, InterruptedException {
        directory.deleteRecursive();
        properties = null;
        testExec = null;
    }

    /*********************************************************************
     * Test Group 1: Properties validation
     ********************************************************************/
    @Test(expected = FileNotFoundException.class)
    public void testValidatePropertiesJarFileNotFound() throws Exception {
        properties = new IlesTestExecProperties();
        properties.setIntXLocation("./NonExistingFile");
        testExec = new IlesTestExec(properties);
        assertTrue(testExec.validateProperties());
    }

    @Test
    public void testValidatePropertiesViseNotProvided() throws Exception {
        File fileMock = mock(File.class);
        whenNew(File.class).withArguments(anyString()).thenReturn(fileMock);
        when(fileMock.exists()).thenReturn(true);

        properties = new IlesTestExecProperties();
        properties.setIntXLocation("./");
        testExec = new IlesTestExec(properties);
        try {
            assertTrue(testExec.validateProperties());
            fail();
        }
        catch (NullPointerException npe) {
            assertTrue(npe.getMessage().contains("Vise channel not provided"));
        }
    }

    @Test
    public void testValidatePropertiesTestNotProvided() throws Exception {
        File fileMock = mock(File.class);
        whenNew(File.class).withArguments(anyString()).thenReturn(fileMock);
        when(fileMock.exists()).thenReturn(true);

        properties = new IlesTestExecProperties();
        properties.setIntXLocation("./");
        testExec = new IlesTestExec(properties);
        try {
            assertTrue(testExec.validateProperties());
            fail();
        }
        catch (NullPointerException npe) {
            assertTrue(npe.getMessage().contains("Vise channel not provided"));
        }
    }

    @Test
    public void testValidatePropertiesAllOk() throws Exception {
        File fileMock = mock(File.class);
        whenNew(File.class).withArguments(anyString()).thenReturn(fileMock);
        when(fileMock.exists()).thenReturn(true);

        properties = new IlesTestExecProperties();
        properties.setIntXLocation("./");
        properties.setViseChannel(new ViseChannel("VISE0308"));
        properties.setCfgFile("/tmp/config");
         testExec = new IlesTestExec(properties);

        try {
            assertTrue(testExec.validateProperties());
        }
        catch (FileNotFoundException fnfe) {
            fail(fnfe.getMessage());
        }
        catch (NullPointerException npe) {
            fail(npe.getMessage());
        }
    }

    /*********************************************************************
     * Test Group 2: Generators
     ********************************************************************/

    @Test
    public void testGenerateExecuteCommand() {
        try {
            properties = new IlesTestExecProperties();
            properties.setIntXLocation("./");
            properties.setViseChannel(new ViseChannel("VISE0308"));
            properties.setCfgFile("/tmp/INT.tgz");
            properties.setExecutionServer("testExecutionServer");
            properties.setGatewayServer("testGatewayServer");
            properties.setUploadToInsight(false);
            properties.setUseSandboxPdbInstance(false);
            properties.setRealTestExec(false);
            testExec = new IlesTestExec(properties);
            assertTrue(testExec.generateExecuteCommand().contains("java -jar"));

        }
        catch (Exception e) {
            fail("Failed with reason: " + e.getMessage());
        }
    }
}
