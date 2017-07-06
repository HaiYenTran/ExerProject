package com.ericsson.becrux.base.common.dao.filedb;

import com.ericsson.becrux.base.common.dao.CommonDao;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.utils.CIFileHelper;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Abstract class define base methods of {@link CommonDao}.
 */
public abstract class JsonCommonDao implements CommonDao {

    protected Path dir;  //Path of the current instance

    /** {@inheritDoc} */
    @Override
    public Path getPath() {
        return dir;
    }

    /** {@inheritDoc} */
    @Override
    public void setPath(String path) {
        this.dir = Paths.get(path);
    }

    /** {@inheritDoc} */
    @Override
    public boolean validate() throws Exception {
        return CIFileHelper.validateFile(dir.toString(), true, true, false, false, true);
    }

    protected Map<String, Event[]> convertMapToArrayTypedValues(Map<String, List<Event>> map) {
        Map<String, Event[]> res = new HashMap<String, Event[]>();
        for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            List<Event> l = map.get(key);
            Event[] a = l.toArray(new Event[0]);
            res.put(key, a);
        }
        return res;
    }

    protected Map<String, List<Event>> createMapToLists(Collection<Event> queue) {
        Map<String, List<Event>> res = new HashMap<String, List<Event>>();
        for (Event ev : queue) {
            String type = ev.getType();
            if (res.get(type) == null) {
                List<Event> temp = new ArrayList<>();
                temp.add(ev);
                res.put(type, temp);
            } else {
                res.get(type).add(ev);
            }
        }

        return res;
    }

    protected void saveObjectToJsonFile(Object obj, String fileName) throws IOException {
        if (!dir.toFile().mkdirs() && !dir.toFile().isDirectory()) // Create all directories along the path
            throw new IOException("Cannot create necessary directories, aborting");
        File f = dir.resolve(fileName).toFile();
        FileUtils.writeStringToFile(f, (new Gson()).toJson(obj), StandardCharsets.UTF_8);
    }

    protected void writeStringToJsonFile(String s, String fileName) throws IOException {
        if (!dir.toFile().mkdirs() && !dir.toFile().isDirectory()) // Create all directories along the path
            throw new IOException("Cannot create necessary directories, aborting");
        File f = dir.resolve(fileName).toFile();
        FileUtils.writeStringToFile(f, s, StandardCharsets.UTF_8);
    }

    protected String generateFileNameFor(String componentName, String version) {
        return new StringBuilder()
                .append(componentName.toUpperCase())
                .append('_')
                .append(version.replace(File.separatorChar, '_'))
                .append(".json")
                .toString();
    }
}
