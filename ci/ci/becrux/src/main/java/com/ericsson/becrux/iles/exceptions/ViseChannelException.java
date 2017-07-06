package com.ericsson.becrux.iles.exceptions;

import com.ericsson.becrux.base.common.eiffel.events.Event;

/**
 * Created by TRONG.LE on 4/13/2017.
 */

/**
 * The Exception when an Vise channel is still running.
 */

public class ViseChannelException extends IlesEventHandlingException {

    private static final String defaultMsg = "No Vise Channels Available";

    public ViseChannelException(Event e) {
        super(defaultMsg, e);
    }

    public ViseChannelException(String message, Event e) {
        super(message, e);
    }

    public ViseChannelException(Throwable cause, Event e) {
        super(defaultMsg, cause, e);
    }

    public ViseChannelException(String message, Throwable cause, Event e) {
        super(message, cause, e);
    }

    public ViseChannelException(String message) {
        super(message, null);
    }
}

