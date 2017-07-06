package com.ericsson.becrux.iles.loop;

import com.ericsson.becrux.base.common.core.NwftDownstreamJob;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.core.NwftPostBuildStep;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.eiffel.parameters.EiffelEventParameterValue;
import com.ericsson.becrux.base.common.eiffel.parameters.EiffelEventParameterValue_ToSend;
import com.ericsson.becrux.base.common.loop.ComponentParameterValue;
import com.ericsson.becrux.base.common.loop.Phase;
import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.base.common.loop.ResultParameterValue;
import com.ericsson.becrux.base.common.testexec.TestStatus;
import com.ericsson.becrux.base.common.vise.parameters.ReservedViseChannelParameterValue;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.iles.data.IlesImsBaseline;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.iles.eiffel.events.EventParamenterValue;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import com.ericsson.becrux.iles.eventhandler.strategies.ITREventStrategy;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import static com.ericsson.becrux.iles.eventhandler.strategies.ITREventStrategy.ITR_SENDER_JOB_NAME;
/**
 * Loop Feedback Baseline post build step is to update all information about Phoenix, Provisioning, TestExec
 * to a BTF event. Triggers the downstream job to send BTF event to IMS nodes.
 */
public class LoopFeedbacksUpdaterPostBuildStep extends NwftPostBuildStep {

    private String senderJob;

