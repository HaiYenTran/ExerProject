package com.ericsson.becrux.iles.testexec;

import com.ericsson.becrux.base.common.core.CommonParamenterValue;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.core.NwftDownstreamJob;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.loop.ComponentParameterValue;
import com.ericsson.becrux.base.common.loop.JobsScheduler;
import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.base.common.loop.ResultParameterValue;
import com.ericsson.becrux.base.common.testexec.TestStatus;
import com.ericsson.becrux.base.common.vise.parameters.ReservedViseChannelParameterValue;
import com.ericsson.becrux.iles.eventhandler.strategies.ITREventStrategy;
import com.ericsson.becrux.iles.leo.parameters.InitLeoParameterValue;
import com.ericsson.becrux.iles.data.Int;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.junit.TestResultAction;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.*;

/**
 *
 * This class is responsible for a build step that schedules the
 * "TESTEXEC" downstream job.
 * Created by ematwie on 2016-12-13.
 */
public class TestExecutionSchedulerBuildStep extends NwftBuildStep {

    private int delay;
    private long timeout;
    private String jobName;
    private static String PROCESS_NAME = "TESTEXEC";
    private static String PROCESS_RESULT_KEY = "LINK";
    /**
     * Constructor.
     * @param delay
     * @param timeout
     * @param jobName Name of the downstream job to be scheduled
     */
    @DataBoundConstructor
    public TestExecutionSchedulerBuildStep(String delay, String timeout, String jobName) {
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

    /** {@inheritDoc} */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        ResultParameterValue result = startTestExecution(build, launcher, listener);
        addNwftBuildParameter(build, result);

        if (result.getProcessStatus().equals(PhaseStatus.SUCCESS) ||
                result.getProcessStatus().equals(PhaseStatus.SKIPPED) ||
                result.getProcessStatus().equals(PhaseStatus.UNSTABLE)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Starts the test execution for INT node. Gets the reserved VISe channel and Leo parameters from
     * the build and run the job scheduler to execute "TESTEXEC" downstream job.
     * @param build Base implementation that runs build
     * @param launcher Launches the process
     * @param listener Receives events that happen during a build
     * @return ResultParameterValue with status (Error/Success) of process execution
     */
    private ResultParameterValue startTestExecution (AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {

        // Initialize an empty ResultParameterValue
        Map<String, String> details = new TreeMap<>();
        Map<TestStatus, Integer> testScore = new TreeMap<>();

        ResultParameterValue result = new ResultParameterValue(PROCESS_NAME, PhaseStatus.SUCCESS, details, testScore);
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

            List<Action> actions = new LinkedList<>();
            NwftParametersAction params = new NwftParametersAction();

            for (CommonParamenterValue param : getAllNwftParametersOfType(build, CommonParamenterValue.class)) {
                if (param.getName().equals(ITREventStrategy.TESTEXEC_ABLE) && param.getValue().equals("false")) {
                    listener.getLogger().println("-> The " + PROCESS_NAME + " step was skipped by user.");

                    // Update ResultParameterValue
                    result.setProcessStatus(PhaseStatus.SKIPPED);
                    des.append("The " + PROCESS_NAME + " step was skipped by user.");
                    details.put(PROCESS_RESULT_KEY, des.toString());
                    result.setDetails(details);
                    return result;

                }

                //get config properties from ITR Strategy
                if (param.getName().equals(ITREventStrategy.CONFIG_PROPERTIES)) {
                    params.addParam(param);
                }
            }

            List<ComponentParameterValue> components = new LinkedList<>();

            for (ComponentParameterValue param : getAllNwftParametersOfType(build, ComponentParameterValue.class)) {
                if (param.getComponent() instanceof Int)
                    components.add(param);
            }

            if (components.size() < 1) {
                listener.getLogger().println("No INT test suite data in build parameters.");
                throw new Exception("No INT test suite data in build parameters.");
            } else if (components.size() > 1) {
                listener.getLogger().println("More than one INT test suite data parameter provided.");
                throw new Exception("More than one INT test suite data parameter provided.");
            } else {
                params.addParam(components.get(0));
            }

            List<ReservedViseChannelParameterValue> channels = getAllNwftParametersOfType(build, ReservedViseChannelParameterValue.class);

            if (channels.size() < 1) {
                listener.getLogger().println("No VISE value in build parameters.");
                throw new Exception("No VISE value in build parameters.");
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

            if (job.getBuild() == null) {
                //build is not yet initiated
                throw new Exception("The build has not initiated yet!");
            }

            TestResultAction action = job.getBuild().getAction(TestResultAction.class);
            if (action == null || action.getResult() == null) {
                throw new Exception("TestResultAction is null");
            }

            // Update ResultParameterValue
            Result buildResult = build.getResult();

            if (buildResult.isBetterOrEqualTo(Result.SUCCESS)) {
                result.setProcessStatus(PhaseStatus.SUCCESS);

            } else if (buildResult.isBetterOrEqualTo(Result.UNSTABLE)) {
                result.setProcessStatus(PhaseStatus.UNSTABLE);

            } else if (buildResult.isBetterOrEqualTo(Result.FAILURE)) {
                result.setProcessStatus(PhaseStatus.FAILURE);

            } else {
                // In case of NOT_BUILT or ABORTED
                result.setProcessStatus(PhaseStatus.ERROR);
            }

            // Get link of build
            String buildUrl = jenkins.getRootUrl() + job.getBuild().getUrl();
            des.append(buildUrl);
            details.put(PROCESS_RESULT_KEY, des.toString());

            // Get testScore
            testScore.put(TestStatus.TOTAL, action.getResult().getTotalCount());
            testScore.put(TestStatus.PASSED, action.getResult().getPassCount());
            testScore.put(TestStatus.FAILED, action.getResult().getFailCount());
            testScore.put(TestStatus.SKIPPED, action.getResult().getSkipCount());

        } catch (Exception e) {
            e.printStackTrace(listener.getLogger());
            result.setProcessStatus(PhaseStatus.ERROR);
            des.append("Error when starting ").append(result.getName()).append(", Exception: ").append(e.getMessage());
            details.put(PROCESS_RESULT_KEY, des.toString());
        }
        result.setDetails(details);
        result.setTestScore(testScore);
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
            return "ILES: Test Execution Build Scheduler";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            save();
            return super.configure(req, json);
        }
    }
}
