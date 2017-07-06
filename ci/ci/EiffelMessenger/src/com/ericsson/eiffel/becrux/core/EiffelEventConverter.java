package com.ericsson.eiffel.becrux.core;

import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.eiffelmessage.messages.EiffelEvent;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage.Factory.OngoingMessageConfiguration;
import com.ericsson.duraci.eiffelmessage.messages.events.EiffelGenericEvent;
import com.ericsson.eiffel.becrux.events.Event;

public class EiffelEventConverter
{
	private EiffelEventConverter() {}
	
	private static final String fieldName = "event";
	
	public static EiffelEvent convertToEiffelEvent(Event input)
	{
		EiffelEvent event = EiffelGenericEvent.Factory.create(input.getClass().getSimpleName());
		event.setOptionalParameter(fieldName, input.toJson());
		return event;
	}
	
	public static EiffelMessage convertToEiffelMessage(Event input)
	{
		return convertToEiffelMessage(input, null, null);
	}
	
	public static EiffelMessage convertToEiffelMessage(Event input, EiffelConfiguration configuration)
	{
		return convertToEiffelMessage(input, configuration, null);
	}
	
	public static EiffelMessage convertToEiffelMessage(Event input, String routingKey)
	{
		return convertToEiffelMessage(input, null, routingKey);
	}
	
	public static EiffelMessage convertToEiffelMessage(Event input, EiffelConfiguration configuration, String routingKey)
	{
		EiffelEvent event = convertToEiffelEvent(input);
		return convertToEiffelMessage(event, configuration, routingKey);
	}
	
	public static EiffelMessage convertToEiffelMessage(EiffelEvent input)
	{
		return convertToEiffelMessage(input, null, null);
	}
	
	public static EiffelMessage convertToEiffelMessage(EiffelEvent input, EiffelConfiguration configuration)
	{
		return convertToEiffelMessage(input, configuration, null);
	}
	
	public static EiffelMessage convertToEiffelMessage(EiffelEvent input, EiffelConfiguration configuration, String routingKey)
	{
		OngoingMessageConfiguration messageConfig = EiffelMessage.Factory.configure(configuration.getDomainId(), input);
		if(routingKey != null && routingKey.length() > 0)
			messageConfig.setRoutingKeyTagWord(routingKey);
		return messageConfig.create();
	}
	
	public static Event convertToEvent(EiffelEvent event)
	{
		Event result = Event.fromJson(event.getOptionalParameter(fieldName));
		result.setSourceEiffelEvent(event);
		return result;
	}
	
	public static Event convertToEvent(EiffelMessage message)
	{
		Event result = convertToEvent(message.getEvent());
		result.setSourceEiffelMessage(message);
		return result;
	}
}