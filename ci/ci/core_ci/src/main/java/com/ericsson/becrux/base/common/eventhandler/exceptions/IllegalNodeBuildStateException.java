package com.ericsson.becrux.base.common.eventhandler.exceptions;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.eiffel.events.Event;

/**
 * Exception thrown when a node build is in wrong state, for example Failed Build in a loop.
 *
 * @author emacmyc
 */
public class IllegalNodeBuildStateException extends EventHandlingException {
    private static final long serialVersionUID = -203966642892003281L;
    private static final String defaultMsg = "While handling an value, an illegal state was encountered: ";
    private static final String defaultMsgExt = "\nCaused by illegal state: ";

    // CONSTRUCTORS

    public IllegalNodeBuildStateException(Event e, Component.State state) {
        super(defaultMsg + (state == null ? "null" : state.name()), e);
    }

    public IllegalNodeBuildStateException(String message, Event e, Component.State state) {
        super(message + defaultMsgExt + (state == null ? "null" : state.name()), e);
    }

    public IllegalNodeBuildStateException(Throwable cause, Event e, Component.State state) {
        super(defaultMsg + (state == null ? "null" : state.name()), cause, e);
    }

    public IllegalNodeBuildStateException(String message, Throwable cause, Event e, Component.State state) {
        super(message + defaultMsgExt + (state == null ? "null" : state.name()), cause, e);
    }

}
