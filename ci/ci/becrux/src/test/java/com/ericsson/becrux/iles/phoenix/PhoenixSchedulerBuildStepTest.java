package com.ericsson.becrux.iles.phoenix;

import com.ericsson.becrux.base.common.buildsteptestbase.NwftBuildStepTestBase;
import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.base.common.utils.ExecCommandHelper;
import com.ericsson.becrux.iles.common.mockers.BuildParametersMocker;
import com.ericsson.becrux.iles.common.mockers.MockerInitializer;
import com.ericsson.becrux.iles.configuration.IlesDirectory;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.tasks.junit.TestResult;
import jenkins.model.Jenkins;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/******************************************************************************
 * Class under test: {@link PhoenixSchedulerBuildStep}
 *****************************************************************************/
@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class,
        IlesGlobalConfig.class,
        AbstractBuild.class,
        PhoenixSchedulerBuildStep.class,
        NwftParametersAction.class,
        BuildParametersExtractor.class,
        IlesDirectory.class,
        ViseChannelGlobalConfig.class,
        JenkinsGlobalConfig.class,
        PhoenixSchedulerBuildStep.DescriptorImpl.class,
        Run.class,
        FilePath.class,
        TestResult.class,
        ExecCommandHelper.class,
        Cause.UpstreamCause.class})

public class PhoenixSchedulerBuildStepTest extends NwftBuildStepTestBase {

    @Override
    public Descriptor getDescriptor() {
        return PowerMockito.mock(PhoenixSchedulerBuildStep.DescriptorImpl.class);
    }

    @Override
    public NwftBuildStep getBuildStep() {
        return new PhoenixSchedulerBuildStep("600", "6000", "Test");
    }

    @Before
    public void setUp() throws Exception {
        init();
        Cause.UpstreamCause causeMock = PowerMockito.mock(Cause.UpstreamCause.class);
        PowerMockito.mockStatic(Cause.UpstreamCause.class);
        PowerMockito.whenNew(Cause.UpstreamCause.class).withAnyArguments().thenReturn(causeMock);
        jenkinsMocker.mockNwftDownstreamJob();

        PowerMockito.mockStatic(ExecCommandHelper.class);
        PowerMockito.when(ExecCommandHelper.readSymbolicLink(null)).thenReturn("/path");

        MockerInitializer.initializeGlobalConfigurationMocker();
    }

    @After
    public void tearDown() throws Exception {
        destroy();
    }

    @Test
    public void testPhoenixSchedulerBuildStep() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("test", 1).
                addReservedViseChannelParameterValue("VISE0308", 1).addInitLeoParameterValue(1).finishMock();
        assertPerformNoOutput(true);
    }

    @Test
    public void testTestPhoenixSchedulingWithNoViseChannel() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("test", 1).addInitLeoParameterValue(1).finishMock();
        assertPerformException("No VISE value in build parameters");
    }

    @Test
    public void testPhoenixSchedulingWithNoBuildParameters() throws Exception {
        BuildParametersMocker.newMock();
        assertPerformException("Nodes are not provided in build parameters.");
    }

     @Test
    public void testTestPhoenixSchedulingWithSeveralViseChannel() throws Exception {
         BuildParametersMocker.newMock().addComponentParameterValue("test", 1).
                 addReservedViseChannelParameterValue("VISE0308", 2).addInitLeoParameterValue(1).finishMock();
        assertPerformException("More than one VISE value provided");
    }

    @Test
    public void testTestPhoenixSchedulingWithNoLeoParameters() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("test", 1).
                addReservedViseChannelParameterValue("VISE0308", 1).finishMock();
        assertPerformInvocation("No Leo data in build parameters", true);
    }

    @Test
    public void testTestPhoenixSchedulingWithSeveralLeoParameters() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("test", 1).
                addInitLeoParameterValue(2).
                addReservedViseChannelParameterValue("VISE0308", 1).finishMock();
        assertPerformException("More than one Leo data parameter provided");
    }

}
