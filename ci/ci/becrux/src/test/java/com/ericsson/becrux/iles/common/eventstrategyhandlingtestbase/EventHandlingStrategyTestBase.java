package com.ericsson.becrux.iles.common.eventstrategyhandlingtestbase;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eventhandler.EventHandler;
import com.ericsson.becrux.base.common.eventhandler.EventHandlingResult;
import com.ericsson.becrux.base.common.eventhandler.EventValidationResult;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategy;
import com.ericsson.becrux.base.common.mockers.AbstractBuildMocker;
import com.ericsson.becrux.iles.common.mockers.GlobalConfigurationMocker;
import com.ericsson.becrux.base.common.mockers.JenkinsMocker;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Descriptor;
import javafx.util.Pair;
import org.powermock.api.mockito.PowerMockito;

import javax.annotation.Nonnull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by zguntom on 4/13/17.
 */
public abstract class EventHandlingStrategyTestBase {
    private JenkinsMocker jenkinsMocker;
    private Descriptor descriptorMock;
    private EventHandlingStrategy eventHandlingStrategy;
    private Event eventUnderTest;
    private PrintWriter writeToFile;
    private PrintStream eventHandlerLogger;
    private File logFile;

    protected AbstractBuildMocker abstractBuildMocker;
    protected GlobalConfigurationMocker globalConfigurationMocker;
    private File jarFileMock;
    private EventHandler eventHandlerMock;

    /**************************************************************************
     * getEventHandlingStrategy() must return a subclass instance of
     * {@link EventHandlingStrategy}. This is the class under test.
     *
     * @return a subclass instance of EventHandlingStrategy
     *************************************************************************/
    public abstract EventHandlingStrategy getEventHandlingStrategy();

    /**************************************************************************
     * Initializes the test suite - prepares all mocks, a logger and sets
     * up the event handling strategy under test.
     *
     * init() should be called in a method with the annotation @Before.
     *************************************************************************/
    public void init() throws Exception {
        logFile = new File("./eventHandlingTestLog.txt");
        writeToFile = new PrintWriter(logFile);
        eventHandlerLogger = new PrintStream(logFile);

        globalConfigurationMocker = GlobalConfigurationMocker.createMock();
        jarFileMock = PowerMockito.mock(File.class);
        globalConfigurationMocker.mockAll(jarFileMock);
        abstractBuildMocker = AbstractBuildMocker.createMock(new Pair<>("JOB_NAME", "CONTROLLER"));

        eventHandlerMock = PowerMockito.mock(EventHandler.class);
        PowerMockito.when(eventHandlerMock.getLog()).thenReturn(eventHandlerLogger);
        eventHandlingStrategy = getEventHandlingStrategy();
        eventHandlingStrategy.setHandler(eventHandlerMock);

        descriptorMock = PowerMockito.mock(Descriptor.class);
        jenkinsMocker = JenkinsMocker.createMock(descriptorMock);
        jenkinsMocker.mockNwftDownstreamJob();

        Cause.UpstreamCause causeMock = PowerMockito.mock(Cause.UpstreamCause.class);
        PowerMockito.mockStatic(Cause.UpstreamCause.class);
        PowerMockito.whenNew(Cause.UpstreamCause.class).withAnyArguments().thenReturn(causeMock);
    }

    /**************************************************************************
     * Destroys the test suite - tear down all logger related objects, prepares
     * for next test case.
     *
     * destroy() should be called in a method with the annotation @After.
     *************************************************************************/
    public void destroy() {
        writeToFile.close();
        eventHandlerLogger.close();
        logFile.delete();
    }

    /**************************************************************************
     * Sets the event that is handled by the class under test.
     *
     * @param event non-null subclass of {@link Event}
     *************************************************************************/
    public void setEvent(@Nonnull Event event) {
        this.eventUnderTest = event;
    }

    /**************************************************************************
     * Returns the event that is handled by the class under test.
     *************************************************************************/
    public Event getEvent() {
        return this.eventUnderTest;
    }

    /**************************************************************************
     * Get the mock of {@link AbstractBuild}, could be used for additional
     * mocking if needed.
     *************************************************************************/
    public AbstractBuild getAbstractBuild() {
        return abstractBuildMocker.getMock();
    }

    /**************************************************************************
     * Check if a certain string exists in the logger that belongs to the
     * class under test.
     * Use for additional assertions.
     *
     * @param output the expected output
     *************************************************************************/
    public void assertLogContains(String output) throws IOException {
        if (!logContains(output))
            fail("Log didn't contain expected output: " + output);
    }

    public void assertLogNotContains(String output) throws IOException {
        if (logContains(output))
            fail("Log contains non-expected output: " + output);
    }

    private boolean logContains(String output) throws IOException {
        List<String> logs = Files.readAllLines(logFile.toPath(), Charset.forName("ISO-8859-1"));
        for (String line : logs) {
            if (line.toLowerCase().contains(output.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**************************************************************************
     * Perform the main test - e.g. a call to EventHandlingStrategy::sub
     * ::handle().
     *
     * assertHandleInvocation() should do all assertions, expand if needed and
     * create more explanatory assert methods that calls this function.
     *************************************************************************/
    public void assertHandleSuccessful() {
        assertValidateInvocation(true);
        assertHandleInvocation(true);
    }

    public void assertHandleFailure() {
        assertValidateInvocation(true);
        assertHandleInvocation(false);
    }
    public void assertValidateInvocation(boolean validate) {
        EventValidationResult validationResult = eventHandlingStrategy.validateEvent(eventUnderTest);
        assertEquals(validate, validationResult.isSuccessful());
    }

    private void assertHandleInvocation(boolean expectedResult) {
        try {
            EventHandlingResult result = eventHandlingStrategy.handle(eventUnderTest);
            assertEquals(expectedResult, result.isSuccessful());
        }
        catch (Exception e) {
            fail("Test failed with reason: " + e.getLocalizedMessage() + " " + e.getMessage());
        }
    }
}
