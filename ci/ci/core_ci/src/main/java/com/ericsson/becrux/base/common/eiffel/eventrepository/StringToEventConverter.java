package com.ericsson.becrux.base.common.eiffel.eventrepository;

import com.ericsson.becrux.base.common.eiffel.EiffelEventConverter;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventFactory;
import com.ericsson.duraci.eiffelmessage.deserialization.Deserializer;
import com.ericsson.duraci.eiffelmessage.deserialization.exceptions.MessageDeserializationException;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import retrofit.Converter;

import java.io.IOException;

// TODO: review if it can be on CORE ?
public class StringToEventConverter implements Converter<String, Event> {
    private EiffelEventConverter converter;
    public StringToEventConverter() {
        super();
        this.converter = new EiffelEventConverter(new BaseEventFactory());
    }

    public StringToEventConverter(EiffelEventConverter converter) {
        super();
        this.converter = converter;
    }

    @Override
    public Event convert(String input) throws IOException {
        try {
            Deserializer des = new Deserializer();
            EiffelMessage message = des.deserialize(input);
            return converter.convertToEvent(message);
        } catch (MessageDeserializationException ex) {
            throw new IOException(ex);
        }
    }
}
