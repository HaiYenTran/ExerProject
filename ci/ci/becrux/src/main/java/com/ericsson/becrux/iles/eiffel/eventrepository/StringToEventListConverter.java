package com.ericsson.becrux.iles.eiffel.eventrepository;

import com.ericsson.becrux.base.common.eiffel.EiffelEventConverter;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.iles.eiffel.events.IlesEventFactory;
import com.ericsson.duraci.eiffelmessage.deserialization.Deserializer;
import com.ericsson.duraci.eiffelmessage.deserialization.exceptions.MessageDeserializationException;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import retrofit.Converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to convert the string input into list of events {@Event}
 */
// TODO: review if it can be on CORE ?
public class StringToEventListConverter implements Converter<String, List<Event>> {
    private static final String itemsName = "items";

    private EiffelEventConverter converter = new EiffelEventConverter(IlesEventFactory.getInstance());

    @Override
    public List<Event> convert(String input) throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(input).getAsJsonObject();
        return convert(obj);
    }

    public List<Event> convert(InputStream input) throws IOException {
        return convert(new InputStreamReader(input));
    }

    public List<Event> convert(Reader input) throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(input).getAsJsonObject();
        return convert(obj);
    }

    /**
     * Retrieves the list of eiffel messages from JsonObject and
     * converts it into the list of events {@Event}.
     * @param input JsonObject containing list of eiffel messages
     * @return List of events
     * @throws IOException
     */
    public List<Event> convert(JsonObject input) throws IOException {
        try {
            JsonArray items = input.getAsJsonArray(itemsName);
            Deserializer des = new Deserializer();
            List<Event> result = new ArrayList<>();
            for (JsonElement element : items) {
                try {
                    EiffelMessage message = des.deserialize(element.toString());
                    Event event = converter.convertToEvent(message);
                    result.add(event);
                } catch (NullPointerException ex) {
                    continue;
                }
            }
            return result;
        } catch (MessageDeserializationException ex) {
            throw new IOException(ex);
        }
    }
}
