package com.ericsson.becrux.iles.loop;

import com.ericsson.becrux.base.common.buildsteptestbase.NwftPostBuildStepTestBase;
import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.core.NwftPostBuildStep;
import com.ericsson.becrux.base.common.loop.JobsScheduler;
import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.iles.common.mockers.BuildParametersMocker;
import com.ericsson.becrux.iles.common.mockers.MockerInitializer;
import com.ericsson.becrux.iles.configuration.IlesDirectory;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import com.ericsson.becrux.iles.eventhandler.strategies.ITREventStrategy;
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

import java.util.HashMap;
import java.util.Map;

/******************************************************************************
 * Class under test: {@link LoopFeedbacksUpdaterPostBuildStep}
 *****************************************************************************/
@PrepareForTest({Jenkins.class,
        IlesGlobalConfig.class,
        AbstractBuild.class,
        LoopFeedbacksUpdaterPostBuildStep.class,
        NwftParametersAction.class,
        BuildParametersExtractor.class,
        IlesDirectory.class,
        ViseChannelGlobalConfig.class,
        JenkinsGlobalConfig.class,
        LoopFeedbacksUpdaterPostBuildStep.DescriptorImpl.class,
        Run.class,
        FilePath.class,
        Cause.UpstreamCause.class})
@RunWith(PowerMockRunner.class)
public class LoopFeedbacksUpdaterPostBuildStepTest extends NwftPostBuildStepTestBase {
    private JobsScheduler jobsScheduler;
    @Override
    public Descriptor getDescriptor() {
        return PowerMockito.mock(LoopFeedbacksUpdaterPostBuildStep.DescriptorImpl.class);
    }

    @Override
    public NwftPostBuildStep getBuildStep() {
        return new LoopFeedbacksUpdaterPostBuildStep("SenderJob");
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
        jenkinsMocker.mockJenkinsJobs("SenderJob");

        MockerInitializer.initializeGlobalConfigurationMocker();
    }

    public String getITRJobRequester() {
        return "ITR_sender";
    }
    @After
    public void tearDown() throws Exception {
        destroy();
    }

    @Test
    public void testTwoBTFEventGiven() throws Exception {
        BuildParametersMocker.newMock().addEiffelEventParameterValue(new BTFEvent(),2).
                                        addReservedViseChannelParameterValue("VISE0308", 1).
                                        finishMock();
        assertPerformException("Error: more than one BTF event parameter value found");
    }

    @Test
    public void testOneBTFEventGiven() throws Exception {
        BuildParametersMocker.newMock().addEiffelEventParameterValue(new BTFEvent(), 1).
                                        addEventParamenterValue(createITREvent(), 1).
                                        addComponentParameterValue("test", 2).
                                        addReservedViseChannelParameterValue("VISE0308", 1).
                                        finishMock();
        assertPerformNoOutput(true);
    }

    @Test
    public void testNoBTFEventGiven() throws Exception {
        BuildParametersMocker.newMock().addEventParamenterValue(createITREvent(), 1).
                                        addReservedViseChannelParameterValue("VISE0308", 1).
                                        addComponentParameterValue("test", 2).
                                        finishMock();
        assertPerformNoOutput(true);

    }

    @Test
    public void testBTFEventGivenWithResultError() throws Exception {
        BuildParametersMocker.newMock().addEventParamenterValue(createITREvent(), 1).
                                        addResultParameterValue(PhaseStatus.ERROR, 1).
                                        addComponentParameterValue("test", 2).
                                        addReservedViseChannelParameterValue("VISE0308", 1).
                                        finishMock();
        assertPerformNoOutput(true);
    }

    @Test
    public void testBTFEventGivenWithResultFailure() throws Exception {
        BuildParametersMocker.newMock().addEventParamenterValue(createITREvent(), 1).
                                        addResultParameterValue(PhaseStatus.FAILURE, 1).
                                        addComponentParameterValue("test", 2).
                                        addReservedViseChannelParameterValue("VISE0308", 1).
                                        finishMock();
        assertPerformNoOutput(true);
    }
    /**************************************************************************
     * Simulate the ITR came
     *
     *************************************************************************/
    public ITREvent createITREvent() {
        ITREvent event = new ITREvent();
        Map<String, String> a = new HashMap<>();
        a.put(ITREventStrategy.EVENT_TAG, "tagTest");
        event.setParameters(a);
        a.put(ITREventStrategy.ITR_SENDER_JOB_NAME, "ITR_Sender_Test");
        a.put(ITREventStrategy.ITR_SENDER_JOB_BUILD, "100");
        a.put(ITREventStrategy.CLIENT_JENKINS, "https://fem023-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins");
        return event;
    }

}
