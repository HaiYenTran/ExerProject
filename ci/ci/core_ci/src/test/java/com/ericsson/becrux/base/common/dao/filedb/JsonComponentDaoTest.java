package com.ericsson.becrux.base.common.dao.filedb;

import com.ericsson.becrux.base.common.core.ComponentFactory;
import com.ericsson.becrux.base.common.dao.ComponentDao;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.data.impl.BaseComponentFactory;
import com.ericsson.becrux.base.common.data.impl.BaseComponentImpl;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test {@link JsonComponentDao}
 */
public class JsonComponentDaoTest {

    private ComponentDao dao;
    private File dir;

    /**
     * Setting up before test.
     */
    @Before
    public void setUp() {
        this.dir = Files.createTempDir();
        this.dao = new JsonComponentDao(dir.getPath(), new BaseComponentFactory());
    }

    @Test
    public void testSerializationAndDeserialization() throws IOException {
        Component node = new BaseComponentImpl("1.0");

        this.dao.saveComponent(node);
        ComponentDao dao2 = new JsonComponentDao(this.dir.getPath(), new BaseComponentFactory());
        Component node2 = dao2.loadComponent(BaseComponentImpl.class.getSimpleName(),"1.0");
        assertEquals(node, node2);
    }

    @Test
    public void testSerializationAndDeserializationRState() throws Exception {
        Component node = new BaseComponentImpl("R1A");

        this.dao.saveComponent(node);
        ComponentDao dao2 = new JsonComponentDao(this.dir.getPath(), new BaseComponentFactory());
        Component node2;

        node2 = dao2.loadComponent(BaseComponentImpl.class.getSimpleName(),"R1A");
        assertEquals(node, node2);
        assertTrue(this.dao.validate());
    }

    @Test
    public void testSavingState() throws IOException {

        Component defaultNode = new BaseComponentImpl("2.0", Component.State.NEW_BUILD);
        this.dao.saveComponent(defaultNode);
        try {
            this.dao.saveComponentState(new BaseComponentImpl("2.0"), Component.State.BASELINE_APPROVED);
            assertTrue(this.dao.validate());
        } catch (Exception e) {
            fail("Problem saving state of BaseNodeImpl 2.0");
            e.printStackTrace();
        }

        Component expected = null;
        try {
            expected = this.dao.loadComponent(BaseComponentImpl.class.getSimpleName(), "2.0");
        } catch (IOException e) {
            fail("Problem reading MTAS 2.0");
            e.printStackTrace();
        }
        assertTrue(expected.getState().equals(Component.State.BASELINE_APPROVED));
    }

    /* By-method tests */
    @Test
    public void testLoadNewestComponent() throws Exception {
        int numOfComponents = 10;
        Component newestComponent = new BaseComponentImpl();
        for (int i = 0; i < numOfComponents; ++i) {
            newestComponent = new BaseComponentImpl("1.0." + i);
            newestComponent.setState(Component.State.NEW_BUILD);
            this.dao.saveComponent(newestComponent);
        }

        this.dao.checkSynchronizeLock();
        assertTrue(dao.validate());
        List<Component> comps = this.dao.loadAllComponents(BaseComponentImpl.class.getSimpleName(),
                Component.State.NEW_BUILD);
        assertFalse(comps.isEmpty());
        assertEquals(numOfComponents, comps.size());
        Component loadedComponent = this.dao.loadNewestComponent(BaseComponentImpl.class.getSimpleName());
        assertEquals(newestComponent, loadedComponent);
    }

    @Test
    public void testLoadNewestComponentByState() throws Exception {
        int numOfEach = 5;
        Component newestComponent = new BaseComponentImpl();
        for (Component.State state : Component.State.values()) {
            for (int i = 0; i < numOfEach; ++i) {
                newestComponent = new BaseComponentImpl(state.ordinal() + ".0." + i);
                newestComponent.setState(state);
                this.dao.saveComponent(newestComponent);
            }
        }

        this.dao.checkSynchronizeLock();
        assertTrue(this.dao.validate());

        int lastState = Component.State.values().length;
        List<Component> comps = this.dao.loadAllComponents(BaseComponentImpl.class.getSimpleName(),
                Component.State.values()[lastState-1]);
        assertFalse(comps.isEmpty());
        assertEquals(numOfEach, comps.size());
        Component loadedComponent = this.dao.loadNewestComponent(BaseComponentImpl.class.getSimpleName(),
                Component.State.values()[lastState-1]);
        assertEquals(newestComponent, loadedComponent);
    }

    @Test
    public void testRemoveComponent() throws Exception {
        Component component;
        for (Component.State state : Component.State.values()) {
            component = new BaseComponentImpl("1.0." + state.ordinal());
            component.setState(state);
            this.dao.saveComponent(component);
        }

        this.dao.checkSynchronizeLock();
        assertTrue(this.dao.validate());

        List<Component> comps = this.dao.loadAllComponents(BaseComponentImpl.class.getSimpleName(),
                Component.State.values());
        assertFalse(comps.isEmpty());
        assertEquals(Component.State.values().length, comps.size());

        // Remove component and make sure it is removed from dao
        this.dao.removeComponent(BaseComponentImpl.class.getSimpleName(),
                "1.0." + Component.State.BASELINE_REJECTED.ordinal());
        comps = this.dao.loadAllComponents(BaseComponentImpl.class.getSimpleName(),
                Component.State.values());
        assertFalse(comps.isEmpty());
        assertEquals(Component.State.values().length-1, comps.size());
        assertNull(this.dao.loadComponent(BaseComponentImpl.class.getSimpleName(),
                "1.0." + Component.State.BASELINE_REJECTED.ordinal()));
    }

    @Test
    public void testLoadComponentState() throws Exception {
        Component component = new BaseComponentImpl("1.0.0");
        component.setState(Component.State.BASELINE_APPROVED);
        this.dao.saveComponent(component);
        this.dao.checkSynchronizeLock();
        assertTrue(dao.validate());

        assertEquals(null, this.dao.loadComponentState("NonExistingComponent", "1.0.0"));
        assertEquals(null, this.dao.loadComponentState(BaseComponentImpl.class.getSimpleName(), "1.0.1"));
        assertEquals(component.getState(), this.dao.loadComponentState(BaseComponentImpl.class.getSimpleName(), "1.0.0"));
        assertEquals(component.getState(), this.dao.loadComponentState(component));
    }

    @Test
    public void testSetGetNodeFactory() throws IOException {
        ComponentFactory factory = new BaseComponentFactory();
        dao.setNodeFactory(factory);
        assertEquals(factory, dao.getNodeFactory());
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
