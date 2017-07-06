package com.ericsson.becrux.iles.eventhandler;

import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.base.common.dao.EventDao;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.parameters.EiffelEventParameterValue_Received;
import com.ericsson.becrux.base.common.eventhandler.EventHandler;
import com.ericsson.becrux.base.common.eventhandler.EventHandlerBuildStep;
import com.ericsson.becrux.base.common.eventhandler.EventHandlingResult;
import com.ericsson.becrux.base.common.eventhandler.exceptions.EventHandlingException;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategy;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategyParameterValue;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.iles.eiffel.events.IlesEventFactory;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The build step for handling Events. This class retrieves all the registered event handling strategies
 * and handle events using registered strategies.
 */
public class IlesEventHandlerBuildStep extends EventHandlerBuildStep {

    /**
     * Constructor.
     * @param maxRetries Maximum retries for event handling
     * @param maxTimeout Maximum timeout for each retry (ms)
     * @param queueName Name of queue to save scheduled events
     */
    @DataBoundConstructor
    public IlesEventHandlerBuildStep(String maxRetries, String maxTimeout, String queueName) {
        super(maxRetries, maxTimeout, queueName);
    }

    /** {@inheritDoc} */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        try {
            EventDao dao = IlesGlobalConfig.getInstance().getEventDao();

            listener.getLogger().println("\n===> List of assigned event type");
            IlesEventFactory.getInstance().getRegisteredClassNames().forEach(c -> listener.getLogger().print(c + ", "));

            // Event handler starts when Controller job has been started
            EventHandler handler = new EventHandler(listener.getLogger(), new EventSchedulerComparator(), IlesEventFactory.getInstance());

            // Update maxRetries, maxTimeout for Event Handler
            handler.setMaxRetries(getMaxRetries());
            handler.setMaxTimeout(getMaxTimeout());

            // Find all Event Handling Strategies and register them.
            listener.getLogger().println("\n\n===> Checking existing event strategies");
            List<EventHandlingStrategy> strategies = this.getAllNwftParametersOfType(build, EventHandlingStrategyParameterValue.class)
                    .stream().map(param -> param.getStrategy()).collect(Collectors.toList());

            strategies.forEach(eventHandlingStrategy -> listener.getLogger().println("Found: " + eventHandlingStrategy));
            strategies.forEach(strategy -> handler.registerNewStrategy(strategy));

            // Get all events from Eiffel Receiver or Upstream jobs
            List<EiffelEventParameterValue_Received> receivedEvents =
                    this.getAllNwftParametersOfType(build, EiffelEventParameterValue_Received.class)
                            .stream().filter(param -> !param.isUsed()).collect(Collectors.toList());

            listener.getLogger().println("\n===> List all received events");
            if (receivedEvents.size() >0) {
                List<Event> events = receivedEvents.stream().map(param -> param.getEvent()).collect(Collectors.toList());
                events.forEach(event -> listener.getLogger().println("\n-> Received " + event.getType() + " with ID: " + event.getID() + "\n" + IlesEventFactory.getInstance().toJson(event)));

                events.forEach(event -> event.setEventFromQueue(false));
                events.forEach(event -> handler.addEventForHandling(event));
            } else {
                listener.getLogger().println("No event received!");
            }

            // Get all scheduled events from DB
            listener.getLogger().println("\n===> Load all scheduled events in ILES CI DB with queue name = " + getQueueName());
            Collection<Event> scheduledEvents = dao.loadEventQueue(getQueueName());
            if (scheduledEvents.size() >0 ) {
                scheduledEvents.forEach(event -> listener.getLogger().println("Load " + event.getType() + " with ID: " + event.getID() + "\n" + IlesEventFactory.getInstance().toJson(event)));
                scheduledEvents.forEach(event -> handler.addEventForHandling(event));
            } else {
                listener.getLogger().println("No scheduled event loaded!");
            }
            // Count total input events
            int totalReceiveEvents = handler.getInputEvents().size();

            listener.getLogger().println("\n===> Start handling all events");
            // Start handling all events

            handler.handleAllEvents();

            // After handling value, set value to used = true
            receivedEvents.stream().forEach(param -> param.setUsed(true));

            // Put all feedback events to EiffelEventParameterValue_ToSend so that Eiffel Sender can send out.
            List<NwftParameterValue> feedbackParams = handler.getFeedbackParams();
            this.addNwftBuildParameters(build, feedbackParams);

            // Save scheduled events into DB to handle in next execution
            try {
                listener.getLogger().println("\n===> Save all scheduled events to ILES CI DB with queue name = " + getQueueName());
                handler.getScheduledEvents().forEach(event -> event.setEventFromQueue(true));
                handler.getScheduledEvents().forEach(event -> listener.getLogger().println("\nSave " + event.getType() + " with ID: " + event.getID() + "\n" + IlesEventFactory.getInstance().toJson(event)));
                dao.saveEventQueue(getQueueName(), handler.getScheduledEvents());
                if (handler.getScheduledEvents().size() == 0) {
                    listener.getLogger().println("No event saved!\n");
                }
            } catch (IOException e) {
                throw new EventHandlingException("ERROR: Saving scheduled events to DB failed.", e, null);
            }

            // Add a brief description about test result below each build history in Jenkins
            Map<Event, EventHandlingResult> results = handler.getHandlingResults();
            addEventsBadge(build, results, totalReceiveEvents, handler.getScheduledEvents().size());

            return true;
        } catch (Exception e) {
            e.printStackTrace(listener.getLogger());
            listener.getLogger().println("ERORR: Handle all events failed with exception: " + e);
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * The descriptor for {@link IlesEventHandlerBuildStep}
     */
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
            return "ILES: Event Handler";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
