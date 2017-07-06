package com.ericsson.eiffel.becrux.core;

import com.ericsson.duraci.eiffelmessage.sending.exceptions.EiffelMessageSenderException;
import com.ericsson.eiffel.becrux.events.Event;

public interface IEventSender extends AutoCloseable {

	public void sendEvent(Event event, String routingKey) throws EiffelMessageSenderException;
}
