package com.ericsson.becrux.base.common.buildsteptestbase;

import com.ericsson.becrux.base.common.mockers.JenkinsMocker;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import org.powermock.api.mockito.PowerMockito;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by zguntom on 4/12/17.
 */
public abstract class BuildStepTestBase {
    private PrintStream logger;
    private PrintWriter writeToFile;
    private File logFile;

    protected Launcher launcherMock;
    protected BuildListener buildListenerMock;

    protected JenkinsMocker jenkinsMocker;

    private Descriptor descriptor;

    /**************************************************************************
     * getDescriptor() must return the descriptor of class under test.
     *
     * @return build step descriptor
     *************************************************************************/
    public abstract Descriptor getDescriptor();

    /**************************************************************************
     * Initializes the test suite - prepares all mocks, a logger for
     * {@link BuildListener} and sets up the build step class under test.
     *
     * init() should be called in a method with the annotation @Before.
     *************************************************************************/
    public void init() throws Exception {
        setupLogger();
        launcherMock = PowerMockito.mock(Launcher.class);

        descriptor = getDescriptor();
        jenkinsMocker = JenkinsMocker.createMock(descriptor);

    }

    /**************************************************************************
     * Destroys the test suite - tear down all logger related objects, prepares
     * for next test case.
     *
     * destroy() should be called in a method with the annotation @After.
     *************************************************************************/
    public void destroy() throws IOException {
        writeToFile.close();
        logger.close();
        logFile.delete();
    }

    /**************************************************************************
     * Returns the result of all logged items in BuildListener as a string.
     *
     * Use this for assertions.
     *
     * @return content of the build listener logger, each line separated by
     *         newline.
     *************************************************************************/
    public String getLoggerResult() throws IOException {
        List<String> logs = Files.readAllLines(logFile.toPath(), Charset.forName("ISO-8859-1"));
        String allLines = "";
        for (String line : logs) {
            allLines += line;
            allLines += "\n";
        }
        return allLines;
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
}
