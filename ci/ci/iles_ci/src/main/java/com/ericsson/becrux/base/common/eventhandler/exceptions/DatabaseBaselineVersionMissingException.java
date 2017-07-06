package com.ericsson.becrux.base.common.eventhandler.exceptions;

import com.ericsson.becrux.base.common.eiffel.events.Event;

public class DatabaseBaselineVersionMissingException extends EventHandlingException {
    private static final long serialVersionUID = -6085490789575434690L;
    private static final String defaultMsg = "Baseline version of the node build is missing from the database for type: ";
    private static final String defaultMsgExt = "\nCaused by node type: ";

    // CONSTRUCTORS

    public DatabaseBaselineVersionMissingException(Event e, String type) {
        super(defaultMsg + (type == null ? "null" : type), e);
    }

    public DatabaseBaselineVersionMissingException(String message, Event e, String type) {
        super(message + defaultMsgExt + (type == null ? "null" : type), e);
    }

    public DatabaseBaselineVersionMissingException(Throwable cause, Event e, String type) {
        super(defaultMsg + (type == null ? "null" : type), cause, e);
    }

    public DatabaseBaselineVersionMissingException(String message, Throwable cause, Event e, String type) {
        super(message + defaultMsgExt + (type == null ? "null" : type), cause, e);
    }

}
