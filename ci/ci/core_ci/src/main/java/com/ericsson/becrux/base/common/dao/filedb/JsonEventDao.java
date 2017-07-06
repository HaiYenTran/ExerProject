package com.ericsson.becrux.base.common.dao.filedb;

import com.ericsson.becrux.base.common.dao.EventDao;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventFactory;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Implementation of {@link EventDao}
 */
public class JsonEventDao extends JsonCommonDao implements EventDao {

    public static final String EVENTS_QUEUE_JSON_PATH_PREFIX = "queue";
    public static final String EVENTS_QUEUE_JSON_PATH_POSTFIX = ".json";

    private EventFactory eventFactory;

    /**
     * Constructor.
     * @param dirPath
     */
    @Deprecated
    public JsonEventDao(@Nonnull String dirPath) {
        this(dirPath, new BaseEventFactory());
    }

    /**
     * Constructor.
     * @param dirPath
     */
    public JsonEventDao(@Nonnull String dirPath, EventFactory eventFactory) {
        this.dir = Paths.get(dirPath);
        this.eventFactory = eventFactory;

        checkSynchronizeLock();
    }

    /** {@inheritDoc} */
    public EventFactory getEventFactory() {
        return eventFactory;
    }

    /** {@inheritDoc} */
    public void setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void checkSynchronizeLock() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Event> loadEventQueue(@Nonnull String name) throws IOException {
        synchronized (this) {
            return loadEventQueueFromJsonFile(name);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveEventQueue(@Nonnull String name, @Nonnull Collection<Event> queue) throws IOException {
        synchronized (this) {
            saveEventQueueToJsonFile(name, queue);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addEventToQueue(String name, Collection<Event> events) throws IOException {
        synchronized (this) {
            Collection<Event> eventsInQueue = this.loadEventQueueFromJsonFile(name);

            events.forEach(e -> {
                if (eventsInQueue.contains(e)) {
                    eventsInQueue.remove(e);
                }
            });
            eventsInQueue.addAll(events);

            saveEventQueueToJsonFile(name, eventsInQueue);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeEventsInQueue(@Nonnull String name,@Nonnull Collection<Event> removedEvents) throws IOException {
        synchronized (this) {
            Collection<Event> eventsInQueue = this.loadEventQueueFromJsonFile(name);

//            removedEvents.forEach(e -> {
//                if (eventsInQueue.contains(e)) {
//                    eventsInQueue.remove(e);
//                }
//            });
            eventsInQueue.removeAll(removedEvents);

            this.saveEventQueueToJsonFile(name, eventsInQueue);
        }
    }

    private Collection<Event> loadEventQueueFromJsonFile(@Nonnull String name) throws IOException {
        if (name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        synchronized (this) {
            List<Event> queue = new ArrayList<>();
            for (String type : eventFactory.getRegisteredClassNames()) {
                Path p = Paths.get(EVENTS_QUEUE_JSON_PATH_PREFIX, name, type + EVENTS_QUEUE_JSON_PATH_POSTFIX);
                List<Event> events = readEventListFromJsonFile(p.toString(), type);
                queue.addAll(events);
            }
            return queue;
        }
    }

    private void saveEventQueueToJsonFile(@Nonnull String name, @Nonnull Collection<Event> queue) throws IOException{
        if (name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");

        Map<String, Event[]> map = splitEventsByType(queue);
        // WARNING: because we delete queue folder contain events , we may missing some pending event.
        FileUtils.deleteDirectory(dir.resolve(EVENTS_QUEUE_JSON_PATH_PREFIX).resolve(name).toFile());
        for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
            String type = iterator.next();
            Path p = Paths.get(EVENTS_QUEUE_JSON_PATH_PREFIX, name, type + EVENTS_QUEUE_JSON_PATH_POSTFIX);
            writeStringToJsonFile(eventFactory.convertCollectionToJson(map.get(type)), p.toString());
        }
    }

    private List<Event> readEventListFromJsonFile(@Nonnull String fileName,@Nonnull String eventType) throws IOException {
        List<Event> events = new ArrayList<>();

        // if condition for case the path to queue file not exits
        File file = dir.resolve(fileName).toFile();
        if(file.exists()) {
            String jsonTxt = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            List<Event> es = eventFactory.fromJsonToList(jsonTxt);
            events.addAll(es);
        }

        return events.stream().filter(e -> eventType.equals(e.getType())).collect(Collectors.toList());
    }

    private Map<String, Event[]> splitEventsByType(Collection<Event> queue) {
        Map<String, List<Event>> res = createMapToLists(queue);
        return convertMapToArrayTypedValues(res);
    }


}
