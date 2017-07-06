package com.ericsson.becrux.base.common.eventhandler;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class EventQueue {

    List<Event> events = new Vector<>();
    Comparator<Event> comparator;

//    public EventQueue(Comparator<Event> comparator) {
//        this.comparator = new EventSchedulerComparator();
//    }

    public EventQueue(Comparator<Event> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Event> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<Event> comparator) {
        this.comparator = comparator;
    }

    /**
     * Adds new value to the queue and sorts it.
     *
     * @param e Event that will be added to queue.
     */
    public void pushEvent(Event e) {
        events.add(e);
        this.sortQueue();
    }

    /**
     * Returns the next value and removes it from the queue.
     *
     * @return The next value from the queue.
     */
    public Event pullEvent() {
        if (events.isEmpty())
            return null;
        Event e = events.get(0);
        events.remove(0);
        return e;
    }

    /**
     * Returns the next value without removing it from the queue.
     *
     * @return The next value from the queue.
     */
    public Event peekEvent() {
        if (events.isEmpty())
            return null;
        return events.get(0);
    }

    /**
     * Forces sorting of the value queue according to the comparator provided.
     */
    public void sortQueue() {
        Collections.sort(events, comparator);
    }

    public void discardObsoleteEvents() {
        // TODO: implement decision making
        return;
    }

    public boolean isEmpty() {
        return events.isEmpty();
    }

    public int getSize() {
        return events.size();
    }

    public List<Event> getEventList() {
        return events;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((events == null) ? 0 : events.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventQueue other = (EventQueue) obj;
        if (events == null) {
            if (other.events != null)
                return false;
        } else if (!events.equals(other.events))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(events);
    }


}
