package com.ericsson.becrux.base.common.provisioning;

/**
 * The Provisioning processor.
 *
 * @author DungB
 */
public interface Provisioning {

    /**
     * Validate the {@link ProvisioningProperties}.
     * @return
     * @throws if something fail
     */
    boolean validateProperties() throws Exception;

    /**
     * Generate Execution Shell Command.
     * @return
     * @throws Exception
     */
    String generateExecuteCommand() throws Exception;
}
