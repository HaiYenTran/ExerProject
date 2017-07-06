package com.ericsson.becrux.iles.eiffel.events;

import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventFactory;

/**
 * The Event Factory for ILES CI.
 *
 * @author dung.t.bui
 */
public class IlesEventFactory extends BaseEventFactory implements EventFactory {


    protected IlesEventFactory() {
        super();
    }

    /**
     * Registered child class.
     * @return list of child class
     */
    @Override
    protected void initChildClasses() {
        super.initChildClasses();
    }

    public static IlesEventFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        static final IlesEventFactory INSTANCE = new IlesEventFactory();
    }
}
