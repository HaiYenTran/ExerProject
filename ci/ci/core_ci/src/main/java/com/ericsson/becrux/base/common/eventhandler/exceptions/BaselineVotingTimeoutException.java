package com.ericsson.becrux.base.common.eventhandler.exceptions;

import com.ericsson.becrux.base.common.eiffel.events.Event;

public class BaselineVotingTimeoutException extends EventHandlingException {
    private static final long serialVersionUID = -8110046896671282684L;

    private static final String defaultMsg = "Baseline voting was timed out ";

    // CONSTRUCTORS

    public BaselineVotingTimeoutException(Event e) {
        super(defaultMsg, e);
    }

    public BaselineVotingTimeoutException(String message, Event e) {
        super(message, e);
    }

    public BaselineVotingTimeoutException(Throwable cause, Event e) {
        super(defaultMsg, cause, e);
    }

    public BaselineVotingTimeoutException(String message, Throwable cause, Event e) {
        super(message, cause, e);
    }
}
