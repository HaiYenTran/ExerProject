package com.ericsson.becrux.base.common.eiffel.events.impl;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eventhandler.EventValidationResult;

/**
 * Base Implementation for {@link Event}.
 * @author dung.t.bui
 */
public class BaseEventImpl extends Event {

    @Override
    public EventValidationResult validate() {
        return new EventValidationResult();
    }
}
