package com.ericsson.becrux.base.common.eventhandler;

//import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.base.common.dao.CommonDao;
import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import com.ericsson.becrux.base.common.eventhandler.exceptions.EventHandlingStrategyException;
import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eventhandler.exceptions.AmbiguousStrategyException;
import com.ericsson.becrux.base.common.eventhandler.exceptions.EventHandlingException;
import com.ericsson.becrux.base.common.eventhandler.exceptions.StrategyNotFoundException;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategy;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that contains the logic required to handle {@link Event events} using {@link EventHandlingStrategy strategies}.
 *
 * @author emacmyc
 */
public class EventHandler {

    private List<EventHandlingStrategy> strategies = new ArrayList<>();
    // value queues
    private List<Event> inputEvents = Collections.synchronizedList(new LinkedList<>());
    private List<NwftParameterValue> outputParams = Collections.synchronizedList(new LinkedList<>());
    private List<Event> scheduledEvents = Collections.synchronizedList(new LinkedList<>());
    private Map<Event, EventHandlingResult> handlingResults = Collections.synchronizedMap(new HashMap<>());
    // configuration
    private int maxRetries;
    private int maxTimeout;
    // component connections
    private PrintStream log;
    private Comparator<Event> schedulingComparator;
    private EventFactory eventFactory;

    /**
     * Creates a new value handler.
     *
     * @param log                  log where all messages will be sent
     *                             availability and to trigger new loops
     * @param schedulingComparator comparator used to determine the events priority during scheduling
     */
    public EventHandler(PrintStream log, Comparator<Event> schedulingComparator, EventFactory eventFactory) {
        this.schedulingComparator = schedulingComparator;

        // validation
        if (log == null)
            throw new NullPointerException("null log provided");
        if (schedulingComparator == null)
            throw new NullPointerException("null scheduling comparator provided");

        this.log = log;
        this.schedulingComparator = schedulingComparator;
        this.eventFactory = eventFactory;
    }

    /**
     * Picks a value that can handle given value from the registered strategies.
     * Will always return one value.
     *
     * @param event the value that the returned value is supposed to handle
     * @return one of the registered strategies that can handle the given value
     * @throws StrategyNotFoundException  when there is no registered value that can handle the given value
     * @throws AmbiguousStrategyException when there is more than one value that can handle given value
     */
    public EventHandlingStrategy selectStrategy(Event event) throws StrategyNotFoundException, AmbiguousStrategyException {
        synchronized (strategies) {
            final Event e = event;
            List<EventHandlingStrategy> potentialStrategies = strategies.stream().filter(eventHandlingStrategy ->
                    eventHandlingStrategy.canBeHandled(e)).collect(Collectors.toList());

            if (potentialStrategies.isEmpty()) {
                throw new StrategyNotFoundException(event);
            } else if (potentialStrategies.size() == 1) {
                return potentialStrategies.get(0);

            } else {
                throw new AmbiguousStrategyException(event, potentialStrategies.size());
            }
        }
    }

    /**
     * Adds new value to the value handler.
     *
     * @param strategy The value used to handle the given types of events.
     */
    public void registerNewStrategy(EventHandlingStrategy strategy) {
        if (strategy == null)
            return;

        synchronized (strategies) {
            strategies.add(strategy);
            strategy.setHandler(this);
        }
    }

    /**
     * Adds an value for handling.
     * To start handling the events, use {@link #handleAllEvents()}.
     *
     * @param e value to handle
     */
    public void addEventForHandling(Event e) {
        synchronized (inputEvents) {
            inputEvents.add(e);
        }
    }

    /**
     * Adds an value for feedback from value handling.
     * Should be used by the strategies as a preferred form of sending feedback and collected by the user after using
     * {@link #handleAllEvents()} or {@link #handleEvent(Event)}.
     *
     * @param e value that should be sent as a feedback from value handling
     */
    public void addFeedbackParam(NwftParameterValue e) {
        synchronized (outputParams) {
            outputParams.add(e);
        }
    }

    /**
     * Processes all events in the inputEvent which contain all Events from Eiffel message and load from Queued (if possible).
     *
     * @throws EventHandlingException when there's a problem with value handling (will contain all
     *                                causes as suppressed exceptions)
     */
    public synchronized void handleAllEvents() throws EventHandlingException, InterruptedException {
        if (this.inputEvents.isEmpty()) {
            log.println("No events queued - nothing to do.");
            return;
        }

        while (!inputEvents.isEmpty()) {
            List<Event> currentBatch;
            synchronized (inputEvents) {
                currentBatch = inputEvents;
                inputEvents = Collections.synchronizedList(new LinkedList<>());
            }
            this.handleEventBatch(currentBatch);
        }
    }

