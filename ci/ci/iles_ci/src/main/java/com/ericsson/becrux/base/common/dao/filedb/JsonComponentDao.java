package com.ericsson.becrux.base.common.dao.filedb;

import com.ericsson.becrux.base.common.core.ComponentFactory;
import com.ericsson.becrux.base.common.dao.ComponentDao;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.data.impl.BaseComponentFactory;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DirectoryScanner;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * The Implementation of {@link ComponentDao}.
 */
public class JsonComponentDao extends JsonCommonDao implements ComponentDao {
    private static final ConcurrentMap<Path, Object> _componentLocks = new ConcurrentHashMap<>(); //Locks for component handling
    private static Comparator<Component> _componentVersionOrderingComparator = (o1, o2) -> {
        if(o1.getVersion() == null && o2.getVersion() != null)
            return -1;
        else if(o1.getVersion() != null && o2.getVersion() == null)
            return 1;
        else
            return o1.getVersion().compareTo(o2.getVersion());
    };

    private ComponentFactory nodeFactory;

    /**
     * Constructor.
     * @param dirPath
     */
    @Deprecated
    public JsonComponentDao(@Nonnull String dirPath) {
        this(dirPath, new BaseComponentFactory());
    }

    /**
     * Constructor.
     * @param dirPath
     */
    public JsonComponentDao(@Nonnull String dirPath, ComponentFactory nodeFactory) {
        this.dir = Paths.get(dirPath);
        this.nodeFactory = nodeFactory;

        checkSynchronizeLock();
    }

    /** {@inheritDoc} */
    @Override
    public Path getPath() {
        return dir;
    }

    /** {@inheritDoc} */
    @Override
    public boolean validate() throws Exception {
        List<Exception> exceptions = new ArrayList<>();
        nodeFactory.getRegisteredClasses().forEach((k, v) -> {
            try {
                // get all node with same type but different version
                List<Component> nodes = getAllComponentsByType(k, dir);

                // case 1: have at least 1 version with status Component.State.BASELINE_APPROVED
                List<Component> approvedNodes = nodes.stream().filter(n -> Component.State.BASELINE_APPROVED.equals(n.getState())).collect(Collectors.toList());
                if (approvedNodes == null || approvedNodes.size() == 0) {
                    throw new Exception("Missing approved version of type [" + k + "]");
                }

                Set<Component> nodeSet = new HashSet<>(nodes);
                if(nodeSet.size() < nodes.size()) {
                    throw new Exception("Having duplicate Version of type [" + k + "]");
                }
            }catch (Exception e) {
                exceptions.add(e);
            }
        });

        return true;
    }

    /**
     * In case all the queue lock not initialize correctly
     */
    @Override
    public void checkSynchronizeLock() {
        if (_componentLocks.get(this.dir) == null) {
            _componentLocks.putIfAbsent(this.dir, new Object());
        }
    }

    /** {@inheritDoc} */
    @Override
    public ComponentFactory getNodeFactory() {
        return nodeFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void setNodeFactory(ComponentFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void saveComponent(@Nonnull Component component) throws IOException {
        synchronized (_componentLocks.get(dir)) {
            saveObjectToJsonFile(component, generateFileNameFor(component.getType(), component.getVersion().getVersion()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Component loadComponent(@Nonnull String type, @Nonnull String version) throws IOException {
        synchronized (_componentLocks.get(dir)) {
            return readComponentFromJsonFile(generateFileNameFor(type, version));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Component loadNewestComponent(@Nonnull String type) throws IOException {
        synchronized (_componentLocks.get(dir)) {
            List<Component> components = getAllComponentsByType(type, dir);
            components.sort(_componentVersionOrderingComparator);
            return components.size() > 0 ? components.get(components.size() - 1) : null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Component loadNewestComponent(@Nonnull String type, @Nonnull Component.State... states) throws IOException {
        synchronized (_componentLocks.get(dir)) {
            List<Component> components = getAllComponentsByType(type, dir).stream()
                    .filter(c -> Arrays.asList(states).contains(c.getState()))
                    .collect(Collectors.toList());
            components.sort(_componentVersionOrderingComparator);
            return components.size() > 0 ? components.get(components.size() - 1) : null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeComponent(@Nonnull String type, @Nonnull String version) throws IOException {
        synchronized (_componentLocks.get(dir)) {
            if (loadComponent(type, version) != null) {
                File f = dir.resolve(generateFileNameFor(type, version)).toFile();
                FileUtils.deleteQuietly(f);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Component.State loadComponentState(@Nonnull Component component) throws IOException {
        return loadComponentState(component.getType(), component.getVersion().getVersion());
    }

    /** {@inheritDoc} */
    @Override
    public Component.State loadComponentState(String type, String version) {
        synchronized (_componentLocks.get(dir)) {
            Component n = readComponentFromJsonFile(generateFileNameFor(type, version));
            return (n == null) ? null : n.getState();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveComponentState(Component component, Component.State state) throws IOException {
        saveComponentState(component.getType(), component.getVersion().getVersion(), state);
    }

    /** {@inheritDoc} */
    @Override
    public void saveComponentState(String type, String version, Component.State state) throws IOException {
        synchronized (_componentLocks.get(dir)) {
            String fileName = generateFileNameFor(type, version);
            Component c = readComponentFromJsonFile(fileName);
            c.setState(state);
            saveObjectToJsonFile(c, fileName);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Component> loadAllComponents(String type, Component.State... states) throws IOException {
        synchronized (_componentLocks.get(dir)) {
            List<Component> components = getAllComponentsByType(type, dir).stream()
                    .filter(c -> Arrays.asList(states).contains(c.getState()))
                    .collect(Collectors.toList());
            components.sort(_componentVersionOrderingComparator);
            return components;
        }
    }

    //Private methods
    private Component readComponentFromJsonFile(String fileName) {
        try {
            String jsonTxt = new String(Files.readAllBytes(dir.resolve(fileName)), StandardCharsets.UTF_8);
            return nodeFactory.fromJson(jsonTxt);
        } catch (IOException e) {
            return null;
        }
    }

    private List<Component> getAllComponentsByType(String type, Path dir) throws IOException {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{type.toString().toUpperCase() + "*.json"});
        scanner.setBasedir(dir.toString());
        scanner.setCaseSensitive(true);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();

        List<Component> components = new ArrayList<>();
        for (String fileName : files) {
            components.add(readComponentFromJsonFile(fileName));
        }
        return components;
    }
}
