package com.ericsson.becrux.base.common.eiffel.buildstep;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventImpl;
import com.ericsson.becrux.base.common.eiffel.events.impl.TestInheritEventFactory;
import hudson.Launcher;
import hudson.model.BuildListener;
import org.powermock.api.mockito.PowerMockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;

import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by znikvik on 5/16/17.
 */
public class BuildStepTestBase {

    private PrintStream logger;
    private PrintWriter writeToFile;
    private File logFile;

    protected Launcher launcherMock;
    protected BuildListener buildListenerMock;

    public void init() throws Exception {
        launcherMock = PowerMockito.mock(Launcher.class);
        setupLogger();
    }

    public void destroy() throws Exception {
        writeToFile.close();
        logger.close();
        logFile.delete();
    }

    private void setupLogger() {
        try {
            logFile = new File("./buildStepTestLog.txt");
            writeToFile = new PrintWriter(logFile);
            logger = new PrintStream(logFile);
            buildListenerMock = PowerMockito.mock(BuildListener.class);
            when(buildListenerMock.getLogger()).thenReturn(logger);
        }
        catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            fail();
        }
    }

    public Event createEffeilEvent() throws Exception {
        TestInheritEventFactory eventFactory = new TestInheritEventFactory();
        Event event = eventFactory.createEvent(BaseEventImpl.class.getSimpleName());
        return event;
    }

}
