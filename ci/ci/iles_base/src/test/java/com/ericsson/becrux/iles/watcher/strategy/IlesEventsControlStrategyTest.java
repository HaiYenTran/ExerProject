package com.ericsson.becrux.iles.watcher.strategy;

import com.ericsson.becrux.base.common.core.NwftDownstreamJob;
import com.ericsson.becrux.base.common.dao.EventDao;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventImpl;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.google.common.io.Files;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Job;
import org.apache.commons.io.FileDeleteStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/******************************************************************************
 * Class under test: {@link IlesEventsControlStrategy}
 *****************************************************************************/

@RunWith(PowerMockRunner.class)
@PrepareForTest({NwftDownstreamJob.class,
                 Cause.UpstreamCause.class,
                 Job.class,
                 IlesGlobalConfig.class,
                 IlesEventsControlStrategy.class})
public class IlesEventsControlStrategyTest {
    private final String controllerJobName = "CONTROLLER";
    private final String eventQueueName = "testQueue";
    private File baseDir;
    private File log;
    private PrintStream logger;
    IlesEventsControlStrategy strategy;

    @Mock private EventDao dao;
    @Mock private AbstractBuild build;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        baseDir = Files.createTempDir();
        log = new File(baseDir, "log");
        log.createNewFile();
        logger = new PrintStream(log);

        IlesGlobalConfig config =  PowerMockito.mock(IlesGlobalConfig.class);
        PowerMockito.mockStatic(IlesGlobalConfig.class);
        when(IlesGlobalConfig.getInstance()).thenReturn(config);
        when(config.getEventDao()).thenReturn(dao);

        strategy = new IlesEventsControlStrategy(controllerJobName, eventQueueName, logger, dao, build);
        strategy.initializeData();
        assertNotNull(strategy);
    }

    @After
    public void cleanUp() throws Exception {
        logger.close();
        strategy = null;
        FileDeleteStrategy.FORCE.delete(baseDir);
    }

    /**********************************************************************
     * Tests:
     * - when exception is thrown, e.g. event queue does not exist.
     * - when the event queue is empty
     * - when the event queue is not empty
     *********************************************************************/
    @Test
    public void testHandleEventQueueThrowsException() {
        try {
            when(dao.loadEventQueue(anyString())).thenThrow(new IOException("No event queue found"));
            strategy.handle();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void testHandleEventQueueEmpty() {
        try {
            when(dao.loadEventQueue(anyString())).thenReturn(new ArrayList<>());
            strategy.handle();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void testHandleEventQueueNotEmpty() {
        try {
            ArrayList<Event> eventQueue = new ArrayList<>();
            eventQueue.add(new BaseEventImpl());
            when(dao.loadEventQueue(anyString())).thenReturn(eventQueue);

            Job upstreamJob = PowerMockito.mock(Job.class);
            PowerMockito.when(build.getParent()).thenReturn(upstreamJob);
            PowerMockito.when(upstreamJob.getFullName()).thenReturn("UpstreamJob");

            NwftDownstreamJob job = PowerMockito.mock(NwftDownstreamJob.class);
            PowerMockito.whenNew(NwftDownstreamJob.class).withArguments(anyString(),
                                                                        anyCollection(),
                                                                        any(Cause.class),
                                                                        anyInt(),
                                                                        anyLong()).thenReturn(job);
            PowerMockito.whenNew(NwftDownstreamJob.class).withArguments(anyString(),
                                                                        anyString(),
                                                                        anyCollection(),
                                                                        any(Cause.class),
                                                                        anyInt(),
                                                                        anyLong()).thenReturn(job);


            strategy.handle();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }
}
