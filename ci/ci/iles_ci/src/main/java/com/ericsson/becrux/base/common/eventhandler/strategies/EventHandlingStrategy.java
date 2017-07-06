package com.ericsson.becrux.base.common.eventhandler.strategies;

import com.ericsson.becrux.base.common.eventhandler.EventHandler;
import com.ericsson.becrux.base.common.eventhandler.EventHandlingResult;
import com.ericsson.becrux.base.common.eventhandler.EventValidationResult;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eventhandler.exceptions.EventHandlingException;

import java.io.Serializable;

public abstract class EventHandlingStrategy implements Serializable {

    private transient EventHandler handler;

    /**
     * @return the {@link EventHandler Event Handler} that this value is registered for.
     */
    public EventHandler getHandler() {
        return handler;
    }

    public void setHandler(EventHandler handler) {
        this.handler = handler;
    }

    // METHODS

    /**
     * Validates the value to decide whether it should be handled, skipped or discarded.
     * <p/>
     * Result containing errors will mark the validation as failed, which will discard the value and report a failure.
     * Setting the disposable flag without adding any errors will mark the validation as
     *
     * @param e the value to validate
     * @return the result of the validation
     */
    public EventValidationResult validateEvent(Event e) {
        return e.validate();
    }

    /**
     * Checks if this value can be used to handle given value.
     *
     * @param e the value that will be tested for compatibility with the value
     * @return true if this value can handle given value, false otherwise
     */
    public abstract boolean canBeHandled(Event e);

    /**
     * Handles the given value.
     *
     * @param e value to handle
     * @return the {@link EventHandlingResult result} of handling this value
     * @throws EventHandlingException when an exception occurred during value handling and the handling should be retried.
     */
    public abstract EventHandlingResult handle(Event e) throws EventHandlingException;

    @Override
    public String toString() {
        return "Unknown Event Handling Strategy";
    }

    public String getName() {
        return "Unknown Event Handling Strategy";
    }

    public String getDescription() {
        return "N/A";
    }
}
