package com.ericsson.becrux.base.common.eiffel.eventrepository;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

// TODO: review if it can be on CORE ?
public class EventRepositoryCommunicator implements IEventRepositoryCommunicator {
    private final static String eventsUrl = "/restapi/events/";
    private final static String downstreamUrl = "/downstream/";
    private final static String upstreamUrl = "/upstream/";

    private String baseUrl;
    private StringToEventListConverter listConverter = new StringToEventListConverter();
    private StringToEventConverter singleConverter = new StringToEventConverter();
    private HttpClient client = new HttpClient();

    public EventRepositoryCommunicator(String baseUrl) throws EventRepositoryException {
        if (baseUrl == null || baseUrl.length() <= 0)
            throw new NullPointerException("Url cannot be null");
        try {
            this.baseUrl = new URL(baseUrl).toString();
            if (baseUrl.endsWith("/"))
                this.baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        } catch (MalformedURLException ex) {
            throw new EventRepositoryException(ex);
        }
    }

    @Override
    public Event getEvent(String id) throws EventRepositoryException {
        String target = new StringBuilder(baseUrl).append(eventsUrl).append(id).toString();
        try {
            HttpMethod method = new GetMethod(new URL(target).toString());
            client.executeMethod(method);
            return singleConverter.convert(method.getResponseBodyAsString());
        } catch (IOException ex) {
            throw new EventRepositoryException(ex);
        }
    }

    @Override
    public List<Event> getEventList() throws EventRepositoryException {
        String target = new StringBuilder(baseUrl).append(eventsUrl).toString();
        try {
            HttpMethod method = new GetMethod(new URL(target).toString());
            client.executeMethod(method);
            return listConverter.convert(method.getResponseBodyAsStream());
        } catch (IOException ex) {
            throw new EventRepositoryException(ex);
        }
    }

    @Override
    public List<Event> getDownstreamEventList(String id) throws EventRepositoryException {
        String target = new StringBuilder(baseUrl).append(eventsUrl).append(id).append(downstreamUrl).toString();
        try {
            HttpMethod method = new GetMethod(new URL(target).toString());
            client.executeMethod(method);
            return listConverter.convert(method.getResponseBodyAsStream());
        } catch (IOException ex) {
            throw new EventRepositoryException(ex);
        }
    }

    @Override
    public List<Event> getDownstreamEventList(Event event) throws EventRepositoryException {
        return getDownstreamEventList(event.getSourceEiffelMessage().getEventId().toString());
    }

    @Override
    public List<Event> getUpstreamEventList(String id) throws EventRepositoryException {
        String target = new StringBuilder(baseUrl).append(eventsUrl).append(id).append(upstreamUrl).toString();
        try {
            HttpMethod method = new GetMethod(new URL(target).toString());
            client.executeMethod(method);
            return listConverter.convert(method.getResponseBodyAsStream());
        } catch (IOException ex) {
            throw new EventRepositoryException(ex);
        }
    }

    @Override
    public List<Event> getUpstreamEventList(Event event) throws EventRepositoryException {
        return getUpstreamEventList(event.getSourceEiffelMessage().getEventId().toString());
    }
}
