package com.ericsson.becrux.base.common.eventhandler.exceptions;

import com.ericsson.becrux.base.common.eiffel.events.Event;

public class StrategyNotFoundException extends EventHandlingStrategyException {
    private static final long serialVersionUID = -7064209039659086390L;
    private static final String defaultMsg = "No value found to handle this value.";

    // CONSTRUCTORS

    public StrategyNotFoundException(Event e) {
        super(defaultMsg, e, null);
    }

    public StrategyNotFoundException(String message, Event e) {
        super(message, e, null);
    }

    public StrategyNotFoundException(Throwable cause, Event e) {
        super(defaultMsg, cause, e, null);
    }

    public StrategyNotFoundException(String message, Throwable cause, Event e) {
        super(message, cause, e, null);
    }

}
