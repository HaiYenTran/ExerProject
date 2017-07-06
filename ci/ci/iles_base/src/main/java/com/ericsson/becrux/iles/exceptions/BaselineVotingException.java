package com.ericsson.becrux.iles.exceptions;

import com.ericsson.becrux.base.common.eiffel.events.Event;

/**
 * The Exception when an baseline in voting process.
 */
public class BaselineVotingException extends IlesEventHandlingException {

    private static final String defaultMsg = "Baseline voting in progress";

    public BaselineVotingException(Event e) {
        super(defaultMsg, e);
    }

    public BaselineVotingException(String message, Event e) {
        super(message, e);
    }

    public BaselineVotingException(Throwable cause, Event e) {
        super(defaultMsg, cause, e);
    }

    public BaselineVotingException(String message, Throwable cause, Event e) {
        super(message, cause, e);
    }

    public BaselineVotingException(String message) {
        super(message, null);
    }
}
