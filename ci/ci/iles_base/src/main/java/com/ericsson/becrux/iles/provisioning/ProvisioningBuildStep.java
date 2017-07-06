package com.ericsson.becrux.iles.provisioning;

import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.provisioning.Provisioning;
import com.ericsson.becrux.base.common.provisioning.ProvisioningProperties;
import com.ericsson.becrux.base.common.utils.BecruxBuildBadgeAction;
import com.ericsson.becrux.base.common.vise.parameters.ReservedViseChannelParameterValue;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.iles.provisioning.impl.IlesProvisioning;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.List;

public class ProvisioningBuildStep extends NwftBuildStep {


    @DataBoundConstructor
    public ProvisioningBuildStep() {
        super();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        try {
            ProvisioningProperties provisioningProperties = getProvisioningPropertiesFromBuild(build);
            Provisioning provisioning = new IlesProvisioning(provisioningProperties);

            // Add badge build
            build.addAction(
                    new BecruxBuildBadgeAction(provisioningProperties.getViseChannel().getFullName()));

            // validate the properties
            listener.getLogger().println("Validate Provisioning properties.");
            provisioning.validateProperties();

            String command = provisioning.generateExecuteCommand();
            listener.getLogger().println("Executing Provisioning command : " + command);
            return new Shell(command).perform(build, launcher, listener);
        }  catch (Exception e) {
            e.printStackTrace(listener.getLogger());
            return false;
        }
    }

    private ProvisioningProperties getProvisioningPropertiesFromBuild(AbstractBuild<?, ?> build) throws Exception {
        IlesGlobalConfig ilessConfig = IlesGlobalConfig.getInstance();
        JenkinsGlobalConfig globalConfig = JenkinsGlobalConfig.getInstance();

        ProvisioningProperties provisioningProperties = new ProvisioningProperties();
        List<ReservedViseChannelParameterValue> viseValues = getAllNwftParametersOfType(build, ReservedViseChannelParameterValue.class);

        if (viseValues.size() <= 0)
            throw new Exception("No VISE value in build parameters.");
        else if (viseValues.size() > 1)
            throw new Exception("More than one VISE value provided.");

        provisioningProperties.setScriptPath(ilessConfig.getIlesDirectory().getProvisioningScript().getAbsolutePath());
        provisioningProperties.setViseChannel(viseValues.get(0).getViseChannel());
        provisioningProperties.setNetconf(globalConfig.isNetconf());
        provisioningProperties.setSslFilePath(globalConfig.getSslFile());

        return provisioningProperties;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "ILES: Perform provisioning";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            save();
            return super.configure(req, json);
        }
    }

}
