package com.ericsson.becrux.base.common.eventhandler.exceptions;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategy;

/**
 * Exception thrown when an exception breaks value handling inside a value.
 *
 * @author emacmyc
 */
public class EventHandlingStrategyException extends EventHandlingException {
    private static final long serialVersionUID = -5284395676247985419L;
    private static final String defaultMsg = "An exception occurred while handling an value using the value: ";
    private static final String defaultMsgExt = "\nCaused by a value: ";

    // CONSTRUCTORS

    public EventHandlingStrategyException(Event e, EventHandlingStrategy strategy) {
        super(defaultMsg + (strategy == null ? "null" : strategy.getClass().getSimpleName()), e);
    }

    public EventHandlingStrategyException(String message, Event e, EventHandlingStrategy strategy) {
        super(message + defaultMsgExt + (strategy == null ? "null" : strategy.getClass().getSimpleName()), e);
    }

    public EventHandlingStrategyException(Throwable cause, Event e, EventHandlingStrategy strategy) {
        super(defaultMsg + (strategy == null ? "null" : strategy.getClass().getSimpleName()), cause, e);
    }

    public EventHandlingStrategyException(String message, Throwable cause, Event e,
                                          EventHandlingStrategy strategy) {
        super(message + defaultMsgExt + (strategy == null ? "null" : strategy.getClass().getSimpleName()), cause, e);
    }

}
