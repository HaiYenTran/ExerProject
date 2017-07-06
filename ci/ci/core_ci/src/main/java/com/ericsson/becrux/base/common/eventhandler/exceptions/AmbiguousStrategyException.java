package com.ericsson.becrux.base.common.eventhandler.exceptions;

import com.ericsson.becrux.base.common.eiffel.events.Event;

/**
 * Created by emacmyc on 2016-12-02.
 */
public class AmbiguousStrategyException extends EventHandlingStrategyException {
    private static final String defaultMsg = " ambiguous candidates were found while looking for a value to handle an value.";
    private static final String defaultMsgExt = "\nAmbiguous strategies found: ";

    public AmbiguousStrategyException(Event e, int count) {
        super(count + defaultMsg, e, null);
    }

    public AmbiguousStrategyException(String message, Event e, int count) {
        super(message + defaultMsgExt + count, e, null);
    }

    public AmbiguousStrategyException(Throwable cause, Event e, int count) {
        super(count + defaultMsg, cause, e, null);
    }

    public AmbiguousStrategyException(String message, Throwable cause, Event e, int count) {
        super(message + defaultMsgExt + count, cause, e, null);
    }
}
