package com.ericsson.becrux.iles.eventhandler.strategies;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

/**
 * Build Step for registering all event strategies
 */
public class AllEventStrategyRegistrationBuildStep extends ITREventStrategyRegistrationBuildStep {

    int votingThreshold;

    /**
     * Constructor.
     * @param workerJobName Name of the worker job
     * @param poolName Name of the VISE channel pool to be used
     * @param votingThreshold Threshold of number passed testcase to start the Voting request
     */
    @DataBoundConstructor
    public AllEventStrategyRegistrationBuildStep(String workerJobName, String poolName, int votingThreshold) {
        super(workerJobName, poolName);
        this.votingThreshold = votingThreshold;
    }

    public int getVotingThreshold() {
        return votingThreshold;
    }

    public void setVotingThreshold(int votingThreshold) {
        this.votingThreshold = votingThreshold;
    }

    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        super.perform(build, launcher, listener); // registers ITREventStrategy

        return true;
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
            return "ILES: Event Handler: Strategy for All Events";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }

}
