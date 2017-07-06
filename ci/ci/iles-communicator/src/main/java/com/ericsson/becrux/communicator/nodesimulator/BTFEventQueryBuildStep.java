package com.ericsson.becrux.communicator.nodesimulator;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.dao.EventDao;
import com.ericsson.becrux.base.common.dao.filedb.JsonEventDao;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.communicator.eiffel.events.CommunicatorEventFactory;
import com.ericsson.becrux.communicator.eiffel.events.EventParamenterValue;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Query all BTF event with condition.
 */
public class BTFEventQueryBuildStep extends NwftBuildStep {

    private int delayTime = 2;
    private int waitingTime = 10;
    private List<String> btfEventReceivedType;
    private List<String> btfEventRejectType;
    private String eventDAOPath;
    private String queueName;
    private String instancePropertyName;

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(String delayTime) {
        if (delayTime != null && !delayTime.isEmpty()) { this.delayTime = Integer.valueOf(delayTime); }
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(String waitingTime) {
        if (waitingTime != null && !waitingTime.isEmpty()) { this.waitingTime = Integer.valueOf(waitingTime); }
    }

    public String getBtfEventRejectType() {
        StringBuilder builder = new StringBuilder();
        btfEventRejectType.forEach(t -> builder.append(t + ", " ));

        String value = builder.toString();
        value = value.substring(0, value.length() - 2);

        return btfEventRejectType.size() > 0 ? value : "";
    }

    public void setBtfEventRejectType(String btfEventRejectType) {
        this.btfEventRejectType = getTypes(btfEventRejectType);
    }

    public String getBtfEventReceivedType() {
        StringBuilder builder = new StringBuilder();
        btfEventReceivedType.forEach(t -> builder.append(t + ", " ));

        String value = builder.toString();
        value = value.substring(0, value.length() - 2);

        return btfEventReceivedType.size() > 0 ? value : "";
    }

    public void setBtfEventReceivedType(String btfEventReceivedType) {
        this.btfEventReceivedType = getTypes(btfEventReceivedType);
    }

    public String getEventDAOPath() {
        return eventDAOPath;
    }

    public void setEventDAOPath(String eventDAOPath) {
        this.eventDAOPath = eventDAOPath;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getInstancePropertyName() {
        return instancePropertyName;
    }

    public void setInstancePropertyName(String instancePropertyName) {
        this.instancePropertyName = instancePropertyName;
    }

    @DataBoundConstructor
    public BTFEventQueryBuildStep(String delayTime, String waitingTime, String btfEventReceivedType, String btfEventRejectType, String instancePropertyName, String eventDAOPath, String queueName) throws Exception {
        if (delayTime != null && !delayTime.isEmpty()) { this.delayTime = Integer.valueOf(delayTime); }
        if (waitingTime != null && !waitingTime.isEmpty()) { this.waitingTime = Integer.valueOf(waitingTime); }
        this.btfEventReceivedType = getTypes(btfEventReceivedType);
        this.btfEventRejectType = getTypes(btfEventRejectType);
        this.eventDAOPath = eventDAOPath;
        this.queueName = queueName;
        this.instancePropertyName = instancePropertyName;
    }

    /**
     * Get types from string
     * The String will have format: 'TYPE1, TYPE2, ...'
     * */
    private List<String> getTypes(String type) {
        type = type.replace(" ", "");

        List<String> types = Arrays.asList(type.split(","));

        return types;
    }

    @Override
    /**
     * Process:
     *  - Get the event ID in ITR or BTF event
     *  - Query in DB to get the event with above ID
     *  - Choose one BTF event. RESTART is highest priorities
     *  - Add buildLogURL to build parameter action
     *  - Print BTF event information
     *  - Check and stop the build if receive BTF reject type
     */
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        BTFEvent chosenBtfEvent = null;

        // Get the event ID in ITR or BTF event
        String eventID = getEventID(build);

        // Query in DB to get event with above ID
        List<Event> events = getAllBTFEventFromQueue(eventID);

        // Choose one BTF event. RESTART is highest priorities
        if (events.size() > 0) {
            List<Event> eventRestarts = new ArrayList<>();
            eventRestarts = events.stream()
                    .filter(e -> e instanceof BTFEvent && BTFEvent.BtfType.RESTART.equals(((BTFEvent) e).getBtfType()))
                    .collect(Collectors.toList());
            if (eventRestarts.size() > 0) {
                chosenBtfEvent = (BTFEvent) eventRestarts.get(0);
            }
            else {
                chosenBtfEvent = (BTFEvent) events.get(0);
            }
        }
        else {
            listener.getLogger().println("No BTF event received!");
            return false;
        }

        // Add buildLogURL to build parameter action
        int eventParamSize = getAllNwftParametersOfType(build, EventParamenterValue.class)
                .stream().map(e -> e.getEvent()).collect(Collectors.toList()).size();
        // Only add The Build log URL to parameter action at the beginning of each build
        if (eventParamSize == 1) {
            addInstanceBuildParams(build, instancePropertyName, chosenBtfEvent.getBuildLogUrl());
        }

        // Print BTF event information
        listener.getLogger().println("\n\n>>Received BTF Events: ");
        listener.getLogger().println("\n-->> BTF event type: " + chosenBtfEvent.getBtfType());
        listener.getLogger().println("\tRequester ID: " + chosenBtfEvent.getBtfId());
        listener.getLogger().println("\tPhase: " + chosenBtfEvent.getPhase());
        listener.getLogger().println("\tPhase Status: " + chosenBtfEvent.getPhaseStatus());
        listener.getLogger().println("\tMessage: " + chosenBtfEvent.getMessage());
        if (chosenBtfEvent.getTestScores() != null && !chosenBtfEvent.getTestScores().isEmpty()) {
            listener.getLogger().println("\tTest scores: " + chosenBtfEvent.getTestScores());
        }
        if (chosenBtfEvent.getBuildLogUrl() != null && !chosenBtfEvent.getBuildLogUrl().isEmpty()) {
            listener.getLogger().println("\tILES CI build log URL: " + chosenBtfEvent.getBuildLogUrl());
        }
        listener.getLogger().println("\n");

        // Add received BTF events to build parameters action to debug
        addNwftBuildParameter(build, new EventParamenterValue(chosenBtfEvent));

        // Check if BTF event contain any rejected status
        if (btfEventRejectType.contains(chosenBtfEvent.getBtfType().toString())) {
            listener.getLogger().println("\n==>> Stop the build because received BTF event type: " + chosenBtfEvent.getBtfType());
            listener.getLogger().println("\n");
            return false;
        }
        return true;
    }

    /*
    ** Get the Event ID from NWFTParameter.
    * @param build
     */
    public String getEventID(AbstractBuild<?,?> build) throws InterruptedException, IOException {
        String eventID = "";

        Event eventFromBuild = getAllNwftParametersOfType(build, EventParamenterValue.class)
                .stream().map(e -> e.getEvent()).findFirst().get();

        if (eventFromBuild instanceof ITREvent) {
            eventID = eventFromBuild.getID();
        }
        else if (eventFromBuild instanceof BTFEvent){
            eventID = ((BTFEvent) eventFromBuild).getBtfId();
        }
        return eventID;
    }

    /**
     * Query all BTF events from queue matching with specific ID
     * @param eventID : ID of Event
    **/
    public List <Event> getAllBTFEventFromQueue(String eventID) throws InterruptedException, IOException {
        EventDao eventDao = new JsonEventDao(eventDAOPath, CommunicatorEventFactory.getInstance());
        eventDao.checkSynchronizeLock();
        List<Event> events = new ArrayList<>();
        int countingTime = 0;

        // remove all old events in DB
        removeAllOldEvents(eventDao);

        // query the correct event
        do {
            events = eventDao.loadEventQueue(queueName).stream()
                    .filter(e -> e instanceof BTFEvent && eventID.equals(((BTFEvent) e).getBtfId())
                            && btfEventReceivedType.contains(((BTFEvent) e).getBtfType().toString())
                    ).collect(Collectors.toList());

            if (events.size() > 0) {
                // after get needed events, remove it from DAO
                eventDao.removeEventsInQueue(queueName, events);
                countingTime += waitingTime;
            } else {
                countingTime += delayTime;
                Thread.sleep(delayTime * 1000);
            }
        } while (countingTime + delayTime < waitingTime);
        return events;
    }

    /*
    ** Remove all event is over time.
    * @param eventDao
     */
    public void removeAllOldEvents(EventDao eventDao) throws InterruptedException, IOException {
        List<Event> oldEvents = eventDao.loadEventQueue(queueName).stream()
                .filter(e -> e instanceof BTFEvent && isExpiredEvent((BTFEvent) e)).collect(Collectors.toList());
        if (oldEvents != null && !oldEvents.isEmpty()) {
            eventDao.removeEventsInQueue(queueName, oldEvents);
        }
    }

    /*
    ** Check the BTF event is over timeout.
    * @param btfEvent
     */
    public boolean isExpiredEvent(BTFEvent btfEvent) {
        String currentTimeStamp = "BTFEvent_" + DateTime.now().plusSeconds(-getWaitingTime()).withZone(DateTimeZone.UTC).toString("MM-dd-yyyy-HH:mm:ss-z");
        String btfEventTimeStamp = btfEvent.getID().substring(0, btfEvent.getID().indexOf("UTC") + 3);
        if (btfEventTimeStamp.compareTo(currentTimeStamp) < 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /*
    ** Add the parameter values used for a build.
    * @param build : build
    * @param name : name of parameter
    * @param value : value of parameter
     */
    private void addInstanceBuildParams(AbstractBuild<?,?> build, String name, String value) {
        if (name != null && !name.isEmpty()) {
            Action action = new ParametersAction(new StringParameterValue(name, value));
            build.addAction(action);
        }
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
            return "ILES-Communicator: BTF Query";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }

}
