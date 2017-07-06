package com.ericsson.becrux.base.common.eventhandler.exceptions;

import com.ericsson.becrux.base.common.eiffel.events.Event;

public class IllegalEventException extends EventHandlingException {
    private static final long serialVersionUID = -7840806641494454436L;
    private static final String defaultMsg = "An illegal value occured.";

    // CONSTRUCTORS

    public IllegalEventException(Event e) {
        super(defaultMsg, e);
    }

    public IllegalEventException(String message, Event e) {
        super(message, e);
    }

    public IllegalEventException(Throwable cause, Event e) {
        super(defaultMsg, cause, e);
    }

    public IllegalEventException(String message, Throwable cause, Event e) {
        super(message, cause, e);
    }

}
