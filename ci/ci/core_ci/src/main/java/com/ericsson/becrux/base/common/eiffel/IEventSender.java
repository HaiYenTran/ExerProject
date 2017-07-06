package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.duraci.eiffelmessage.sending.exceptions.EiffelMessageSenderException;

/**
 * A class can implement this interface when it wants to send an event.
 */
public interface IEventSender extends AutoCloseable {

    /**
     * Sends the event using routing key
     * @param event event to be sent
     * @param routingKey routing key to be matched
     * @throws EiffelMessageSenderException
     */
    void sendEvent(Event event, String routingKey) throws EiffelMessageSenderException;

    /**
     *Sends the event using IMS node
     * @param event event to be sent
     * @param target IMS node
     * @throws EiffelMessageSenderException
     */
    void sendEvent(Event event, Component target) throws EiffelMessageSenderException;
}
