package com.ericsson.becrux.base.common.utils;

import com.ericsson.becrux.base.common.eiffel.events.Event;

import java.io.PrintStream;

public class EventLoggingHelper {

    PrintStream log;

    // GETTERS AND SETTERS

    public EventLoggingHelper(PrintStream log) {
        this.log = log;
    }

    public PrintStream getLog() {
        return log;
    }

    // CONSTRUCTORS

    public void setLog(PrintStream log) {
        if (log == null)
            throw new NullPointerException("log can't be null");
        this.log = log;
    }

    // METHODS

    public void logEvent(Event e) {

    }
}
