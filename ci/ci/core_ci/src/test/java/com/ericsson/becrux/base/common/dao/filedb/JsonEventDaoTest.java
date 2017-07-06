package com.ericsson.becrux.base.common.dao.filedb;

import com.ericsson.becrux.base.common.dao.EventDao;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventFactory;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventImpl;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests {@link JsonEventDao}
 */
public class JsonEventDaoTest {

    private EventDao dao;
    private File dir;

    /**
     * Setting up before test.
     */
    @Before
    public void setUp() {
        this.dir = Files.createTempDir();
        this.dao = new JsonEventDao(dir.getPath(), new BaseEventFactory());
    }

    @Test
    public void testSaveAndLoadEmptyEventQueue() throws IOException {
        try {
            dao.loadEventQueue("");
            fail("Exception not thrown when calling loadEventQueue with empty name");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Name cannot be empty"));
        }

        try {
            List<Event> events = new ArrayList<>();
            dao.saveEventQueue("", events);
            fail("Exception not thrown when calling saveEventQueue with empty name");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Name cannot be empty"));
        }
    }

    @Test
    public void testSaveAndLoadEventQueue() throws IOException, IllegalArgumentException {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            BaseEventImpl event = new BaseEventImpl();
            event.setBuildId("" + i);
            events.add(event);
        }
        dao.saveEventQueue("queue", events);

        try {
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }

        assertEquals(events, dao.loadEventQueue("queue"));
    }

    @Test
    public void testSetGetNodeFactory() throws IOException {
        EventFactory factory = new BaseEventFactory();
        dao.setEventFactory(factory);
        assertEquals(factory, dao.getEventFactory());
    }

    @Test
    public void testOverrideEventsInQueue() throws Exception {
        List<Event> newEvents = new ArrayList<>();
        Event event1 = new BaseEventImpl();
        newEvents.add(event1);
        newEvents.add(new BaseEventImpl());
        dao.saveEventQueue("TEST", newEvents);

        String buildID = "111111";
        event1.setBuildId(buildID);
        newEvents.clear();
        newEvents.add(event1);

        dao.addEventToQueue("TEST", newEvents);

        List<Event> result = new ArrayList<>(dao.loadEventQueue("TEST"));
        assertTrue(result != null);
        assertTrue(result.size() == 2);

        result = result.stream().filter(e -> buildID.equals(e.getBuildId())).collect(Collectors.toList());
        assertTrue(result != null);
        assertTrue(result.size() == 1);
    }

    @Test
    public void testRemoveEventInQueue() throws Exception {
        String queueName = "queue1";

        Event event1 = new BaseEventImpl();
        Event event2 = new BaseEventImpl();

        List<Event> eventQueue = new ArrayList<>();
        eventQueue.add(event1);
        eventQueue.add(event2);

        dao.saveEventQueue(queueName, eventQueue);

        eventQueue.remove(event2);
        eventQueue.add(new BaseEventImpl());
        eventQueue.add(new BaseEventImpl());
        dao.removeEventsInQueue(queueName, eventQueue);

        List<Event> result = new ArrayList<>(dao.loadEventQueue(queueName));
        assertTrue(result.size() == 1);
        assertTrue(result.get(0).equals(event2));
    }

    /**
     * Clean up instances after tests.
     * @throws Exception
     */
    @After
    public void cleanUp() throws Exception {
        FileUtils.deleteDirectory(dir);
        this.dao = null;
        this.dir = null;
    }
}
