package com.ericsson.becrux.base.common.core;

import com.ericsson.becrux.base.common.exceptions.BecruxDirectoryException;
import com.ericsson.becrux.base.common.utils.CIFileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * <p>Becrux project directory with the whole content required
 * for the proper Becrux CI Engine work.</p>
 * <p>Becrux directory requires the following structure:<pre>
 * Becrux_directory
 .
 |-- baseline
 |   |-- *.json (all baseline approved components are required)
 |-- eventDao
 |   `-- queue
 |-- manageBTFEvents
 |   `-- fem023
 |       `-- queue
 |-- tools
 |   |-- config.properties -> /proj/ims_lu/cba_cde/int_ci/deliveries/intX/config/LATEST (the latest config.properties file )
 |   |-- intX -> /proj/ims_lu/cba_cde/int_ci/deliveries/intX/LATEST (the latest Test execution jar file)
 |   |-- phoenix -> /proj/ims_lu/cba_cde/int_ci/deliveries/phoenix/LATEST (the latest Phoenix delivery)
 |   `-- provisioning -> /proj/ims_lu/cba_cde/int_ci/tools/provision/latest (the latest Provisioning delivery)
 |-- viseManager
     |-- production
     |   |-- channelreservations
     |   |-- visechannels
     |   `-- visepools
     |-- staging
     |   |-- channelreservations
     |   |-- visechannels
     |   `-- visepools
     `-- standalone
         |-- channelreservations
         |-- visechannels
         `-- visepools

 * </pre>
 */
public class BecruxDirectory {

    private static final String TOOLS_DIR_NAME = "tools";
    private static final String TEST_EXEC_FILE_NAME = "intX";
    private static final String PROVISION_DIR_NAME = "provisioning";
    private static final String PROVISIONING_SCRIPT_PATH = File.separator + "provision";
    private static final String PHOENIX_DIR_NAME = "phoenix";
    private static final String PHOENIX_SCRIPT_DIR_NAME = "bin";
    private static final String PHOENIX_SCRIPT_PATH = PHOENIX_SCRIPT_DIR_NAME + File.separator + "phoenix";

    private File baseDir;
    private File toolsDir;

    private File testExecJar;

    private File phoenixDir;
    private File phoenixScript;

    private File provisioningDir;
    private File provisioningScript;

    /**
     * Creates BECRUX directory object for the given path. If the directory structure
     * is invalid, the exception will be thrown.
     *
     * @param directory string with the path
     * @throws BecruxDirectoryException thrown if the directory structure is invalid
     */
    public BecruxDirectory(String directory) throws BecruxDirectoryException {
        this(directory, false);
    }

    /**
     * Creates BECRUX directory object for the given path. If the directory structure
     * is invalid, the exception will be thrown
     *
     * @param directory
     * @param generateDir
     * @throws BecruxDirectoryException thrown if the directory structure is invalid
     */
    public BecruxDirectory(String directory, boolean generateDir) throws BecruxDirectoryException {
        try {
            // initialize
            baseDir = new File(directory);
            toolsDir = new File(baseDir, TOOLS_DIR_NAME);

            provisioningDir = new File(toolsDir, PROVISION_DIR_NAME);
            provisioningScript = new File(provisioningDir, PROVISIONING_SCRIPT_PATH);

            phoenixDir = new File(toolsDir, PHOENIX_DIR_NAME);
            phoenixScript = new File(phoenixDir, PHOENIX_SCRIPT_PATH);

            testExecJar = new File(toolsDir, TEST_EXEC_FILE_NAME);


            if(generateDir) {
                // generate Folder structure
                createFolders();
            }
        } catch (IOException ex) {
            throw new BecruxDirectoryException(ex);
        }
    }

    /**
     * Create folder structure and temp files. Those temp files need to be replaced by the real runnable file.
     *
     * @throws Exception if the path is invalid
     */
    public void createFolders() throws IOException {
        // create base dir from pathDir if not exits
        if (!baseDir.exists())
            Files.createDirectories(baseDir.toPath());

        // create tools dir
        if (!toolsDir.exists())
            Files.createDirectory(toolsDir.toPath());

        // create Provisioning dir
        if (!provisioningDir.exists())
            Files.createDirectory(provisioningDir.toPath());
        if (!provisioningScript.exists()) {
            Files.createDirectories(provisioningScript.getParentFile().toPath());
            Files.createFile(provisioningScript.toPath());
        }
        provisioningScript.setExecutable(true);

        // create Phoenix Dir
        if (!phoenixDir.exists())
            Files.createDirectory(phoenixDir.toPath());
        if (!phoenixScript.exists()) {
            Files.createDirectories(phoenixScript.getParentFile().toPath());
            Files.createFile(phoenixScript.toPath());
        }
        phoenixScript.setExecutable(true);

        // create Test exec file
        if (!testExecJar.exists())
            Files.createFile(testExecJar.toPath());
    }

    /**
     * Validate all the Folders.
     *
     * @return
     * @throws Exception if anything fail
     */
    public boolean validate() throws BecruxDirectoryException {
        BecruxDirectoryException exception = new BecruxDirectoryException();

        // TODO: the logic to validate what need to have inside every folder seem wrong.
        try {
            validateDir(null, baseDir);
            validateDir(baseDir, toolsDir);

            try {
                validateDir(toolsDir, phoenixDir);
            } catch (Exception e) { exception.add(e); }

            try {
                validateDir(toolsDir, provisioningDir);
            } catch (Exception e) { exception.add(e); }
        }catch (Exception e) {
            exception.add(e);
        }

        if (exception.getMessages().size() > 0) {
            throw exception;
        }

        return true;
    }

    private boolean validateDir(File parent, File child) throws Exception {
        try {
            if (child == null || !child.exists())
                throw new Exception("null or empty path provided");

            CIFileHelper.validateFile(child.getPath(), true, true, false, false, true);
        } catch (Exception ex) {
            throw ex;
        }
        return true;
    }

    /**
     * get the ILES directory location
     *
     * @return file object
     */
    public File getBaseDir() {
        return baseDir;
    }

    /**
     * get the Test Execution Jar file location
     *
     * @return file object
     */
    public File getTestExecJar() {
        return testExecJar;
    }

    /**
     * get the Provisioning script location
     *
     * @return file object
     */
    public File getProvisioningScript() {
        return provisioningScript;
    }

    public File getPhoenixDir() {
        return phoenixDir;
    }

    public File getProvisioningDir() {
        return provisioningDir;
    }
}
