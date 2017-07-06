package com.ericsson.becrux.iles.provisioning.impl;

import com.ericsson.becrux.base.common.provisioning.Provisioning;
import com.ericsson.becrux.base.common.provisioning.ProvisioningProperties;
import com.ericsson.becrux.base.common.provisioning.impl.BaseProvisioning;
import com.ericsson.becrux.base.common.utils.CIFileHelper;
import com.ericsson.becrux.base.common.vise.ViseChannel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link Provisioning}
 */
public class IlesProvisioning extends BaseProvisioning implements Provisioning {

    // TODO: can we make it become external properties ?
    private static String EXECUTE_COMMAND_PATTERN = "{0} -c delete -l {1} {2} {3} \n {0} -c add -l {1} {2} {3}";

    /**
     * Constructor.
     */
    public IlesProvisioning(ProvisioningProperties properties) {
        super(properties);
    }

    /** {@inheritDoc} */
    @Override
    public boolean validateProperties() throws Exception {
        // validate Provisioning script
        CIFileHelper.validateExecutableFile(getProperties().getScriptPath(), false, true);

        // validate vise value
        ViseChannel viseChannel = getProperties().getViseChannel();
        if (viseChannel == null)
            throw new NullPointerException("VISE value not provided.");

        // check if net config is required
        boolean netconf = getProperties().isNetconf();

        // check if ssl file can be read and execute
        CIFileHelper.validateExecutableFile(getProperties().getSslFilePath(), false, false);

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String generateExecuteCommand() throws Exception {
        List<String> commandParams = new ArrayList<>();

        // map properties to commandParams
        commandParams.add(getProperties().getScriptPath());
        commandParams.add(getProperties().getViseChannel().getShortName());
        commandParams.add(getProperties().isNetconf() ? " -n " : "");
        String ssl = getProperties().getSslFilePath();
        if (ssl == null || ssl.isEmpty())
            commandParams.add("");
        else
            commandParams.add("-s " + ssl);

        return MessageFormat.format(EXECUTE_COMMAND_PATTERN, commandParams.toArray());
    }
}
