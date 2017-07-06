package com.ericsson.becrux.iles.phoenix;

import com.ericsson.becrux.base.common.core.CommonParamenterValue;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.core.NwftDownstreamJob;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.base.common.loop.ResultParameterValue;
import com.ericsson.becrux.base.common.vise.parameters.ReservedViseChannelParameterValue;
import com.ericsson.becrux.iles.eventhandler.strategies.ITREventStrategy;
import com.ericsson.becrux.iles.leo.buildsteps.InitUnitRequestBuildStep;
import com.ericsson.becrux.iles.leo.buildsteps.UpdateUnitBuildStep;
import com.ericsson.becrux.iles.leo.parameters.InitLeoParameterValue;
import com.ericsson.becrux.base.common.loop.ComponentParameterValue;
import com.ericsson.becrux.base.common.loop.JobsScheduler;
import com.ericsson.becrux.iles.leo.parameters.NodesLeoParameterValue;
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
 * Created by ematwie on 2016-12-13.
 */
public class PhoenixSchedulerBuildStep extends NwftBuildStep {

    private int delay;
    private long timeout;
    private String jobName;
    private static String PROCESS_NAME = "INSTALLATION(PHOENIX)";
    private static String PROCESS_RESULT_KEY = "LINK";

    @DataBoundConstructor
    public PhoenixSchedulerBuildStep(String delay, String timeout, String jobName) {
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

        ResultParameterValue result = startPhoenix(build, launcher, listener);
        addNwftBuildParameter(build, result);

        if (result.getProcessStatus().equals(PhaseStatus.SUCCESS) ||
                result.getProcessStatus().equals(PhaseStatus.SKIPPED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Start installation for IMS node
     * @param build
     * @param launcher
     * @param listener
     * @return
     */
    private ResultParameterValue startPhoenix (AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {

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
                if (param.getName().equals(ITREventStrategy.INSTALLATION_ABLE) && param.getValue().equals("false")) {
                    listener.getLogger().println("-> The " + PROCESS_NAME + " step was skipped by user.");

                    // Update ResultParameterValue
                    result.setProcessStatus(PhaseStatus.SKIPPED);
                    des.append("The " + PROCESS_NAME + " step was skipped by user.");
                    details.put(PROCESS_RESULT_KEY, des.toString());
                    result.setDetails(details);
                    return result;
                }
            }

            List<ComponentParameterValue> components = new LinkedList<>();

            for (ComponentParameterValue param : getAllNwftParametersOfType(build, ComponentParameterValue.class)) {
                components.add(param);
            }

            if (components.size() == 0) {
                listener.getLogger().println("Nodes are not provided in build parameters.");
                throw new Exception("Nodes are not provided in build parameters.");
            } else {
                listener.getLogger().println("Provided nodes to install:");
                for (ComponentParameterValue param : components) {
                    Component n = param.getComponent();
                    listener.getLogger().println("* " + n.getType() + " " + n.getVersion().getVersion());
                }
            }

            ReservedViseChannelParameterValue channel;

            List<ReservedViseChannelParameterValue> channels = getAllNwftParametersOfType(build, ReservedViseChannelParameterValue.class);

            if (channels.size() < 1) {
                listener.getLogger().println("No VISE value in build parameters.");
                throw new Exception("No VISE value in build parameters");
            } else if (channels.size() > 1) {
                listener.getLogger().println("More than one VISE value provided.");
                throw new Exception("More than one VISE value provided.");
            } else {
                channel = channels.get(0);
            }

            NodesLeoParameterValue nodesLeoParam = null;

            List<NodesLeoParameterValue> nodesLeoParams = getAllNwftParametersOfType(build, NodesLeoParameterValue.class);

            if (nodesLeoParams.size() < 1) {
                listener.getLogger().println("Warning: No Leo nodes data in build parameters.");
            } else if (nodesLeoParams.size() > 1) {
                listener.getLogger().println("More than one Leo nodes data parameter provided.");
                throw new Exception("More than one Leo nodes data parameter provided.");
            } else {
                nodesLeoParam = nodesLeoParams.get(0);
            }

            InitLeoParameterValue leoParam = null;

            List<InitLeoParameterValue> leoParams = getAllNwftParametersOfType(build, InitLeoParameterValue.class);

            if (leoParams.size() < 1) {
                listener.getLogger().println("Warning: No Leo data in build parameters.");
            } else if (leoParams.size() > 1) {
                listener.getLogger().println("More than one Leo data parameter provided.");
                throw new Exception("More than one Leo data parameter provided.");
            } else {
                leoParam = leoParams.get(0);
            }

            Map<NwftDownstreamJob, JobsScheduler.Operation> jobsMap = new HashMap<>();
            StringBuilder downstreamJobLink = new StringBuilder();
            downstreamJobLink.append("\n");

            for (ComponentParameterValue nodeParam : components) {
                List<Action> actions = new LinkedList<>();
                NwftParametersAction action = new NwftParametersAction();
                Component node = nodeParam.getComponent();
                if (node.isInstallable()) {
                    String description = node.getType() + " " + node.getVersion().getVersion();

                    action.addParam(nodeParam);
                    action.addParam(channel);

                    if (leoParam != null && nodesLeoParam != null) {
                        InitLeoParameterValue newLeoParam = leoParam.clone();
                        newLeoParam.setUnitResponse(nodesLeoParam.getResponse(node.getType()));

                        action.addParam(newLeoParam);
                    }

                    actions.add(action);
                    jobsMap.put(new NwftDownstreamJob(getJobName(), description, actions, cause, getDelay(), getTimeout()), JobsScheduler.Operation.WAIT_FOR_FINISH);
                }
            }
            JobsScheduler scheduler = new JobsScheduler(listener.getLogger(), jobsMap);

            build.setResult(scheduler.run());

            if (leoParam != null && nodesLeoParam != null) {
                new InitUnitRequestBuildStep("Installation", "", "INSTALLATION", generateMapListResult().get(build.getResult().toString()), false, true).perform(build, launcher, listener);
                new UpdateUnitBuildStep().perform(build, launcher, listener);
            }

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
            jobsMap.forEach((key,value)->{
                // Add downstream job link for each component
                downstreamJobLink.append(key.getDescription()).append(": ")
                        .append(key.getBuild().getResult().toString()).append(", ")
                        .append(jenkins.getRootUrl() + key.getBuild().getUrl()).append("\n");
            });
            details.put(PROCESS_RESULT_KEY, downstreamJobLink.toString());

        } catch (Exception e) {
            e.printStackTrace(listener.getLogger());
            result.setProcessStatus(PhaseStatus.ERROR);
            des.append("Error when starting ").append(result.getName()).append(", Exception: ").append(e.getMessage());
            details.put(PROCESS_RESULT_KEY, des.toString());
        }

        result.setDetails(details);
        return result;
    }

    public static Map<String, String> generateMapListResult() {
        Map<String, String> mapList = new HashMap<>();
        mapList.put("SUCCESS", "FINISHED");
        mapList.put("FAILURE", "FAILED");
        mapList.put("ABORTED", "ABORTED");
        mapList.put("UNSTABLE", "ERROR");

        return mapList;
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
            return "ILES: Phoenix Installation Build Scheduler";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            save();
            return super.configure(req, json);
        }
    }
}
