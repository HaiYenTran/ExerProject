package com.ericsson.becrux.base.common.eventhandler.exceptions;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategy;

public class StrategyNotApplicableException extends EventHandlingStrategyException {
    private static final long serialVersionUID = -5424027053071594566L;
    private static final String defaultMsg = "Cannot handle this value using the value.";

    // CONSTRUCTORS

    public StrategyNotApplicableException(Event e, EventHandlingStrategy strategy) {
        super(defaultMsg + (strategy == null ? "null" : strategy.getClass().getSimpleName()), e, strategy);
    }

    public StrategyNotApplicableException(String message, Event e, EventHandlingStrategy strategy) {
        super(message, e, strategy);
    }

    public StrategyNotApplicableException(Throwable cause, Event e, EventHandlingStrategy strategy) {
        super(defaultMsg + (strategy == null ? "null" : strategy.getClass().getSimpleName()), cause, e, strategy);
    }

    public StrategyNotApplicableException(String message, Throwable cause, Event e,
                                          EventHandlingStrategy strategy) {
        super(message, cause, e, strategy);
    }

}