    @DataBoundConstructor
    public LoopFeedbacksUpdaterPostBuildStep(String senderJob) {
        super();
        this.senderJob = senderJob;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
            throws InterruptedException, IOException {

        // BTF checklist: Use to check to see if BTF parameters have been created/updated
        // - Products
        // - Baselines
        // - VISE channel used
        // - Phase
        // - Phase Status
        // - Message
        // - Test scores
        // - Results
        // - JobID
        // - New component details, ie MTAS: R7A05
        listener.getLogger().println("\n-> Starting Loop Feedback Updater");

        // Create/update BTF event
        List<EiffelEventParameterValue> eventParams = getAllNwftParametersOfType(build, EiffelEventParameterValue.class)
                .stream().filter(param -> param.getEvent() instanceof BTFEvent).collect(Collectors.toList());

        BTFEvent btf;
        if (eventParams.size() <= 0) {
            // Create an empty BTF event
            btf = new BTFEvent();
        } else if (eventParams.size() > 1) {
            listener.getLogger().println("Error: more than one BTF event parameter value found.");
            return false;
        } else {
            btf = (BTFEvent)eventParams.get(0).getEvent();
        }

        // Update BTF event
        Map<TestStatus, Integer> testScores = new TreeMap<>();
        StringBuilder logResult = new StringBuilder();
        logResult.append("\n");

        List<ComponentParameterValue> components = getAllNwftParametersOfType(build, ComponentParameterValue.class);
        if (components != null) {
            List<String> products = new ArrayList<>();
            List<String> versions = new ArrayList<>();
            for (ComponentParameterValue c : components) {
                if (c.getValue() != null) {
                    products.add(c.getComponent().getType());
                    versions.add(c.getComponent().getVersion());
                }
            }
            // Set BTF products, baselines
            btf.setProducts(products);
            btf.setBaselines(versions);
        }

        // Get vise channel used in loop
        ReservedViseChannelParameterValue channel = null;
        List<ReservedViseChannelParameterValue> channels = getAllNwftParametersOfType(build, ReservedViseChannelParameterValue.class);
        if (channels != null) {
            channel = channels.get(0);
        }

        // Set BTF VISE channel
        btf.setViseChannel(channel.getViseChannel().getFullName());

        // Set BTF Phase and PhaseStatus
        btf.setPhase(Phase.FINALIZING);

        List<ResultParameterValue> resultParams = getAllNwftParametersOfType(build, ResultParameterValue.class);
        if (resultParams == null) {
            // None of (INSTALLATION, PROVISIONING, TESTEXEC) started.
            btf.setPhaseStatus(PhaseStatus.ERROR);
            btf.setMessage("Loop has not started yet!");
        } else {

            // Count process status
            int errorProcesses = 0;
            int failureProcesses = 0;
            int unstableProcesses = 0;

            // Detect failure at step
            List<String> errorSteps = new LinkedList<>();
            List<String> failureSteps = new LinkedList<>();
            List<String> unstableSteps = new LinkedList<>();

            for (ResultParameterValue para : resultParams) {
                switch (para.getProcessStatus()) {
                    case ERROR:
                        errorSteps.add(para.getName());
                        ++errorProcesses;
                    case FAILURE:
                        failureSteps.add(para.getName());
                        ++failureProcesses;
                    case UNSTABLE:
                        unstableSteps.add(para.getName());
                        ++unstableProcesses;
                    default:
                        break;
                }
            }

            // Correct BTF PhaseStatus and Phase
            if (errorProcesses > 0) {
                btf.setPhaseStatus(PhaseStatus.ERROR);
                btf.setMessage("An error occurred when running loop at step: " + errorSteps);
            } else if (failureProcesses > 0) {
                btf.setPhaseStatus(PhaseStatus.FAILURE);
                btf.setMessage("Loop run failed at step: " + failureSteps);
            } else if (unstableProcesses > 0) {
                btf.setPhaseStatus(PhaseStatus.UNSTABLE);
                btf.setMessage("Loop run unstable at step: " + unstableSteps);
            } else {
                btf.setPhaseStatus(PhaseStatus.SUCCESS);
                btf.setMessage("Loop run successfully!");
            }

            // Get result logs
            listener.getLogger().println("\n====>>> Summary some information <<<====");
            for (ResultParameterValue para: resultParams) {
                para.getDetails().forEach((key,value)-> {
                    logResult.append("\n").append(para.getName()).append(": ").append(para.getProcessStatus())
                            .append("\n").append(key).append(": ").append(value);
                });
                // Get testScore
                if (para.getTestScore() != null) {
                    testScores.putAll(para.getTestScore());
                }
                //get the Source folder
                if (para.getSourcePath() != null) {
                    listener.getLogger().println("\n\tThe source folder of " + para.getName() + " is " + para.getSourcePath());
                }
            }

            listener.getLogger().println("\n====>>> Finished summary some information <<<====");

        }

        // Correct build status for the job included this post buildstep
        // We consider the build status is UNSTABLE if one of buildstep return UNSTABLE
        if (btf.getPhaseStatus().equals(PhaseStatus.UNSTABLE)) {
            build.setResult(Result.UNSTABLE);
        }

        // BTF set testScore
        btf.setTestScores(testScores);

        // Set BTF Results
        btf.setResults(Arrays.asList(logResult.toString()));

        // Set BTF JobID in LEO
        btf.setJobId(0); //TODO: Update JobId for LEO, use for voting

        // Set BTF Type = ENDLOOP
        btf.setBtfType(BTFEvent.BtfType.ENDLOOP);

        //Set BTF ID match with ITR ID
        btf.setBtfId(getFirstITREventFromBuild(build).getID());

        // Add ITR Job Requester to BTF event to send back to client
        btf.setRequester(getItrSenderJobName(build));

        listener.getLogger().println("\n-> Finished Loop Feedback Updater");

        // Update baselineProcessing, change LoopRunning = false
        IlesImsBaseline baselineProcessing = IlesGlobalConfig.getInstance().getProcessingBaseline();
        baselineProcessing.setLoopRunning(false);
        IlesGlobalConfig.getInstance().setProcessingBaseline(baselineProcessing);

        // Save this BTF event to EiffelEventParameterValue_ToSend so that it will be sent back to client
        List<Action> actions = new LinkedList<>();
        NwftParametersAction action = new NwftParametersAction();
        EiffelEventParameterValue_ToSend param = new EiffelEventParameterValue_ToSend(btf.getClass().getSimpleName(), btf);

        // Set tag in routing key
        param.setTag(getTagFromBuild(build));
        listener.getLogger().println("\n-> Updated TAG_IN_ROUTING_KEY to send BTF event back: " + param.getTag());

        action.addParam(param);
        actions.add(action);

        // Trigger Eiffel Sender job to send BTF back
        if (senderJob == null || senderJob.isEmpty()) {
            listener.getLogger().println("\n-> ERROR: Sender Job is empty!");
            return false;
        } else {
            // List all jobs in Jenkins
            List<String> jobs = (List<String>)Jenkins.getInstance().getJobNames();
            if (jobs.contains(senderJob)) {
                listener.getLogger().println("\n-> Triggered [" + senderJob + "] to send BTF event back!");
                NwftDownstreamJob triggeredJob = new NwftDownstreamJob(senderJob, actions, new Cause.UpstreamCause(build), 0, 0);
                triggeredJob.schedule();
            } else {
                listener.getLogger().println("\n-> ERROR: " + senderJob + " is not exist!");
                return false;
            }
        }
        return true;
    }

    private ITREvent getFirstITREventFromBuild(final AbstractBuild<?, ?> build) {
        // try to get tag from ITR event
        List<EventParamenterValue> eventParams = getAllNwftParametersOfType(build, EventParamenterValue.class);
        // get only the first ITR event
        ITREvent eITR = (ITREvent)eventParams.stream().filter(e -> e.getEvent() instanceof ITREvent).findFirst().get().getEvent();
        return eITR;
    }

    /**
     * Find the tag to add to routing key.
     * The tag is Product Name or EVENT_TAG from ITR event
     * @param build
     * @return
     */
    private String getTagFromBuild(final AbstractBuild<?, ?> build) {
        ITREvent eITR = getFirstITREventFromBuild(build);
        if (eITR != null) {
            String eventTag = eITR.getParameters().get(ITREventStrategy.EVENT_TAG);
            if (eventTag != null)
                return eventTag;
            else
                return eITR.getProduct().toLowerCase();
            // WARNING: If there is no EVENT_TAG in ITR event, the tag = Product Name (production env) or "unknown" (standalone env)
        }

        // This is way to get tag in Production env. The tag is Product Name which have status != BASELINE_APPROVED
        // TODO: Need a clear/reliable detection between Production env and Standalone env
        List<ComponentParameterValue> components = getAllNwftParametersOfType(build, ComponentParameterValue.class);
        for (ComponentParameterValue c : components)
            if (c.getValue() != null)
                if (c.getComponent().getState() != Component.State.BASELINE_APPROVED)
                    return c.getComponent().getType().toLowerCase();

        return null;
    }

    /**
     * Get the client Job Name which sent ITR to ILES CI
     * @return - Name of Job which sent ITR to ILES CI
     */
    private String getItrSenderJobName(final AbstractBuild<?, ?> build) {
        ITREvent eITR = getFirstITREventFromBuild(build);
        if (eITR.getParameters().containsKey(ITR_SENDER_JOB_NAME)) {
            return eITR.getParameters().get(ITR_SENDER_JOB_NAME);
        } else {
            return null;
        }
    }
    public String getSenderJob() {
        return senderJob;
    }

    public void setSenderJob(String senderJob) {
        this.senderJob = senderJob;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "ILES: Loop Feedback Updater";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
