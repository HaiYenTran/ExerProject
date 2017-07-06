package com.ericsson.becrux.base.common.eiffel.parameters;

import com.ericsson.becrux.base.common.eiffel.events.Event;

/**
 * Created by emiwaso on 2016-12-01.
 */
public class EiffelEventParameterValue_ToSend extends EiffelEventParameterValue {

    protected final static String PARAMETER_GROUP = "Eiffel events scheduled for sending";
    private String tag;

    public EiffelEventParameterValue_ToSend(String name, Event event, String description) {
        super(name, event, description);
    }

    public EiffelEventParameterValue_ToSend(String name, Event event) {
        super(name, event);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
