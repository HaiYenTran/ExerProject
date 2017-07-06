package com.ericsson.becrux.iles.utils;

import com.ericsson.becrux.base.common.core.CommonParamenterValue;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.iles.eventhandler.strategies.ITREventStrategy;
import hudson.model.AbstractBuild;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for supporting ILES process (installation, provisioning, testing ...).
 */
public class IlesProcessingHelper {

    /**
     * Get Phoenix script path.
     * If don't have custom script path in build parameters, the method will get default from ILES global config.
     * @param build the current build
     * @return
     */
    public String getPhoenixScriptPath(AbstractBuild<?, ?> build) {
        String defaultPhoenixScriptPath = IlesGlobalConfig.getInstance().getIlesDirectory().getPhoenixDir().getAbsolutePath();
        String customScriptPath = getCommonParamValueFromBuild(build, ITREventStrategy.CUSTOM_PHOENIX_TOOL_PATH);

        return customScriptPath != null ? customScriptPath : defaultPhoenixScriptPath;
    }

    /**
     * Get Provisioning script path.
     * If don't have custom script path in build parameters, the method will get deafault from ILES global config.
     * @param build the current build
     * @return
     */
    public String getProvisioningScriptPath(AbstractBuild<?, ?> build) {
        String defaultProvisioningScript = IlesGlobalConfig.getInstance().getIlesDirectory().getProvisioningScript().getAbsolutePath();
        String customScriptPath = getCommonParamValueFromBuild(build, ITREventStrategy.CUSTOM_PROVISIONING_TOOL_PATH);

        return customScriptPath != null ? customScriptPath : defaultProvisioningScript;
    }

    /**
     * Get Int binary path.
     * If don't have custom script path in build parameters, the method will get deafault from ILES global config.
     * @param build the current build
     * @return
     */
    public String getIntBinaryPath(AbstractBuild<?, ?> build) {
        String defaultIntBinaryPath = IlesGlobalConfig.getInstance().getIlesDirectory().getTestExecJar().getAbsolutePath();
        String customIntPath = getCommonParamValueFromBuild(build, ITREventStrategy.CUSTOM_TESTEXEC_TOOL_PATH);

        return customIntPath != null ? customIntPath : defaultIntBinaryPath;
    }

    /*
        Get the value of CommonParameterValue in the build.
        If more than 1 CommonParameterValue with same name, get the first.
     */
    private String getCommonParamValueFromBuild(AbstractBuild<?, ?> build, String paramName) {
        BuildParametersExtractor extractor = new BuildParametersExtractor(build);
        List<CommonParamenterValue> path =  extractor.getAllNwftParametersOfType(CommonParamenterValue.class)
                .stream()
                .filter(p -> paramName.equalsIgnoreCase(p.getName()))
                .collect(Collectors.toList());

        return path.size() > 0 ? path.get(0).getValue().toString() : null;
    }
}
