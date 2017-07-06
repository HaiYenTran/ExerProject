package com.ericsson.becrux.base.common.eiffel.events.impl;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link EventFactory} with base implement {@link BaseEventFactory}
 */
public class EventFactoryTest {

    EventFactory eventFactory;

    @Before
    public void setUp(){
        eventFactory = new BaseEventFactory();
    }

    @Test
    public void testCreateEvent() {
        Event event = new BaseEventImpl();
        event.setBuildId("1");

        String json = eventFactory.toJson(event);
        assertTrue(json != null);
        event = eventFactory.fromJson(json);
        assertTrue(event != null);
        assertTrue(event instanceof BaseEventImpl);
        assertTrue("1".equals(event.getBuildId()));
        assertTrue(BaseEventImpl.class.getSimpleName().equals(event.getType()));
    }

    @Test
    public void testInheritDummy() {
        EventFactory inheritFactory = new TestInheritEventFactory();

        Event event = new TestDummyEvent();
        event.setBuildId("1");

        String json = inheritFactory.toJson(event);
        assertTrue(json != null);
        event = inheritFactory.fromJson(json);
        assertTrue(event != null);
        assertTrue(event instanceof TestDummyEvent);
        assertTrue("1".equals(event.getBuildId()));
        assertTrue(TestDummyEvent.class.getSimpleName().equals(event.getType()));
    }

    @Test
    public void testConvertManyEvents() {
        TestInheritEventFactory inheritFactory = new TestInheritEventFactory();

        Event event = new TestDummyEvent();
        event.setBuildId("1");
        Event event2 = new TestDummyEvent();
        event.setBuildId("2");

        Event[] events = { event, event2};
        List<Event> list = new ArrayList<>();
        list.add(event);
        list.add(event2);

        String json1 = inheritFactory.toJson(event);
        event = inheritFactory.fromJson(json1);
        assertTrue(event != null);

        String json2 = inheritFactory.convertCollectionToJson(events);
        List<Event> list2 = inheritFactory.fromJsonToList(json2);
        assertTrue(list2 != null);

        String json3 = inheritFactory.convertCollectionToJson(list);
        list = inheritFactory.fromJsonToList(json3);
        assertTrue(list != null);
    }

}
