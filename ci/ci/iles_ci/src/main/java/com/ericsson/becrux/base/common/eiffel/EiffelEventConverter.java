package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.configuration.EiffelJenkinsGlobalConfiguration;
import com.ericsson.duraci.eiffelmessage.messages.EiffelEvent;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage.Factory.OngoingMessageConfiguration;
import com.ericsson.duraci.eiffelmessage.messages.events.EiffelGenericEvent;

public class EiffelEventConverter {
    private static final String fieldName = "json";
    private EventFactory eventFactory;

    public EiffelEventConverter(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    /**
     * Convert {@link Event} to EiffelEvent.
     * @param input
     * @return
     */
    public EiffelEvent convertToEiffelEvent(Event input) {
        EiffelEvent event = EiffelGenericEvent.Factory.create(input.getClass().getSimpleName());
        event.setOptionalParameter(fieldName, eventFactory.toJson(input));
        return event;
    }

    /**
     * Convert {@link Event} to EiffelMessage.
     * @param input
     * @return
     */
    public EiffelMessage convertToEiffelMessage(Event input) {
        return convertToEiffelMessage(input, null, null);
    }

    public EiffelMessage convertToEiffelMessage(Event input, EiffelConfiguration configuration) {
        return convertToEiffelMessage(input, configuration, null);
    }

    public EiffelMessage convertToEiffelMessage(Event input, String routingKey) {
        return convertToEiffelMessage(input, null, routingKey);
    }

    public EiffelMessage convertToEiffelMessage(Event input, EiffelConfiguration configuration, String routingKey) {
        EiffelEvent event = convertToEiffelEvent(input);
        return convertToEiffelMessage(event, configuration, routingKey);
    }

    public EiffelMessage convertToEiffelMessage(EiffelEvent input) {
        return convertToEiffelMessage(input, null, null);
    }

    public EiffelMessage convertToEiffelMessage(EiffelEvent input, EiffelConfiguration configuration) {
        return convertToEiffelMessage(input, configuration, null);
    }

    public EiffelMessage convertToEiffelMessage(EiffelEvent input, EiffelConfiguration configuration, String routingKey) {
        if (configuration == null)
            configuration = new EiffelJenkinsGlobalConfiguration.Provider().provide();

        OngoingMessageConfiguration messageConfig = EiffelMessage.Factory.configure(configuration.getDomainId(), input);
        if (routingKey != null && routingKey.length() > 0)
            messageConfig.setRoutingKeyTagWord(routingKey);
        return messageConfig.create();
    }

    public Event convertToEvent(EiffelEvent event) {
        Event result = eventFactory.fromJson(event.getOptionalParameter(fieldName));
        result.setSourceEiffelEvent(event);
        return result;
    }

    public Event convertToEvent(EiffelMessage message) {
        Event result = convertToEvent(message.getEvent());
        result.setSourceEiffelMessage(message);
        return result;
    }
}