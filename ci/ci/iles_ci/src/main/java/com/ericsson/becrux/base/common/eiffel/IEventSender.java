package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.duraci.eiffelmessage.sending.exceptions.EiffelMessageSenderException;

public interface IEventSender extends AutoCloseable {

    void sendEvent(Event event, String routingKey) throws EiffelMessageSenderException;

    void sendEvent(Event event, Component target) throws EiffelMessageSenderException;
}
