package com.ericsson.becrux.communicator.eiffel.events;

import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.google.gson.Gson;

/**
 * This class represents a value for a event parameter in a build.
 */
public class EventParamenterValue extends NwftParameterValue {

    private static String GROUP_TYPE = "EventParamenterValue";
    private Event event;

    /**
     * Constructor.
     * @param event ITR or BTF event.
     */
    public EventParamenterValue(Event event) {
        super(event.getClass().getSimpleName(), GROUP_TYPE);
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String getValue() { return new Gson().toJson(event); }
}
