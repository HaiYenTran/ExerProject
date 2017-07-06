package com.ericsson.becrux.iles.provisioning;

import com.ericsson.becrux.base.common.buildsteptestbase.NwftBuildStepTestBase;
import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.loop.JobsScheduler;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.base.common.utils.ExecCommandHelper;
import com.ericsson.becrux.iles.common.mockers.BuildParametersMocker;
import com.ericsson.becrux.iles.common.mockers.MockerInitializer;
import com.ericsson.becrux.iles.configuration.IlesDirectory;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import hudson.FilePath;
import hudson.model.*;
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
 * Class under test: {@link ProvisioningSchedulerBuildStep}
 *****************************************************************************/
@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class,
        IlesGlobalConfig.class,
        AbstractBuild.class,
        ProvisioningSchedulerBuildStep.class,
        NwftParametersAction.class,
        BuildParametersExtractor.class,
        IlesDirectory.class,
        ViseChannelGlobalConfig.class,
        JenkinsGlobalConfig.class,
        ProvisioningSchedulerBuildStep.DescriptorImpl.class,
        Run.class,
        FilePath.class,
        ExecCommandHelper.class,
        Cause.UpstreamCause.class})
public class ProvisioningSchedulerBuildStepTest extends NwftBuildStepTestBase {
    private JobsScheduler jobsScheduler;

    @Override
    public Descriptor getDescriptor() {
        return PowerMockito.mock(ProvisioningSchedulerBuildStep.DescriptorImpl.class);
    }

    @Override
    public NwftBuildStep getBuildStep() {
        return new ProvisioningSchedulerBuildStep("1", "1", "PROVISIONING");
    }

    @Before
    public void setUp() throws Exception {
        init();

        jenkinsMocker.mockNwftDownstreamJob();

        jobsScheduler = PowerMockito.mock(JobsScheduler.class);
        PowerMockito.whenNew(JobsScheduler.class).withAnyArguments().thenReturn(jobsScheduler);
        PowerMockito.when(jobsScheduler.run()).thenReturn(Result.SUCCESS);

        Cause.UpstreamCause causeMock = PowerMockito.mock(Cause.UpstreamCause.class);
        PowerMockito.mockStatic(Cause.UpstreamCause.class);
        PowerMockito.whenNew(Cause.UpstreamCause.class).withAnyArguments().thenReturn(causeMock);

        PowerMockito.mockStatic(ExecCommandHelper.class);
        PowerMockito.when(ExecCommandHelper.readSymbolicLink(null)).thenReturn("/path");

        MockerInitializer.initializeGlobalConfigurationMocker();
    }

    @After
    public void tearDown() throws IOException {
        destroy();
    }

    @Test
    public void testProvisioningSkippedByUser() throws Exception {
        BuildParametersMocker.newMock().addCommonParamenterValue("PROVISIONING_ABLE", "false", 1).finishMock();

        assertPerformNoOutput(true);
    }

    @Test
    public void testProvisioningEnabledNoViseChannel() throws Exception {
        BuildParametersMocker.newMock().addCommonParamenterValue("PROVISIONING_ABLE", "true", 1).finishMock();
        assertPerformException("No VISE value in build parameters");
    }

    @Test
    public void testProvisioningEnabledSeveralViseChannels() throws Exception {
        BuildParametersMocker.newMock().addCommonParamenterValue("PROVISIONING_ABLE", "true", 1).
                                        addReservedViseChannelParameterValue("VISE0308", 2).finishMock();
        assertPerformException("More than one VISE value provided");
    }

    @Test
    public void testProvisioningEnabledNoLeoParameters() throws Exception {
        BuildParametersMocker.newMock().addCommonParamenterValue("PROVISIONING_ABLE", "true", 1).
                                        addReservedViseChannelParameterValue("VISE0308", 1).finishMock();
        assertPerformInvocation("No Leo data in build parameters", true);
    }

    @Test
    public void testProvisioningEnabledSeveralLeoParameters() throws Exception {
        BuildParametersMocker.newMock().addCommonParamenterValue("PROVISIONING_ABLE", "true", 1).
                                        addInitLeoParameterValue(2).
                                        addReservedViseChannelParameterValue("VISE0308", 1).finishMock();
        assertPerformException("More than one Leo data parameter provided");
    }

    @Test
    public void testProvisioningEnabledSuccessfulRun() throws Exception {
        BuildParametersMocker.newMock().addCommonParamenterValue("PROVISIONING_ABLE", "true", 1).
                                        addInitLeoParameterValue(1).
                                        addReservedViseChannelParameterValue("VISE0308", 1).finishMock();
        assertPerformNoOutput(true);
    }
}
