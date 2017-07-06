package com.ericsson.becrux.communicator.nodesimulator;

import com.ericsson.becrux.base.common.core.NwftPostBuildStep;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.communicator.eiffel.events.EventParamenterValue;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This build step adds badge information in the build history
 * depending upon the received event, looptype and phase status.
 * Created by thien.d.vu on 5/5/2017.
 */
public class MakeBadgePostBuildStep extends NwftPostBuildStep {

    @DataBoundConstructor
    public MakeBadgePostBuildStep() {
        super();
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
            throws InterruptedException, IOException {
        List<EventParamenterValue> btfEventParams = getAllNwftParametersOfType(build, EventParamenterValue.class)
                .stream().filter(param -> param.getEvent() instanceof BTFEvent).collect(Collectors.toList());
        List<EventParamenterValue> itrEventParams = getAllNwftParametersOfType(build, EventParamenterValue.class)
                .stream().filter(param -> param.getEvent() instanceof ITREvent).collect(Collectors.toList());

        // Check if ITR event was not sent successfully
        if (btfEventParams.isEmpty() && itrEventParams.isEmpty()) {
            // No need badge info since ITR sender buildstep has added.
            return false;
        }

        // Check if it doesn't receive enough BTF event
        if (!isReceivedEnoughBTF(btfEventParams)) {
            addEventsBadge(build, "Status: ERROR<br>Not receive enough BTF");
            build.setResult(Result.FAILURE);
            return false;
        }

        // FAILURE: BTF REJECT, ENDLOOP with phaseStatus=ERROR/FAILURE
        // UNSTABLE: BTF ENDLOOP with phaseStatus=UNSTABLE
        // SUCCESS: BTF WAITING, ENDLOOP with phaseStatus=SUCCESS.
        for (EventParamenterValue eventParamenterValue : btfEventParams) {
            BTFEvent btfEvent = (BTFEvent) eventParamenterValue.getEvent();
            if (btfEvent.getBtfType().equals(BTFEvent.BtfType.REJECT)) {
                addEventsBadge(build, "Status: REJECTED<br>" + btfEvent.getMessage());
                build.setResult(Result.FAILURE);
            }
            else if (btfEvent.getBtfType().equals(BTFEvent.BtfType.WAITING)) {
                addEventsBadge(build, "Status: WAITING<br>" + btfEvent.getMessage());
                build.setResult(Result.SUCCESS);
            }
            else if (btfEvent.getBtfType().equals(BTFEvent.BtfType.ENDLOOP)) {
                addEventsBadge(build, "Status: FINISHED");
                if (!btfEvent.getTestScores().isEmpty()) {
                    addEventsBadge(build, "Test Scores" + btfEvent.getTestScores());
                }
                if (btfEvent.getPhaseStatus().equals(PhaseStatus.ERROR) || btfEvent.getPhaseStatus().equals(PhaseStatus.FAILURE)) {
                    build.setResult(Result.FAILURE);
                }
                else if (btfEvent.getPhaseStatus().equals(PhaseStatus.UNSTABLE)) {
                    build.setResult(Result.UNSTABLE);
                }
                else if (btfEvent.getPhaseStatus().equals(PhaseStatus.SUCCESS)) {
                    build.setResult(Result.SUCCESS);
                }
            }
        }

        return true;
    }

    /**
     * Add badge to build history
     * @param build
     * @param msg
     * @throws IOException
     */
    private void addEventsBadge(AbstractBuild<?, ?> build, String msg) throws IOException {
        String oldMsg = build.getDescription();
        if (oldMsg != null && !oldMsg.isEmpty()) {
            msg = oldMsg + "<br>" + msg;
        }
        build.setDescription(msg);
    }

    /**
     * check this build was received enough BTF or not
     * @param eventParams
     * @throws IOException
     */
    private boolean isReceivedEnoughBTF(List<EventParamenterValue> eventParams) throws IOException {
        if (eventParams.isEmpty()) {
            // Not receive any BTF event
            return false;
        }
        else if (eventParams.size() == 1) {
            BTFEvent.BtfType btfType = ((BTFEvent)eventParams.get(0).getEvent()).getBtfType();
            if (btfType.equals(BTFEvent.BtfType.RESTART) || btfType.equals(BTFEvent.BtfType.STARTLOOP)) {
                // Not receive enough BTF event
                return false;
            }
        }
        return true;
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
            return "ILES-Communicator: Make Badge in build history";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
