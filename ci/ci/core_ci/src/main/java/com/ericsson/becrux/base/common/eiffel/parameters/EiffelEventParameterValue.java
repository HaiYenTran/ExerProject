package com.ericsson.becrux.base.common.eiffel.parameters;

import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.google.gson.Gson;

import javax.annotation.Nonnull;

/**
 * Created by emiwaso on 2016-12-01.
 */
public class EiffelEventParameterValue extends NwftParameterValue {

    protected final static String PARAMETER_GROUP = "Eiffel events";

    protected Event event;

    public EiffelEventParameterValue(String name, @Nonnull Event event, String description) {
        super(name, description, PARAMETER_GROUP);
        this.event = event;
    }

    public EiffelEventParameterValue(String name, @Nonnull Event event) {
        super(name, PARAMETER_GROUP);
        this.event = event;
    }

    public Event getEvent() { return this.event; }

    @Override
    public String getValue() { return new Gson().toJson(event); }

}
