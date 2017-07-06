package com.ericsson.becrux.base.common.eventhandler.exceptions;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventFactory;

/**
 * Exception thrown when an exception breaks value handling.
 *
 * @author emacmyc
 */
public class EventHandlingException extends Exception {
    private static final long serialVersionUID = 4653467896159330330L;
    private static final String defaultMsg = "An exception occurred while handling event : ";
    private static final String defaultMsgExt = "\nCaused by Event : ";

    private String message;

    // CONSTRUCTORS

    public EventHandlingException(Event e) {
        super();
        this.message = defaultMsg + (e == null ? "null" : getEventFactory().toJson(e));
    }

    public EventHandlingException(String message, Event e) {
        super();
        this.message = message + defaultMsgExt + (e == null ? "null" : getEventFactory().toJson(e));
    }

    public EventHandlingException(Throwable cause, Event e) {
        super(cause);
        this.message = defaultMsg + (e == null ? "null" : getEventFactory().toJson(e));
    }

    public EventHandlingException(String message, Throwable cause, Event e) {
        super(cause);
        this.message = message + defaultMsgExt + (e == null ? "null" : getEventFactory().toJson(e));
    }

    /**
     * Get the Even factory for serialize/deserialize
     * Should be override in child class.
     * @return
     */
    protected EventFactory getEventFactory() {
        return new BaseEventFactory();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
