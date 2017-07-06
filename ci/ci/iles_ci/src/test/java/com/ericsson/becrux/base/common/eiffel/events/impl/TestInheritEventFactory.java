package com.ericsson.becrux.base.common.eiffel.events.impl;

/**
 * Created by dung.t.bui on 12/27/2016.
 */
public class TestInheritEventFactory extends BaseEventFactory {

    public TestInheritEventFactory() {
        super();
        registerSubtype(TestDummyEvent.class);
    }
}
