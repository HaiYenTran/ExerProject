package com.ericsson.becrux.communicator.eiffel.events;

import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventFactory;

public class CommunicatorEventFactory extends BaseEventFactory implements EventFactory {

    protected CommunicatorEventFactory() {
        super();
    }

    public static CommunicatorEventFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        static final CommunicatorEventFactory INSTANCE = new CommunicatorEventFactory();
    }
}
