package com.ericsson.becrux.iles.watcher.strategy;

import com.ericsson.becrux.base.common.configuration.FormValidator;
import com.ericsson.becrux.base.common.dao.EventDao;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

/**
 * The build step for {@link IlesEventsControlStrategy}
 */
public class IlesEventsControlBuildStep extends Builder implements BuildStep {

    private String controllerJobName;
    private String eventQueueName;

    @DataBoundConstructor
    public IlesEventsControlBuildStep(String controllerJobName, String eventQueueName)
    {
        this.controllerJobName = controllerJobName;
        this.eventQueueName = eventQueueName;
    }

    public String getControllerJobName() {
        return controllerJobName;
    }

    public void setControllerJobName(String controllerJobName) {
        this.controllerJobName = controllerJobName;
    }

    public String getEventQueueName() {
        return eventQueueName;
    }

    public void setEventQueueName(String eventQueueName) {
        this.eventQueueName = eventQueueName;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        try {
            EventDao dao = IlesGlobalConfig.getInstance().getEventDao();
            IlesEventsControlStrategy strategy = new IlesEventsControlStrategy(controllerJobName, eventQueueName, listener.getLogger(), dao, build);
            strategy.handle();
        } catch (Exception e) {
            e.printStackTrace(listener.getLogger());
        }

        return true;
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

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "ILES: Event Controlling Strategy.";
        }

        public FormValidation doCheckControllerJobName(@QueryParameter String value) {
            return FormValidator.isEmpty(value);
        }

        public FormValidation doCheckEventQueueName(@QueryParameter String value) {
            return FormValidator.isEmpty(value);
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            req.bindJSON(this, json);
            save();
            return super.configure(req, json);
        }
    }
}
