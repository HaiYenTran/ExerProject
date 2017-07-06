package com.ericsson.becrux.iles.testexec;

import com.ericsson.becrux.base.common.buildsteptestbase.NwftBuildStepTestBase;
import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.core.CommonParamenterValue;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.iles.common.mockers.MockerInitializer;
import com.ericsson.becrux.iles.common.mockers.BuildParametersMocker;
import com.ericsson.becrux.iles.configuration.IlesDirectory;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.iles.eventhandler.strategies.ITREventStrategy;
import com.ericsson.becrux.iles.testexec.impl.IlesTestExec;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/******************************************************************************
 * Class under test: {@link TestExecutionBuildStep}
 *****************************************************************************/
@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class,
        IlesGlobalConfig.class,
        AbstractBuild.class,
        TestExecutionBuildStep.class,
        NwftParametersAction.class,
        BuildParametersExtractor.class,
        IlesDirectory.class,
        FilePath.class,
        ViseChannelGlobalConfig.class,
        JenkinsGlobalConfig.class,
        TestExecutionBuildStep.DescriptorImpl.class,
        Cause.UpstreamCause.class})
public class TestExecutionBuildStepTest extends NwftBuildStepTestBase {
    private IlesTestExec ilesTestExec;

    @Override
    public Descriptor getDescriptor() {
        return PowerMockito.mock(TestExecutionBuildStep.DescriptorImpl.class);
    }

    @Override
    public NwftBuildStep getBuildStep() {
        return new TestExecutionBuildStep("gatewayServer", false, false, true);
    }

    @Before
    public void setUp() throws Exception {
        init();
        mockIlesTestExec();
        MockerInitializer.initializeGlobalConfigurationMocker();
    }

    @After
    public void tearDown() throws IOException {
        destroy();
    }

    private void mockIlesTestExec() throws Exception {
        ilesTestExec = mock(IlesTestExec.class);
        PowerMockito.whenNew(IlesTestExec.class).withArguments(any(IlesTestExecProperties.class)).thenReturn(ilesTestExec);
        when(ilesTestExec.validateProperties()).thenReturn(true);
    }

    /**********************************************************************
     * Test Group 1: INT Test suites
     * - 0 INT test suites given, expects exception on INT
     * - 1 INT test suite given, expects exception on VISE
     * - 2 INT test suites given, expects exception on INT
     *********************************************************************/
    @Test
    public void testINTMissing() throws Exception {
        BuildParametersMocker.newMock().addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformException("No INT test suite data in build parameters");
    }

    @Test
    public void testOneINTGiven() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 1).
                                        addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformException("No VISE value in build parameters");
    }

    @Test
    public void testTwoINTGiven() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 2).
                                        addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformException("More than one INT test suite data parameter provided");
    }

    /**********************************************************************
     * Test Group 2: VISE channel parameters
     * - 0 VISE channel given, expects exception on VISE (scenario tested
     *   above)
     * - 1 VISE channel given, expects successful build
     * - 2 VISE channel given, expects exception on VISE
     *********************************************************************/
    @Test
    public void testOneViseChannelGiven() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 1).
                                        addReservedViseChannelParameterValue("VISE0308", 1).
                                        addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformNoOutput(true);
    }

    @Test
    public void testTwoViseChannelGiven() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 1).
                                        addReservedViseChannelParameterValue("VISE0308", 2).
                                        addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformException("More than one VISE value provided");
    }

    /**********************************************************************
     * Test Group 3: LEO parameters
     * - Several LEO parameters provided, expects expection on LEO param
     * - One LEO parameter provided, expects successful build
     *********************************************************************/
    @Test
    public void testSeveralLeoParameterProvided() throws Exception{
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 1).
                                        addReservedViseChannelParameterValue("VISE0308", 1).
                                        addInitLeoParameterValue(2).
                                        addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformException("More than one Leo data parameter provided");
    }

    @Test
    public void testOneLeoParameterProvided() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("Int", 1).
                                        addReservedViseChannelParameterValue("VISE0308", 1).
                                        addInitLeoParameterValue(1).
                                        addCommonParamenterValue("CommonParameter", "value", 1).finishMock();
        assertPerformNoOutput(true);
    }
}
