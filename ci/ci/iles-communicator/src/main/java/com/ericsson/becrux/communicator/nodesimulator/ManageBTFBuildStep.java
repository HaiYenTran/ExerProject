package com.ericsson.becrux.communicator.nodesimulator;

import com.ericsson.becrux.base.common.core.NwftDownstreamJob;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.dao.EventDao;
import com.ericsson.becrux.base.common.dao.filedb.JsonEventDao;
import com.ericsson.becrux.base.common.eiffel.EiffelEventConverter;
import com.ericsson.becrux.base.common.eiffel.EiffelEventReceiver;
import com.ericsson.becrux.base.common.eiffel.configuration.SecondaryBinding;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.base.common.eventhandler.EventHandlingResult;
import com.ericsson.becrux.base.common.utils.BecruxBuildBadgeAction;
import com.ericsson.becrux.base.common.utils.Timestamped;
import com.ericsson.becrux.communicator.eiffel.events.CommunicatorEventFactory;
import com.ericsson.becrux.communicator.eiffel.events.EventParamenterValue;
import com.google.gson.Gson;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.collections.FastTreeMap;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class helps to manage incoming BTF events, save BTF to DB or use it to trigger another job.
 */
public class ManageBTFBuildStep extends Builder implements BuildStep {

    private final String tag;
    private final List<SecondaryBinding> bindingKeys;
    private final String eventDao;
    private final String eventQueue;

    @DataBoundConstructor
    public ManageBTFBuildStep(@Nonnull String tag, @Nonnull List<SecondaryBinding> bindingKeys, @Nonnull String eventDao, @Nonnull String eventQueue) {
        this.tag = tag;
        this.bindingKeys = bindingKeys;
        this.eventDao = eventDao;
        this.eventQueue = eventQueue;
    }

    public String getTag() {
        return tag;
    }

    public List<SecondaryBinding> getBindingKeys() {
        return bindingKeys;
    }

    public String getEventDao() {
        return eventDao;
    }

    public String getEventQueue() { return eventQueue; }

    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        EiffelEventReceiver receiver = null;
        Map<BTFEvent.BtfType, Integer> btfScore = new FastTreeMap();
        int startLoopBTFCount = 0;
        int endLoopBTFCount = 0;
        int rejectBTFCount = 0;
        int waitingBTFCount = 0;
        int restartBTFCount = 0;

        //Use HashSet to remove all duplicate job name
        HashSet<String> notExistJobs = new HashSet<String>();

