package com.ericsson.becrux.iles.dao.filedb;

import com.ericsson.becrux.base.common.dao.ComponentDao;
import com.ericsson.becrux.base.common.dao.EventDao;
import com.ericsson.becrux.base.common.dao.filedb.JsonComponentDao;
import com.ericsson.becrux.base.common.dao.filedb.JsonEventDao;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.loop.Phase;
import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.iles.data.*;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import com.ericsson.becrux.iles.eiffel.events.IlesEventFactory;
import com.ericsson.becrux.iles.eventhandler.EventSchedulerComparator;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by emiwaso on 2016-12-07.
 */
public class JsonCommonDaoFromIlesTests {

    private static EventSchedulerComparator _comaprator = new EventSchedulerComparator();

    @Test
    public void testBasicFindingNodesBaselineConfirmed() throws IOException {
        File myTempDir = null;
        try {
            myTempDir = Files.createTempDir();
            setUpStandardTestFiles(myTempDir);

            ComponentDao dao = new JsonComponentDao(myTempDir.toString(), IlesComponentFactory.getInstance());
            Component mtas = null;
            try {
                mtas = dao.loadNewestComponent("MTAS", Component.State.BASELINE_APPROVED);
            } catch (IOException e) {
                fail("Problem finding latest MTAS");
                e.printStackTrace();
            }

            assertTrue(mtas!=null);
            assertEquals("1.0.1", mtas.getVersion());


            Component cscf = null;
            try {
                cscf = dao.loadNewestComponent("CSCF", Component.State.BASELINE_APPROVED);
            } catch (IOException e) {
                fail("Problem finding latest CSCF");
                e.printStackTrace();
            }

            assertTrue(cscf==null);

            Component int_comp = null;
            try {
                int_comp = dao.loadNewestComponent("INT", Component.State.BASELINE_APPROVED);
            }catch (IOException e) {
                fail("Problem finding latest INT");
                e.printStackTrace();
            }

            assertTrue(int_comp!=null);
            assertEquals("/tmp/INT_latest2.tar.gz", int_comp.getArtifact());
        }
        finally {
            if(myTempDir != null)
                FileUtils.deleteDirectory(myTempDir);
        }
    }

    @Test
    public void testFindingTheLatestMtasReadyForBaselineLoopFromMultiplePossible() throws IOException {
        File myTempDir = null;
        try {
            myTempDir = Files.createTempDir();
            prepareMtases(myTempDir);

            ComponentDao dao = new JsonComponentDao(myTempDir.toString(), IlesComponentFactory.getInstance());
            Component mtas = null;

            try {
                mtas = dao.loadNewestComponent("MTAS", Component.State.BASELINE_APPROVED, Component.State.BASELINE_CANDIDATE);
            } catch (IOException e) {
                fail("Problem finding latest suitable MTAS");
                e.printStackTrace();
            }

            assertNotNull(mtas);
            assertEquals("9.0", mtas.getVersion());
        }
        finally {
            if(myTempDir != null)
                FileUtils.deleteDirectory(myTempDir);
        }
    }

