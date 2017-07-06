package com.ericsson.becrux.base.common.eiffel.eventrepository;

import com.ericsson.becrux.base.common.eiffel.events.Event;

import java.util.List;

// TODO: review if it can be on CORE ?
public interface IEventRepositoryCommunicator {
    public Event getEvent(String id) throws EventRepositoryException;

    public List<Event> getEventList() throws EventRepositoryException;

    public List<Event> getDownstreamEventList(String id) throws EventRepositoryException;

    public List<Event> getDownstreamEventList(Event event) throws EventRepositoryException;

    public List<Event> getUpstreamEventList(String id) throws EventRepositoryException;

    public List<Event> getUpstreamEventList(Event event) throws EventRepositoryException;
}
