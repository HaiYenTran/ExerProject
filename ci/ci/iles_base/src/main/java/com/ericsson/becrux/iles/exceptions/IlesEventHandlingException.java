package com.ericsson.becrux.iles.exceptions;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import com.ericsson.becrux.base.common.eventhandler.exceptions.EventHandlingException;
import com.ericsson.becrux.iles.eiffel.events.IlesEventFactory;

/**
 * {@inheritDoc}
 */
public class IlesEventHandlingException extends EventHandlingException {

    public IlesEventHandlingException(Event e) {
        super(e);
    }

    public IlesEventHandlingException(String message, Event e) {
        super(message, e);
    }

    public IlesEventHandlingException(Throwable cause, Event e) {
        super(cause, e);
    }

    public IlesEventHandlingException(String message, Throwable cause, Event e) {
        super(message, cause, e);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    protected EventFactory getEventFactory() {
        return IlesEventFactory.getInstance();
    }

}
