package com.ericsson.becrux.base.common.buildsteptestbase;

import com.ericsson.becrux.base.common.core.NwftPostBuildStep;

import static org.junit.Assert.*;

/******************************************************************************
 * Base class for build steps unit tests. All classes under test must be
 * subclasses of {@link NwftPostBuildStep}.
 *****************************************************************************/
public abstract class NwftPostBuildStepTestBase extends BuildStepTestBase {
    private NwftPostBuildStep buildStep;

    /**************************************************************************
     * getBuildStep() must return an object of class under test.
     *
     * @return subclass of a build step
     *************************************************************************/
    public abstract NwftPostBuildStep getBuildStep();

    /**************************************************************************
     * Initializes the test suite, see BuildStepTestBase::init()
     *
     * init() should be called in a method with the annotation @Before.
     *************************************************************************/
    public void init() throws Exception {
        super.init();
        buildStep = getBuildStep();
    }

    /**************************************************************************
     * Perform a test case on class under test. Calls perform() and performs
     * assertions.
     *
     * @param expectedOutput a string that is expected in the logger result
     * @param expectedResult the expected return value by perform
     *************************************************************************/
    public void assertPerformInvocation(String expectedOutput, boolean expectedResult) {
        try {
            assertEquals(expectedResult, buildStep.perform(jenkinsMocker.getAbstractBuildMock(), launcherMock, buildListenerMock));
            assertTrue(getLoggerResult().contains(expectedOutput));
        }
        catch (Exception ie) {
            fail("testPerform failed with reason: " + ie.getMessage());
        }
    }

    public void assertPerformException(String expectedOutput) {
        assertPerformInvocation(expectedOutput, false);
    }

    public void assertPerformNoOutput(boolean expectedResult) {
        assertPerformInvocation("", expectedResult);
    }
}
