package com.ericsson.becrux.base.common.testexec;

import com.ericsson.becrux.base.common.provisioning.ProvisioningProperties;
import hudson.FilePath;

/**
 * The Test Execution processor.
 *
 * @author DungB
 */
public interface TestExec {

    /**
     * Validate the {@link ProvisioningProperties}.
     * @return
     * @throws if something fail
     */
    boolean validateProperties() throws Exception;

    /**
     * Generate the config file.
     * @return
     * @throws Exception
     */
//    FilePath generateConfigFile(FilePath workspace) throws Exception;

    /**
     * Generate Execution Shell Command.
     * @return
     * @throws Exception
     */
    String generateExecuteCommand() throws Exception;
}
