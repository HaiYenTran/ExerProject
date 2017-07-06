package com.ericsson.becrux.iles.eiffel.eventrepository;

import com.ericsson.becrux.base.common.eiffel.EiffelEventConverter;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.iles.eiffel.events.IlesEventFactory;
import com.ericsson.duraci.eiffelmessage.deserialization.Deserializer;
import com.ericsson.duraci.eiffelmessage.deserialization.exceptions.MessageDeserializationException;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import retrofit.Converter;

import java.io.IOException;

// TODO: review if it can be on CORE ?
public class StringToEventConverter implements Converter<String, Event> {

    private EiffelEventConverter convert = new EiffelEventConverter(IlesEventFactory.getInstance());
    @Override
    public Event convert(String input) throws IOException {
        try {
            Deserializer des = new Deserializer();
            EiffelMessage message = des.deserialize(input);
            return convert.convertToEvent(message);
        } catch (MessageDeserializationException ex) {
            throw new IOException(ex);
        }
    }
}