    @Test
    public void testEventQueueSerializationAndDeserialization() throws IOException {
        File path = null;
        try {
            path = Files.createTempDir();
            EventDao dao = new JsonEventDao(path.getAbsolutePath(), IlesEventFactory.getInstance());
            Collection<Event> queue = createEventsQueue().stream().sorted(_comaprator).collect(Collectors.toList());
            dao.saveEventQueue("abcd", queue);

            Collection<Event> q = dao.loadEventQueue("abcd").stream().sorted(_comaprator).collect(Collectors.toList());
            assertEquals(3, q.size());
            assertEquals(queue, q);
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadingQueueWithEmptyName() throws IOException {
        File path = null;
        try {
            path = Files.createTempDir();
            EventDao dao = new JsonEventDao(path.getAbsolutePath(), IlesEventFactory.getInstance());

            Collection<Event> q = dao.loadEventQueue("");
        } finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testLoadingNonExistantQueue() throws IOException {
        File path = null;
        try {
            path = Files.createTempDir();
            EventDao dao = new JsonEventDao(path.getAbsolutePath(), IlesEventFactory.getInstance());
            Collection<Event> queue = createEventsQueue().stream().sorted(_comaprator).collect(Collectors.toList());
            dao.saveEventQueue("abcd", queue);

            Collection<Event> q1 = dao.loadEventQueue("abcd").stream().sorted(_comaprator).collect(Collectors.toList());
            Collection<Event> q2 = dao.loadEventQueue("defg").stream().sorted(_comaprator).collect(Collectors.toList());
            assertEquals(3, q1.size());
            assertEquals(queue, q1);
            assertEquals(0, q2.size());
            assertNotEquals(queue, q2);
        } catch(Exception ex) {
            System.out.println(ex);
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    private Collection<Event> createEventsQueue() {
        List<Event> queue = new ArrayList<>();
        ITREvent e = new ITREvent();
        Component node = new Mtas("21.0");
        e.setProduct(node.getType());
        e.setBaseline(node.getVersion());
        e.setBuildId("asdfads3523sdfsd");
        queue.add(e);

        ITREvent e2 = new ITREvent();
        Component node2 = new Ibcf("2.0");
        e2.setProduct(node2.getType());
        e2.setBaseline(node2.getVersion());
        e2.setBuildId("34gads3523sdfsd");
        queue.add(e2);

        BTFEvent e3 = new BTFEvent();
        e3.setProducts(IlesComponentFactory.getInstance().getRegisteredClassNames());
        List<String> baselines = new ArrayList<>();
        for(int i = 0; i < e3.getProducts().size(); ++i)
            baselines.add("1.0");
        e3.setBaselines(baselines);
        e3.setJobId(1);
        e3.setPhase(Phase.VERIFICATION);
        e3.setPhaseStatus(PhaseStatus.SUCCESS);
        queue.add(e3);

        return queue;
    }

    private List<Component> prepareMtases(File dir) {

        ComponentDao dao = new JsonComponentDao(dir.toString(), IlesComponentFactory.getInstance());
        List<Component> list = new ArrayList<Component>();

        list.add(new Mtas("9.0", Component.State.BASELINE_APPROVED));
        list.add(new Mtas("10.0", Component.State.BASELINE_CANDIDATE));
        list.add(new Mtas("3.0", Component.State.BASELINE_APPROVED));
        list.add(new Mtas("6.0", Component.State.BASELINE_APPROVED));
        list.add(new Mtas("6.1", Component.State.NEW_BUILD));
        list.add(new Mtas("11.0", Component.State.NEW_BUILD));

        for (Component node : list) {
            try {
                dao.saveComponent(node);
            } catch (IOException e) {
                fail("Problem storing test data before the test.");
                e.printStackTrace();
            }
        }

        return list;
    }

    private void setUpStandardTestFiles(File dir) {

        ComponentDao dao = new JsonComponentDao(dir.toString(), IlesComponentFactory.getInstance());

        List<Component> nodeList = new ArrayList<Component>();
        Component m1 = new Mtas("1.0");
        m1.setState(Component.State.BASELINE_APPROVED);
        nodeList.add(m1);
        Component m11 = new Mtas("1.0.1");
        m11.setState(Component.State.BASELINE_APPROVED);
        nodeList.add(m11);
        Component m2 = new Mtas("1.1");
        m2.setState(Component.State.BASELINE_CANDIDATE);
        nodeList.add(m2);
        Component m3 = new Mtas("2.0");
        m3.setState(Component.State.NEW_BUILD);
        nodeList.add(m3);

        Component c2 = new Cscf("6.3.0");
        c2.setState(Component.State.BASELINE_CANDIDATE);
        nodeList.add(c2);

        Component c3 = new Cscf("6.4");
        c3.setState(Component.State.NEW_BUILD);
        nodeList.add(c3);

        Component mr1 = new Pcscf("0.2");
        mr1.setState(Component.State.BASELINE_APPROVED);
        nodeList.add(mr1);

        Component mr2 = new Pcscf("0.3");
        mr2.setState(Component.State.BASELINE_CANDIDATE);
        nodeList.add(mr2);

        Component mr3 = new Pcscf("1.0");
        mr3.setState(Component.State.NEW_BUILD);
        nodeList.add(mr3);

        Component s1 = new Ibcf("1.0.2");
        s1.setState(Component.State.BASELINE_APPROVED);
        nodeList.add(s1);

        Component s2 = new Ibcf("1.0.3");
        s2.setState(Component.State.BASELINE_CANDIDATE);
        nodeList.add(s2);

        Component s3 = new Ibcf("2.1.0");
        s3.setState(Component.State.NEW_BUILD);
        nodeList.add(s3);
        Component i1 = new Int("1.0");
        i1.setArtifact("/tmp/INT_latest1.tar.gz");
        i1.setState(Component.State.BASELINE_APPROVED);
        nodeList.add(i1);

        Component i2 = new Int("2.0");
        i2.setArtifact("/tmp/INT_latest2.tar.gz");
        i2.setState(Component.State.BASELINE_APPROVED);
        nodeList.add(i2);

        Component agw1 = new Agw("2.0");
        agw1.setState(Component.State.BASELINE_APPROVED);
        nodeList.add(agw1);  

        Component trgw2 = new Trgw("2.0");
        trgw2.setState(Component.State.BASELINE_APPROVED);
        nodeList.add(trgw2);

        for (Component node : nodeList) {
            try {
                dao.saveComponent(node);
            } catch (IOException e) {
                fail("Problem storing test data before the test.");
                e.printStackTrace();
            }
        }
    }
}
