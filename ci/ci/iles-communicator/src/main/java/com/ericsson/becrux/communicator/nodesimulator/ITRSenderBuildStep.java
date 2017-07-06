package com.ericsson.becrux.communicator.nodesimulator;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.eiffel.EiffelEventSender;
import com.ericsson.becrux.base.common.utils.StringHelper;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.communicator.eiffel.events.CommunicatorEventFactory;
import com.ericsson.becrux.communicator.eiffel.events.EventParamenterValue;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import com.ericsson.duraci.eiffelmessage.sending.exceptions.EiffelMessageSenderException;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This Build step help to send ITR event with the specific tag
 * Default tag: iles
 */
public class ITRSenderBuildStep extends NwftBuildStep {

    private static String TAG_IN_ROUTING_KEY_DEFAULT = "iles";
    private static String ITR_SENDER_FAIL = "Status: ITR Sent Failed";
    private static String TRIGGERED_BY = "Triggered by ";
    private static String JOB_DESCRIPTION = "JOB_DESCRIPTION";
    private String tag;
    private boolean customTag;
    private String nodeType;
    private String version;
    private String loopType;
    private List<Parameters> parameters;// we will convert to map in the ITR event
    private String jobId;

    @DataBoundConstructor
    public ITRSenderBuildStep(String nodeType, String version, String loopType, List<Parameters> parameters, String jobId, String tag, boolean customTag) {
        this.nodeType = nodeType;
        this.version = version;
        this.loopType = loopType;
        this.parameters = parameters;
        this.jobId = jobId;
        this.tag = tag;
        this.customTag = customTag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isCustomTag() {
        return customTag;
    }

    public void setCustomTag(boolean customTag) {
        this.customTag = customTag;
    }

    public String getLoopType() {
        return loopType;
    }

    public void setLoopType(String loopType) {
        this.loopType = loopType;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Parameters> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameters> parameters) {
        this.parameters = parameters;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        //Get evn value from build
        //Map<String, String> buildParam = build.getBuildVariables();
        Map<String, String> buildParam = build.getEnvVars();
        String nodeTypeVar = StringHelper.handleString(buildParam, nodeType);
        String versionVar = StringHelper.handleString(buildParam, version);
        String loopTypeVar = StringHelper.handleString(buildParam, loopType);
        String jobIdVar = StringHelper.handleString(buildParam, jobId);
        // Get the JOB_DESCRIPTION from build variables, don't user parameter list
        String jobDescription = build.getBuildVariables().get(JOB_DESCRIPTION);

        String badgeMsg = "";
        String massageFail = "";
        if (jobDescription != null && !jobDescription.isEmpty()) {
            badgeMsg = jobDescription;
        }

        if(!checkExecutionCondition(build)) {
            listener.getLogger().println(getDescriptor().getDisplayName() + " is skipped.");
            if (!badgeMsg.isEmpty()) {
                build.setDescription(badgeMsg);
            }
            return true;
        }

//        // Get user trigger, backup
//        String userTrigger = "";
//        Cause.UserIdCause userIdCause = build.getCause(hudson.model.Cause.UserIdCause.class);
//        Cause.UpstreamCause upstreamCause = build.getCause(hudson.model.Cause.UpstreamCause.class);
//        if (userIdCause != null) {
//            userTrigger = userIdCause.getUserName();
//        }
//        else if (upstreamCause != null) {
//            userTrigger = upstreamCause.getUpstreamProject() + " with build " + upstreamCause.getUpstreamBuild();
//        }
//        if (!userTrigger.isEmpty()) {
//            badgeMsg = badgeMsg + "<br>" + TRIGGERED_BY + userTrigger;
//        }

        if (!badgeMsg.isEmpty()) {
            massageFail = badgeMsg + "<br>" + ITR_SENDER_FAIL;
        }
        else {
            massageFail = ITR_SENDER_FAIL;
        }

        // Check if customTag is chosen
        String currentTag = this.tag;
        if (!customTag || (customTag && (tag == null || tag.isEmpty()))) {
            currentTag = TAG_IN_ROUTING_KEY_DEFAULT;
        }

        Map<String, String> map = new HashMap<>();
        if (getParameters() != null && !getParameters().isEmpty()) {
            this.getParameters().stream().filter( p -> checkParamCondition(buildParam, p.getCondition(), p.isExtendedCondition()))
                    .forEach(m-> map.put(StringHelper.handleString(buildParam, m.getKey()),StringHelper.handleString(buildParam, m.getValue())));
        }

        // Create ITR event
        ITREvent event = new ITREvent();
        event.setProduct(nodeTypeVar);

        event.setBaseline(versionVar);

        event.setLoopType(ITREvent.LoopType.valueOf(loopTypeVar));
        if (!jobIdVar.isEmpty())
            try {
                event.setJobId(new Long(jobIdVar));
            } catch (NumberFormatException e) {
                listener.getLogger().println("ERROR: Invalid Job ID");
                build.setDescription(massageFail);
                return false;
            }

        event.setParameters(map);

        // Sending event
        try (EiffelEventSender sender = new EiffelEventSender()) {
            // Print event in details
            listener.getLogger().println("\n>>Sending ITR event:");
            if (!ITREvent.STANDALONE_TEST_BASELINE_CONFIG.equals(event.getProduct())) {
                listener.getLogger().println("   Product Type: " + event.getProduct());
            }

            if (!ITREvent.STANDALONE_TEST_BASELINE_CONFIG.equals(versionVar)) {
                listener.getLogger().println("   Version: " + event.getBaseline());
            }

            listener.getLogger().println("   Loop Type: " + event.getLoopType());
            listener.getLogger().println("   JobID in LEO: " + event.getJobId());
            listener.getLogger().println("   Parameter: ");
            event.getParameters().forEach((k,v)-> listener.getLogger().println("\t" + k + ": " + v));
            listener.getLogger().println("   TAG_IN_ROUTING_KEY: " + currentTag);
            listener.getLogger().println("   Requester ID: " + event.getID());
            listener.getLogger().println("\n");

            try {
                sender.sendEvent(event, currentTag);
                if (!badgeMsg.isEmpty()) {
                    build.setDescription(badgeMsg);
                }

                // Add the ITR Event to Build params
                addNwftBuildParameter(build, new EventParamenterValue(event));

                return true;
            } catch (EiffelMessageSenderException ex) {
                ex.printStackTrace(listener.getLogger());
                listener.getLogger().println("Error when sending event: " + CommunicatorEventFactory.getInstance().toJson(event));
                build.setDescription(massageFail);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace(listener.getLogger());
            build.setDescription(massageFail);
            return false;
        }
    }

    /**
     * This function will decide if @ITRSenderBuildStep is executed.
     * @param build
     * @return
     */
    private boolean checkExecutionCondition(AbstractBuild<?, ?> build) {
        List<EventParamenterValue> events = getAllNwftParametersOfType(build, EventParamenterValue.class);

        // Get all BTF events in build params
        List<BTFEvent> btfEvents = events.stream().filter(value -> value.getEvent() instanceof BTFEvent)
                .map(value -> (BTFEvent)value.getEvent())
                .collect(Collectors.toList());

        if (btfEvents.stream().filter(btf ->  BTFEvent.BtfType.RESTART.equals(btf.getBtfType())).findAny().isPresent()){
            return false;
        }

        return true;
    }

    /**
     * This function is to check chosen extended condition and the condition for the buildParam is true or false
     * @param buildParam
     * @param condition
     * @param extendedCondition
     * @return
     */
    private boolean checkParamCondition(Map<String, String> buildParam, String condition, boolean extendedCondition) {
        if (!extendedCondition || condition == null  || condition.isEmpty()) { return true; }

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("javascript");
        try {
            return (Boolean) engine.eval(StringHelper.handleString(buildParam , condition));
        } catch (Exception e) {
            return false;
        }
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
            return "ILES-Communicator: ITR Sender";
        }
        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            save();
            return super.configure(req, json);
        }
    }
}
