package com.ericsson.becrux.base.common.eiffel.events.impl;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eventhandler.EventValidationResult;

import java.util.Map;

/**
 * Created by dung.t.bui on 12/27/2016.
 */
public class TestDummyEvent extends Event {

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    private Map<String, String> params;

    @Override
    public EventValidationResult validate() {
        return new EventValidationResult();
    }
}
