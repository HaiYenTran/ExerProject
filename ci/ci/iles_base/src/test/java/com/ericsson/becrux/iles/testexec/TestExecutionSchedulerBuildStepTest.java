package com.ericsson.becrux.iles.testexec;

import com.ericsson.becrux.base.common.buildsteptestbase.NwftBuildStepTestBase;
import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.iles.common.mockers.BuildParametersMocker;
import com.ericsson.becrux.iles.configuration.IlesDirectory;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Descriptor;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import jenkins.model.Jenkins;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

/******************************************************************************
 * Class under test: {@link TestExecutionSchedulerBuildStep}
 *****************************************************************************/
@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class,
        IlesGlobalConfig.class,
        AbstractBuild.class,
        TestExecutionSchedulerBuildStep.class,
        NwftParametersAction.class,
        BuildParametersExtractor.class,
        IlesDirectory.class,
        FilePath.class,
        ViseChannelGlobalConfig.class,
        JenkinsGlobalConfig.class,
        TestExecutionSchedulerBuildStep.DescriptorImpl.class,
        Cause.UpstreamCause.class,
        TestResultAction.class,
        TestResult.class})
public class TestExecutionSchedulerBuildStepTest extends NwftBuildStepTestBase {
    @Override
    public NwftBuildStep getBuildStep() {
        return new TestExecutionSchedulerBuildStep("600", "60000", "Test");

    }

    @Override
    public Descriptor getDescriptor() {
        return PowerMockito.mock(TestExecutionSchedulerBuildStep.DescriptorImpl.class);
    }

    @Before
    public void setUp() throws Exception {
        init();
        jenkinsMocker.mockNwftDownstreamJob();
        Cause.UpstreamCause causeMock = PowerMockito.mock(Cause.UpstreamCause.class);
        PowerMockito.mockStatic(Cause.UpstreamCause.class);
        PowerMockito.whenNew(Cause.UpstreamCause.class).withAnyArguments().thenReturn(causeMock);
        //
        TestResultAction action = PowerMockito.mock(TestResultAction.class);
        AbstractBuild abstractBuild = PowerMockito.mock(AbstractBuild.class);
        TestResult result = PowerMockito.mock(TestResult.class);
        PowerMockito.when(action.getResult()).thenReturn(result);
        PowerMockito.when(abstractBuild.getAction(TestResultAction.class)).thenReturn(action);


    }

    @After
    public void tearDown() throws IOException {
        destroy();
    }

    @Test
    public void testNoINTParameters() throws Exception {
        BuildParametersMocker.newMock().addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformException("No INT test suite data in build parameters");
    }

    @Test
    public void testWithNoViseChannelParameter() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 1).
                addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformException("No VISE value in build parameters");
    }

    @Test
    public void testWithMoreThanOneINTParameters() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 2).
                addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformException("More than one INT test suite data parameter provided");
    }

    @Test
    public void testWithViseChannelGiven() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 1).
                addReservedViseChannelParameterValue("VISE0308", 1).
                addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformNoOutput(true);
    }

    @Test
    public void testWithMoreThanOneViseChannels() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 1).
                addReservedViseChannelParameterValue("VISE0308", 2).
                addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformException("More than one VISE value provided");
    }

    @Test
    public void testWithSeveralLeoParameters() throws Exception{
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 1).
                addReservedViseChannelParameterValue("VISE0308", 1).
                addInitLeoParameterValue(2).
                addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformException("More than one Leo data parameter provided");
    }

    @Test
    public void testWithOneLeoParameter() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 1).
                addReservedViseChannelParameterValue("VISE0308", 1).
                addInitLeoParameterValue(1).
                addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformNoOutput(true);
    }
}
