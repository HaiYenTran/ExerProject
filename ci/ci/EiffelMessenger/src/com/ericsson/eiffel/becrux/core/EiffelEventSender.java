package com.ericsson.eiffel.becrux.core;

import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import com.ericsson.duraci.eiffelmessage.sending.MessageSendWrapper;
import com.ericsson.duraci.eiffelmessage.sending.MessageSender;
import com.ericsson.duraci.eiffelmessage.sending.exceptions.EiffelMessageSenderException;
import com.ericsson.duraci.logging.JavaLoggerEiffelLog;
import com.ericsson.eiffel.becrux.events.Event;

public class EiffelEventSender implements IEventSender
{
	private EiffelConfigurationILES ILESConfig;
	private EiffelConfiguration configuration;
	private MessageSender sender;
	private MessageSendWrapper sendWrapper;


	public EiffelEventSender(EiffelConfigurationILES ILESConfig)
	{
		this.ILESConfig = ILESConfig;
		this.configuration = ILESConfig;
		this.sendWrapper = new MessageSendWrapper(new JavaLoggerEiffelLog(
                MessageSender.class), this.configuration.getMessageBus(), this.configuration
                .getMessageSendQueue().getQueueLength());
		this.sender = new MessageSender(configuration, sendWrapper);
	}

	public void sendEvent(Event event) throws EiffelMessageSenderException
	{
		sendEvent(event, ILESConfig.getTagName());
	}

	@Override
	public void sendEvent(Event event, String tag) throws EiffelMessageSenderException
	{
		EiffelMessage eiffelMessage = EiffelEventConverter.convertToEiffelMessage(event, configuration, tag);
		sender.send(eiffelMessage);
	}

	public void close() {
		try {
			sender.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			sendWrapper.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}