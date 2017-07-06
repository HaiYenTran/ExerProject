package com.ericsson.becrux.iles.provisioning;

import com.ericsson.becrux.base.common.core.CommonParamenterValue;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.core.NwftDownstreamJob;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.loop.JobsScheduler;
import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.base.common.loop.ResultParameterValue;
import com.ericsson.becrux.base.common.vise.parameters.ReservedViseChannelParameterValue;
import com.ericsson.becrux.iles.eventhandler.strategies.ITREventStrategy;
import com.ericsson.becrux.iles.leo.parameters.InitLeoParameterValue;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.*;

/**
 * This class is responsible for a build step that schedules the
 * "PROVISIONING" downstream job.
 * Created by ematwie on 2016-12-13.
 */
public class ProvisioningSchedulerBuildStep extends NwftBuildStep {

    private int delay;
    private long timeout;
    private String jobName;
    private static String PROCESS_NAME = "PROVISIONING";
    private static String PROCESS_RESULT_KEY = "LINK";

    /**
     * Constructor.
     * @param delay
     * @param timeout
     * @param jobName Name of the downstream job to be scheduled
     */
    @DataBoundConstructor
    public ProvisioningSchedulerBuildStep(String delay, String timeout, String jobName) {
        try {
            this.delay = Integer.parseInt(delay);
        } catch (Exception e) {
            this.delay = 0;
        }

        try {
            this.timeout = Long.parseLong(timeout);
        } catch (Exception e) {
            this.timeout = 0;
        }

        this.jobName = jobName;
    }

    public int getDelay() {
        return delay;
    }

    public long getTimeout() {
        return timeout;
    }

    public String getJobName() {
        return jobName;
    }

   @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        ResultParameterValue result = startProvisioning(build, launcher, listener);
        addNwftBuildParameter(build, result);

        if (result.getProcessStatus().equals(PhaseStatus.SUCCESS) ||
                result.getProcessStatus().equals(PhaseStatus.SKIPPED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the Network function test parameters from build, verifies the VISE channels and leo data parameters and
     * run the job scheduler for downstream job (PROVISIONING).
     * @param build
     * @param launcher
     * @param listener
     * @return ResultParameterValue with status (Error/Success) of process execution
     */
    private ResultParameterValue startProvisioning (AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {

        // Initialize an empty ResultParameterValue
        Map<String, String> details = new HashMap<>();
        ResultParameterValue result = new ResultParameterValue(PROCESS_NAME, PhaseStatus.SUCCESS, details, null);
        // ResultParameterValue will have format like below:
        // PROCESS_NAME      PhaseStatus      map <PROCESS_RESULT_KEY: Value>

        StringBuilder des = new StringBuilder();
        des.append("\n");

        try {
            Jenkins jenkins = Jenkins.getInstance();

            if (jenkins.getItem(jobName) == null) {
                throw new Exception("The specified job does not exist in Jenkins: " + jobName);
            }

            Cause cause = new Cause.UpstreamCause((Run<?, ?>) build);

            for (CommonParamenterValue param : getAllNwftParametersOfType(build, CommonParamenterValue.class)) {
                if (param.getName().equals(ITREventStrategy.PROVISIONING_ABLE) && param.getValue().equals("false")) {
                    listener.getLogger().println("-> The " + PROCESS_NAME + " step was skipped by user.");

                    // Update ResultParameterValue
                    result.setProcessStatus(PhaseStatus.SKIPPED);
                    des.append("The " + PROCESS_NAME + " step was skipped by user.");
                    details.put(PROCESS_RESULT_KEY, des.toString());
                    result.setDetails(details);
                    return result;
                }
            }

            List<Action> actions = new LinkedList<>();
            NwftParametersAction params = new NwftParametersAction();

            List<ReservedViseChannelParameterValue> channels = getAllNwftParametersOfType(build, ReservedViseChannelParameterValue.class);

            if (channels.size() < 1) {
                listener.getLogger().println("No VISE value in build parameters.");
                throw new Exception("No VISE value in build parameters");
            } else if (channels.size() > 1) {
                listener.getLogger().println("More than one VISE value provided.");
                throw new Exception("More than one VISE value provided.");
            } else {
                params.addParam(channels.get(0));
            }

            List<InitLeoParameterValue> leoParams = getAllNwftParametersOfType(build, InitLeoParameterValue.class);

            if (leoParams.size() < 1) {
                listener.getLogger().println("Warning: No Leo data in build parameters.");
            } else if (leoParams.size() > 1) {
                listener.getLogger().println("More than one Leo data parameter provided.");
                throw new Exception("More than one Leo data parameter provided.");
            } else {
                params.addParam(leoParams.get(0));
            }

            actions.add(params);
            NwftDownstreamJob job = new NwftDownstreamJob(getJobName(), actions, cause, getDelay(), getTimeout());
            JobsScheduler scheduler = new JobsScheduler(
                    listener.getLogger(), job,
                    JobsScheduler.Operation.WAIT_FOR_FINISH);

            build.setResult(scheduler.run());

            // Update ResultParameterValue
            Result buildResult = build.getResult();

            if (buildResult.isBetterOrEqualTo(Result.SUCCESS)) {
                result.setProcessStatus(PhaseStatus.SUCCESS);

            } else if (buildResult.isBetterOrEqualTo(Result.UNSTABLE) || buildResult.isBetterOrEqualTo(Result.FAILURE)) {
                result.setProcessStatus(PhaseStatus.FAILURE);
            } else {
                // In case of NOT_BUILT or ABORTED
                result.setProcessStatus(PhaseStatus.ERROR);
            }

            // Get link of build
            String buildUrl = jenkins.getRootUrl() + job.getBuild().getUrl();
            des.append(buildUrl).append("\n");
            details.put(PROCESS_RESULT_KEY, des.toString());

        } catch (Exception e) {
            e.printStackTrace(listener.getLogger());
            result.setProcessStatus(PhaseStatus.ERROR);
            des.append("Error when starting ").append(result.getName()).append(", Exception: ").append(e.getMessage());
            details.put(PROCESS_RESULT_KEY, des.toString());
        }

        result.setDetails(details);
        return result;
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
            return "ILES: Provisioning Build Scheduler";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            save();
            return super.configure(req, json);
        }
    }
}