    protected void handleEventBatch(List<Event> batch) throws InterruptedException {
        this.log.println();
        EventHandlingResult result = null;

        //Sort all events to see which value should be handled first
        batch.sort(schedulingComparator);

        for (Event event : batch) {
            if (Thread.interrupted())  // in case something tries to stop the thread/job, respect it
                throw new InterruptedException();

            try {
                result = this.handleEvent(event);
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                result = EventHandlingResult.createFailedResult("An exception occurred when handling the value", e);
            } finally {
                if (result != null && result.isHandled())
                    this.handlingResults.put(event, result);
                else {
                    this.scheduledEvents.add(event);
                }

                if (result.isSuccessful()) {
                    log.println("\nEvent " + eventFactory.toJson(event) + " handled successfully.");
                } else {
                    log.println("\nEvent " + eventFactory.toJson(event) + " handled failed: " + result.getDescription());
                    if (result.getCause() != null)
                        result.getCause().printStackTrace(log);
                }
                log.println("\n=========>>>>>>>>>>>>>>>----------------<<<<<<<<<<<<<<<=========\n");
            }
        }
    }

    /**
     * Handle single value using Event Handler's registered strategies.
     * Handling this value will not contribute to Event Handler's scheduled events list or handling results list,
     * but may create feedback events.
     *
     * @param event value to handle
     * @return result of the value handling
     * @throws EventHandlingException
     * @throws InterruptedException
     */
    public synchronized EventHandlingResult handleEvent(Event event) throws EventHandlingException, InterruptedException {
        EventHandlingStrategy s = null;
        try {
            s = this.selectStrategy(event);
        } catch (EventHandlingStrategyException e) {
            return EventHandlingResult.createFailedResult("Strategy selection failed", e);
        }

        EventValidationResult validationResult = null;
        try {
            validationResult = s.validateEvent(event);
            if (!validationResult.isSuccessful())
                return EventHandlingResult.createFailedResult("Validation failed:\n"
                        + validationResult.getErrors().stream().collect(Collectors.joining("\n")), null);
        } catch (Exception e) {
            return EventHandlingResult.createFailedResult("Validation failed", e);
        }

        int retry = 0;
        int currentMaxRetries;
        int currentMaxTimeout = this.maxTimeout;
        EventHandlingResult handlingResult = null;
        do {
            if (retry == 0)
                log.println("\nHandling value: " + eventFactory.toJson(event));
            else
                log.println("Handling value (retry " + retry + ")");

            try {
                if (retry > 0)
                    Thread.sleep(currentMaxTimeout);

                handlingResult = s.handle(event);
                if (handlingResult == null)
                    throw new EventHandlingStrategyException("Strategy returned a null result from value handling.",
                            new NullPointerException("Event Handling Strategy result is null"), event, s);
                currentMaxRetries = Math.min(this.maxRetries, handlingResult.getSuggestedRetries());
                currentMaxTimeout = Math.min(this.maxTimeout, handlingResult.getSuggestedRetryTimeout());
            } catch (EventHandlingException e) {
                return EventHandlingResult.createFailedResult("An exception occurred when handling an value", e);
            }
            ++retry;
        } while (handlingResult.isRetryableImmediately(retry) && retry < currentMaxRetries);

        return handlingResult;
    }

    /**
     * Get number of retries that {@link EventHandler} will try to handle the value again in this execution.
     *
     * @return Number of times to retry
     */
    public int getMaxRetries() {
        return maxRetries;
    }

// GETTERS & SETTERS

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * Get amount of time in milliseconds that {@link EventHandler} will spend for each retry.
     *
     * @return Amount of time in milliseconds
     */
    public int getMaxTimeout() {
        return maxTimeout;
    }

    public void setMaxTimeout(int maxTimeout) {
        this.maxTimeout = maxTimeout;
    }

    public PrintStream getLog() {
        return log;
    }

    public void setLog(PrintStream log) {
        this.log = log;
    }

    public List<Event> getScheduledEvents() {
        return Collections.unmodifiableList(scheduledEvents);
    }

    public Map<Event, EventHandlingResult> getHandlingResults() {
        return Collections.unmodifiableMap(handlingResults);
    }

    public List<NwftParameterValue> getFeedbackParams() {
        return Collections.unmodifiableList(outputParams);
    }

}