        try {
            EventDao dao = new JsonEventDao(getEventDao(), CommunicatorEventFactory.getInstance());
            listener.getLogger().println("EVENT DAO : " + dao.getPath());
            dao.checkSynchronizeLock();

            List<Event> btfEvents = new ArrayList<>();

            // Created a queue with name:
            // <domainId>.<componentId>.<tag>-managebtfbuildstep.durable
            receiver = new EiffelEventReceiver(getTag().toLowerCase(), null, true,
                    getTag().toLowerCase()+ "-" + this.getClass().getSimpleName().toLowerCase(), getBindingKeys(),
                    new EiffelEventConverter(CommunicatorEventFactory.getInstance()));

            receiver.start();
            Thread.sleep(5000);
            receiver.stop();
            List<Timestamped<BTFEvent>> BTFReceiver = receiver.getEventQueue()
                    .stream()
                    .filter(e -> e instanceof BTFEvent)
                    .map(e -> (BTFEvent) e)
                    .map(e -> new Timestamped<BTFEvent>(e))
                    .collect(Collectors.toList());

            if (BTFReceiver.size() > 0) {
                for (Timestamped<BTFEvent> e : BTFReceiver) {
                    BTFEvent btfEvent = e.getObject();

                    // Count number of each BTF event type
                    switch (btfEvent.getBtfType()) {
                        case STARTLOOP:
                            ++startLoopBTFCount;
                            break;
                        case ENDLOOP:
                            ++endLoopBTFCount;
                            break;
                        case REJECT:
                            ++rejectBTFCount;
                            break;
                        case WAITING:
                            ++waitingBTFCount;
                            break;
                        case RESTART:
                            ++restartBTFCount;;
                            break;
                        default:
                            listener.getLogger().println("\nWARNING: Unknown BTF Event Type " + btfEvent.getBtfType());
                            break;
                    }

                    // Display BTF event
                    listener.getLogger().println("\n\n*" + e.getDate() + " | Receive BTF event:\n" + new Gson().toJson(btfEvent));

                    // Trigger requester job if BTF type = RESTART or store to event DAO
                    if (btfEvent.getBtfType().equals(BTFEvent.BtfType.RESTART)) {
                        String jobTriggered = btfEvent.getRequester();

                        // Check if the Job name is exist
                        if(Jenkins.getInstance().getJobNames().contains(jobTriggered)) {
                            listener.getLogger().println("\n>>> This BTF event with type " + btfEvent.getBtfType() + " will trigger the " + jobTriggered + " job !!!\n");
                            NwftParametersAction action = new NwftParametersAction();
                            action.addParam(new EventParamenterValue(btfEvent));
                            NwftDownstreamJob triggeredJob = new NwftDownstreamJob(jobTriggered, Arrays.asList(action), new Cause.UpstreamCause(build), 0, 0);
                            triggeredJob.schedule();
                        }
                        else {
                            listener.getLogger().println("\n>>> ERROR: The " + jobTriggered + " job is not existed in Jenkins!!!\n");
                            notExistJobs.add(jobTriggered);
                        }
                    }
                    else {
                        listener.getLogger().println("\n>>> This BTF event with type " + btfEvent.getBtfType() + " will be stored in " + getEventQueue() + " queue !!!\n");
                    }
                    // TODO: workaround for RESTART loop, send BTF event when trigger the sender job to get job ID
                    // TODO: and also save this event to database to the Query BTF event can find in database to break the while loop
                    btfEvents.add(btfEvent);
                }

                // Add value to btfScore
                btfScore.put(BTFEvent.BtfType.STARTLOOP, startLoopBTFCount);
                btfScore.put(BTFEvent.BtfType.WAITING, waitingBTFCount);
                btfScore.put(BTFEvent.BtfType.REJECT, rejectBTFCount);
                btfScore.put(BTFEvent.BtfType.ENDLOOP, endLoopBTFCount);
                btfScore.put(BTFEvent.BtfType.RESTART, restartBTFCount);
                listener.getLogger().println("*****Summary BTF events: " + btfScore);

                // Saves BTF events to eventDAO
                dao.addEventToQueue(getEventQueue(), btfEvents);

            }
            // Show badge info
            addEventsBadge(build, BTFReceiver.size(), btfScore.toString());

        } catch (Exception ex) {
            ex.printStackTrace(listener.getLogger());
            return false;
        } finally {
            if (receiver != null) {
                try {
                    receiver.close();
                } catch (Exception e) {
                    e.printStackTrace(listener.getLogger());
                }
            }
        }
        // Check to see how many triggered jobs not exist, set build FAILURE
        if (!notExistJobs.isEmpty()) {
            listener.getLogger().println("\n>>> ERROR: Found some triggered jobs not exist in Jenkins: " + notExistJobs);
            return false;
        }

        return true;
    }

    /**
     * Add badge to the job build after the job completes
     * @param build
     * @param eventSize
     * @param msg
     * @throws IOException
     */
    protected void addEventsBadge(AbstractBuild<?, ?> build, int eventSize, String msg) throws IOException {
        build.addAction(new BecruxBuildBadgeAction(Integer.toString(eventSize)));
        build.setDescription(msg);
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @SuppressWarnings("rawtypes")
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "ILES-Communicator: Manage BTF Event";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
