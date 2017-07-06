package com.ericsson.becrux.base.common.eiffel.parameters;

import com.ericsson.becrux.base.common.eiffel.events.Event;

/**
 * Created by emiwaso on 2016-12-01.
 */
public class EiffelEventParameterValue_Received extends EiffelEventParameterValue {

    protected final static String PARAMETER_GROUP = "Received Eiffel Events";

    public EiffelEventParameterValue_Received(String name, Event event, String description) {
        super(name, event, description);
    }

    public EiffelEventParameterValue_Received(String name, Event event) {
        super(name, event);
    }
}
