package com.ericsson.becrux.iles.phoenix;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.utils.BecruxBuildBadgeAction;
import com.ericsson.becrux.base.common.loop.ComponentParameterValue;
import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.vise.parameters.ReservedViseChannelParameterValue;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.iles.utils.IlesVersionHelper;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for Phoenix Installation Build Step in whole
 * build process.
 */
public class PhoenixInstallationBuildStep extends NwftBuildStep {

    String additionalArgs;

    @DataBoundConstructor
    public PhoenixInstallationBuildStep(String additionalArgs) {
        this.additionalArgs = additionalArgs;
    }

    public String getAdditionalArgs() {
        return additionalArgs;
    }

    public void setAdditionalArgs(String additionalArgs) {
        this.additionalArgs = additionalArgs;
    }

    /** {@inheritDoc} */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        try {

            List<Component> components = this.getAllNwftParametersOfType(build, ComponentParameterValue.class)
                    .stream().map(param -> param.getComponent()).collect(Collectors.toList());
            if (components == null || components.size() != 1)
                throw new Exception(); // TODO: fix later
            Component node = components.get(0);

            if (node == null) {
                throw new Exception("Node was not initialized!");
            }

            List<ViseChannel> channels = this.getAllNwftParametersOfType(build, ReservedViseChannelParameterValue.class)
                    .stream().map(param -> param.getViseChannel()).collect(Collectors.toList());
            if (channels == null || channels.size() != 1)
                throw new Exception(); // TODO: fix later
            ViseChannel channel = channels.get(0);

            build.addAction(
                    new BecruxBuildBadgeAction(
                            channel.getFullName() + ": " + node.getType().toString() + " " + node.getVersion()));

            IlesGlobalConfig cfg = IlesGlobalConfig.getInstance();
            File phoenixSourceDir = new File(cfg.getIlesDirectory().getPhoenixDir().getPath());

            FilePath workspaceDir = build.getWorkspace();

            FilePath fp = new FilePath(phoenixSourceDir);
            fp.copyRecursiveTo(workspaceDir);

            FilePath phoenixExePath = new FilePath(new FilePath(workspaceDir, "bin"), "phx");

            String cmd = phoenixExePath.getRemote() + " " + getCommandParameters(node,channel );
            listener.getLogger().println("Executing Phoenix command: " + cmd);
            return new Shell(cmd).perform(build, launcher, listener);

        } catch (Exception e) {
            e.printStackTrace();
            listener.getLogger().println(e);
            return false;
        }
    }

    private boolean isCustomVersionByUser(String version) {
        return !IlesVersionHelper.getInstance().isIlesReleaseVersionFormat(version);
    }

    private String getCommandParameters(Component node, ViseChannel viseChannel){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(node.getType().toLowerCase());
        stringBuilder.append("_");
        stringBuilder.append(viseChannel.getWholeNumber());
        stringBuilder.append(" ");

        if (isCustomVersionByUser(node.getVersion())) {
            stringBuilder.append(node.getVersion());
            stringBuilder.append(" ");
            stringBuilder.append(node.getPdb() != null ? node.getPdb() : "");
        }
        else {
            stringBuilder.append("sw=");
            stringBuilder.append(node.getType().toUpperCase()); // phoenix need type to be uppercase !
            stringBuilder.append("_");
            stringBuilder.append(node.getVersion().replace('/', '_'));
        }
        if (additionalArgs != null && !additionalArgs.isEmpty()) {
            // Add additional agrument
            stringBuilder.append(" ").append(additionalArgs);
        }
        return stringBuilder.toString();
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link PhoenixInstallationBuildStep}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an
    // extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project
            // types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "ILES: Use Phoenix to install nodes";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }

    }
}
