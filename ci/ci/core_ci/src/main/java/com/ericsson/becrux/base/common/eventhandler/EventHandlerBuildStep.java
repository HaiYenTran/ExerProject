package com.ericsson.becrux.base.common.eventhandler;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.utils.BecruxBuildBadgeAction;

import hudson.model.AbstractBuild;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EventHandlerBuildStep extends NwftBuildStep {

    private int maxRetries;
    private int maxTimeout;
    private String queueName;

    @DataBoundConstructor
    public EventHandlerBuildStep(String maxRetries, String maxTimeout, String queueName) {
        try {
            this.maxRetries = Integer.parseInt(maxRetries);
        } catch (NumberFormatException ex) {
            this.maxRetries = 5; //Set default retry = 5 times
        }
        try {
            this.maxTimeout = Integer.parseInt(maxTimeout);
        } catch (NumberFormatException ex) {
            this.maxTimeout = 500; // Set default timeout 500 milliseconds for each retry
        }
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }

    /**
     * Get number of retries that {@link EventHandler} will try to handle the value again in this execution
     *
     * @return Number of times to retry
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * Get amount of time in milliseconds that {@link EventHandler} will spend for each retry.
     *
     * @return Amount of time in milliseconds
     */
    public int getMaxTimeout() {
        return maxTimeout;
    }

    /**
     * This function will show 4 numbers in the right of build badge
     * @param build
     * @param results
     * @param totalReceiveEvents
     * @param laterHandledEvents
     * @throws IOException
     */

    protected void addEventsBadge(AbstractBuild<?, ?> build, Map<Event, EventHandlingResult> results, int totalReceiveEvents, int laterHandledEvents) throws IOException {
        // [total events received | events handled success | events handled fail | events saved to queue]
        int successful = 0;
        int failed = 0;
        List<String> details = new LinkedList<>();
        for (Map.Entry<Event, EventHandlingResult> record : results.entrySet()) {
            Event event = record.getKey();
            EventHandlingResult result = record.getValue();
            if (result.isSuccessful())
                ++successful;
            else ++failed;
            details.add(event.getType() + " -> " + result.getSimpleResult());
        }
        build.addAction(new BecruxBuildBadgeAction(totalReceiveEvents + "|" + successful + "|" + "|" + failed + "|" + laterHandledEvents));
        build.setDescription(StringUtils.join(details, "\n"));
    }
}
