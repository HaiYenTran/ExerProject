package com.ericsson.becrux.base.common.eiffel.events.impl;

import com.ericsson.becrux.base.common.core.AbstractFactory;
import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import com.ericsson.becrux.base.common.eiffel.events.Event;

/**
 * The factory for Event type.
 *
 * @author dung.t.bui
 */
public class BaseEventFactory extends AbstractFactory<Event> implements EventFactory {

    /**
     * Constructor.
     */
    public BaseEventFactory() {
        super(Event.class);
        initChildClasses();
    }

    /**
     * {@inheritDoc}
     */
    public Event createEvent(String type) throws Exception {
        Class<? extends Event> clazz = getRegisteredClasses().get(type);
        if (clazz == null) {
            throw new ClassNotFoundException("Class " + type + " is not registered." );
        }
        return clazz.newInstance();
    }

    /**
     * Registered child class.
     * @return list of child class
     */
    protected void initChildClasses() {
        registerSubtype(BaseEventImpl.class);
        registerSubtype(BTFEvent.class);
        registerSubtype(ITREvent.class);
    }
}
