package com.ericsson.becrux.iles.eventhandler.strategies;

import com.ericsson.becrux.base.common.eventhandler.strategies.EventStrategyRegistrationBuildStep;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

/**
 * Build Step for registering the {@link ITREventStrategy}.
 */
public class ITREventStrategyRegistrationBuildStep extends SS {

    String poolName;
    private String workerJobName;

    /**
     * Constructor.
     * @param workerJobName Name of the worker job
     * @param poolName Name of the VISE channel pool to be used
     */
    @DataBoundConstructor
    public ITREventStrategyRegistrationBuildStep(String workerJobName, String poolName) {
        super();
        this.workerJobName = workerJobName;
        this.poolName = poolName;
    }

    public String getPoolName() {
        return poolName;
    }

    public String getWorkerJobName() {
        return workerJobName;
    }

    public void setWorkerJobName(String workerJobName) {
        this.workerJobName = workerJobName;
    }

    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        if (!this.validateJobName(workerJobName))
            return false;
        this.registerStrategy(build, new ITREventStrategy(workerJobName, poolName, build));
        return true;
    }

    private boolean validateJobName(String workerJobName) {
        return Jenkins.getInstance().getJobNames().contains(workerJobName);
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
            return "ILES: Event Handler: Strategy for ITR Event";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }

}
