package com.ericsson.becrux.base.common.eiffel.events;

import com.ericsson.becrux.base.common.core.Factory;

/**
 * The factory for creating Event.
 *
 * @author dung.t.bui
 */
public interface EventFactory extends Factory<Event> {

    /**
     * Create Event by string type.
     *
     * @param type the type of Event
     * @return new instance of Event
     * @throws Exception if anything fail
     */
    Event createEvent(String type) throws Exception;
}
