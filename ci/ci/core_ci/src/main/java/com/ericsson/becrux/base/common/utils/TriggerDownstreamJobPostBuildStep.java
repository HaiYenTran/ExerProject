package com.ericsson.becrux.base.common.utils;

import com.ericsson.becrux.base.common.core.NwftDownstreamJob;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.core.NwftPostBuildStep;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Trigger an downstream job with all current build parameter actions
 */
public class TriggerDownstreamJobPostBuildStep extends NwftPostBuildStep {

    private String jobName;
    private int delay;

    @DataBoundConstructor
    public TriggerDownstreamJobPostBuildStep(String jobName, String delay) {
        this.jobName = jobName;
        try {
            this.delay = Integer.parseInt(delay);
        } catch (Exception e) {
            this.delay = 0;
        }
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Starting job [" + jobName + "]");

        NwftParametersAction buildParams = new NwftParametersAction();
        buildParams.addParam(getAllNwftParameters(build));

        List<Action> actions = new LinkedList<>();
        actions.add(buildParams);
        NwftDownstreamJob downstreamJob = new NwftDownstreamJob(jobName, actions, new Cause.UpstreamCause(build), delay, 0);
        downstreamJob.schedule();

        addBuildDescription(build, "Triggered Job [" + jobName + "]\n");
        return true;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

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
            return "CI: Trigger Downstream Job";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
