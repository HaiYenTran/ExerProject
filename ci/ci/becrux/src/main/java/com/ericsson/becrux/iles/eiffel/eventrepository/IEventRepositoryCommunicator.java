package com.ericsson.becrux.iles.eiffel.eventrepository;

import com.ericsson.becrux.base.common.eiffel.events.Event;

import java.util.List;

// TODO: review if it can be on CORE ?
public interface IEventRepositoryCommunicator {

    /**
     * Gets the Event from event repository
     * @param id event id
     * @return Event
     * @throws EventRepositoryException
     */
    public Event getEvent(String id) throws EventRepositoryException;

    /**
     * Gets the list of events from event repository
     * @return List of Events
     * @throws EventRepositoryException
     */
    public List<Event> getEventList() throws EventRepositoryException;

    /**
     * Gets the list of downstream events from event repository
     * @param id event id
     * @return List of Event
     * @throws EventRepositoryException
     */
    public List<Event> getDownstreamEventList(String id) throws EventRepositoryException;

    /**
     * Get downstream event list from the event repository
     * @param event {@Event}
     * @return List of events
     * @throws EventRepositoryException
     */
    public List<Event> getDownstreamEventList(Event event) throws EventRepositoryException;

    /**
     * Gets the list of upstream events from event repository
     * @param id event id
     * @return List of Event
     * @throws EventRepositoryException
     */
    public List<Event> getUpstreamEventList(String id) throws EventRepositoryException;

    /**
     * Get upstream event list from the event repository
     * @param event {@Event}
     * @return List of events
     * @throws EventRepositoryException
     */
    public List<Event> getUpstreamEventList(Event event) throws EventRepositoryException;
}
