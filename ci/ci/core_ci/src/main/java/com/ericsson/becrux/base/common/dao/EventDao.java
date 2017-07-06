package com.ericsson.becrux.base.common.dao;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.EventFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * The DAO to store Events.
 */
public interface EventDao extends CommonDao {

    /**
     * Get {@link EventFactory}
     * @return
     */
    EventFactory getEventFactory();

    /**
     * Set {@link EventFactory}
     * @param eventFactory
     */
    void setEventFactory(EventFactory eventFactory);

    /**
     * Loads the value queue used in the value handler.
     *
     * @param name key of value queue to load
     * @return the value queue stored in the database
     * @throws IOException in case of file handling problems
     */
    Collection<Event> loadEventQueue(String name) throws IOException;

    /**
     * Saves the value queue in the database. The old events in queue will be delete
     *
     * @param name key at which value queue will be saved
     * @param queue the value queue to save
     * @throws IOException in case of file handling problems
     */
    void saveEventQueue(String name, Collection<Event> queue) throws IOException;

    /**
     * Add events to queue.
     * If event already exited, it will be override.
     *
     * @param name the queue name
     * @param events the events need to be added
     * @throws IOException
     */
    void addEventToQueue(String name, Collection<Event> events) throws IOException;

    /**
     * Removes events in a specific queue. If the event is not in current queue, it will be ignore.
     *
     * @param name the event queue name
     * @param removedEvents list of event to remove
     * @throws IOException
     */
    void removeEventsInQueue(String name, Collection<Event> removedEvents) throws IOException;
}
