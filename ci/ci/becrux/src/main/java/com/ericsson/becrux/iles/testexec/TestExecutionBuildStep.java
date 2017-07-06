package com.ericsson.becrux.iles.testexec;

import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.core.CommonParamenterValue;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.loop.ComponentParameterValue;
import com.ericsson.becrux.base.common.testexec.TestExec;
import com.ericsson.becrux.base.common.utils.BecruxBuildBadgeAction;
import com.ericsson.becrux.base.common.vise.parameters.ReservedViseChannelParameterValue;
import com.ericsson.becrux.iles.eventhandler.strategies.ITREventStrategy;
import com.ericsson.becrux.iles.leo.parameters.InitLeoParameterValue;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.iles.data.Int;
import com.ericsson.becrux.iles.testexec.impl.IlesTestExec;
import com.ericsson.becrux.iles.utils.IlesProcessingHelper;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The build step for executing tests.
 */
public class TestExecutionBuildStep extends NwftBuildStep {
    private static final IlesProcessingHelper ilesProcessingHelper = new IlesProcessingHelper();
    private String configPropertiesPath;
    private String gatewayServer;
    private boolean useSandboxPdbInstance;
    private boolean uploadToInsight;
    private boolean realTestExec;
    private String additionalArgs;

    @DataBoundConstructor
    public TestExecutionBuildStep(String gatewayServer, boolean useSandboxPdbInstance, boolean uploadToInsight, boolean realTestExec, String additionalArgs) {
        this.gatewayServer = gatewayServer;
        this.useSandboxPdbInstance = useSandboxPdbInstance;
        this.uploadToInsight = uploadToInsight;
        this.realTestExec = realTestExec;
        this.additionalArgs = additionalArgs;
    }

    public String getConfigPropertiesPath() {
        return configPropertiesPath;
    }

    public void setConfigPropertiesPath(String configPropertiesPath) {
        this.configPropertiesPath = configPropertiesPath;
    }

    /** {@inheritDoc} */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        try {
            //get config file from NwftParameterValue
            List <CommonParamenterValue> commonParams = getAllNwftParametersOfType(build, CommonParamenterValue.class)
                    .stream().collect(Collectors.toList());

            String cfg = null;
            for (CommonParamenterValue param : commonParams) {
                if (param.getName().equals(ITREventStrategy.CONFIG_PROPERTIES)) {
                    cfg = param.getValue().toString();

                }
            }

            listener.getLogger().println("The path of config.properties file " + cfg);
            setConfigPropertiesPath(cfg);
            new Shell("cat " + cfg).perform(build, launcher, listener);

            // check if we need to add viseChannel or it already have ?
            IlesTestExecProperties ilesTestExecProperties = getTestExecPropertiesFromBuild(build, listener);
            TestExec testExec = new IlesTestExec(ilesTestExecProperties);

            // Add badge build
            build.addAction(
                    new BecruxBuildBadgeAction(ilesTestExecProperties.getViseChannel().getFullName()));

            // Validate properties
            listener.getLogger().println("Validate Test Execution properties.");
            testExec.validateProperties();

            // execute Shell command
            String command = testExec.generateExecuteCommand();
            listener.getLogger().println("\nRunning Test Execution command : " + command);

            // Get the QUICK_TEST parameter
            boolean isQuickTest = false;
            for (CommonParamenterValue param : getAllNwftParametersOfType(build, CommonParamenterValue.class)) {
                if (param.getName().toString().equals(ITREventStrategy.QUICK_TEST)) {
                    isQuickTest = param.getValue().toString().equals("true") ? true : false;
                    break;
                }
            }

            if (isQuickTest) {
                // TODO: we need the sleep time here
                return true;
            }
            else{
                return new Shell(command).perform(build, launcher, listener);
            }
        }  catch (Exception e) {
            e.printStackTrace(listener.getLogger());
            return false;
        }
    }

    /**
     * Gets the INT component type, reserved vise channels, leo parameters and test
     * execution properties configured for a build step from build.
     * @param build Base implementation that runs build
     * @param listener Receives events that happen during a build
     * @return IlesTestExecProperties with test execution properties for INT
     * @throws Exception
     */
    private IlesTestExecProperties getTestExecPropertiesFromBuild(AbstractBuild<?, ?> build, BuildListener listener) throws Exception {
        List<ComponentParameterValue> componentValues = getAllNwftParametersOfType(build, ComponentParameterValue.class)
                .stream().filter(param -> param.getComponent() instanceof Int).collect(Collectors.toList());
        List<ReservedViseChannelParameterValue> viseValues = getAllNwftParametersOfType(build, ReservedViseChannelParameterValue.class);
        List<InitLeoParameterValue> leoValues = getAllNwftParametersOfType(build, InitLeoParameterValue.class);
        String jobId = "";


        if (componentValues.size() <= 0 )
            throw new Exception("No INT test suite data in build parameters.");
        else if (componentValues.size() > 1)
            throw new Exception("More than one INT test suite data parameter provided.");
        if (viseValues.size() <= 0)
            throw new Exception("No VISE value in build parameters.");
        else if (viseValues.size() > 1)
            throw new Exception("More than one VISE value provided.");
        if (leoValues.size() > 1)
            throw new Exception("More than one Leo data parameter provided.");
        else if(leoValues.size() == 1 && leoValues.get(0) != null
                && leoValues.get(0).getJob() != null && leoValues.get(0).getJob().id != null)
            jobId = leoValues.get(0).getJob().id.toString();


        try (IlesTestExecProperties properties = new IlesTestExecProperties()) {

            properties.setCfgFile(getConfigPropertiesPath());
            properties.setIntXLocation(ilesProcessingHelper.getIntBinaryPath(build));
            properties.setViseChannel(viseValues.get(0).getViseChannel());
            properties.setRealTestExec(isRealTestExec());
            properties.setExecutionServer("root@" + ViseChannelGlobalConfig.getInstance().getDao().loadViseChannel(viseValues.get(0).getViseChannel().getFullName()).getIpAddress());
            properties.setGatewayServer(getGatewayServer());
            properties.setUploadToInsight(isUploadToInsight());
            properties.setUseSandboxPdbInstance(isUseSandboxPdbInstance());
            properties.setAdditionalArgs(getAdditionalArgs());

            return properties;
        }
    }

    // Getters and setters

    public String getGatewayServer() {
        return gatewayServer;
    }

    public void setGatewayServer(String gatewayServer) {
        this.gatewayServer = gatewayServer;
    }

    public boolean isUseSandboxPdbInstance() {
        return useSandboxPdbInstance;
    }

    public void setUseSandboxPdbInstance(boolean useSandboxPdbInstance) {
        this.useSandboxPdbInstance = useSandboxPdbInstance;
    }

    public boolean isUploadToInsight() {
        return uploadToInsight;
    }

    public void setUploadToInsight(boolean uploadToInsight) {
        this.uploadToInsight = uploadToInsight;
    }

    public boolean isRealTestExec() {
        return realTestExec;
    }

    public void setRealTestExec(boolean realTestExec) {
        this.realTestExec = realTestExec;
    }

    public String getAdditionalArgs() {
        return additionalArgs;
    }

    public void setAdditionalArgs(String additionalArgs) {
        this.additionalArgs = additionalArgs;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension // This indicates to Jenkins that this is an implementation of an
    // extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information, simply store it in a
         * field and call save().
         * <p>
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */



        /**
         * In order to load the persisted global configuration, you have to call
         * load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        @SuppressWarnings("rawtypes")
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "ILES: Execute INT test suite";
        }
    }

}

