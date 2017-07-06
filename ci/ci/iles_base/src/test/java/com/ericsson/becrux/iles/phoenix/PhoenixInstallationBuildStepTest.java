package com.ericsson.becrux.iles.phoenix;

import com.ericsson.becrux.base.common.buildsteptestbase.NwftBuildStepTestBase;
import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.data.Version;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.iles.common.mockers.MockerInitializer;
import com.ericsson.becrux.iles.common.mockers.BuildParametersMocker;
import com.ericsson.becrux.iles.configuration.IlesDirectory;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.iles.data.IlesComponentFactory;
import com.ericsson.becrux.iles.data.Pcscf;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Descriptor;
import hudson.model.Run;
import jenkins.model.Jenkins;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/******************************************************************************
 * Class under test: {@link PhoenixInstallationBuildStep}
 *****************************************************************************/
@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class,
        IlesGlobalConfig.class,
        AbstractBuild.class,
        PhoenixInstallationBuildStep.class,
        NwftParametersAction.class,
        BuildParametersExtractor.class,
        IlesDirectory.class,
        ViseChannelGlobalConfig.class,
        JenkinsGlobalConfig.class,
        PhoenixInstallationBuildStep.DescriptorImpl.class,
        Run.class,
        FilePath.class,
        Cause.UpstreamCause.class})
public class PhoenixInstallationBuildStepTest extends NwftBuildStepTestBase {
    @Override
    public Descriptor getDescriptor() {
        return PowerMockito.mock(PhoenixInstallationBuildStep.DescriptorImpl.class);
    }

    @Override
    public NwftBuildStep getBuildStep() {
        return new PhoenixInstallationBuildStep(false,
                "https://localhost:8888", "ova_name", "atlas_tip",
                "atlas_user","atlas_pw", "node_ip",
                "node_user", "node_password", "pdb_tools_pkg",
                "pdb_server_host", "pdb_server_port", "pdb_user",
                "pdb_password", "nr_of_samples", "vnf_path",
                "phoenixExecutionScript");
    }

    @Before
    public void setUp() throws Exception {
        init();
        MockerInitializer.initializeGlobalConfigurationMocker();
    }

    @After
    public void tearDown() throws Exception {
        destroy();
    }

    @Test
    public void testPhoenixInstallation() throws Exception {
        BuildParametersMocker.newMock().addComponentParameterValue("test", 1).
                                        addReservedViseChannelParameterValue("VISE0308", 1).finishMock();
        assertPerformNoOutput(true);
    }

    @Test
    public void testCustomVersionInstallation() throws Exception {
        Pcscf pcscf = (Pcscf)IlesComponentFactory.getInstance().create("Pcscf");
        pcscf.setVersion(Version.createCustomVersion("sw_R1A01"));
        pcscf.setPdb("-pdb \"whatever\" -rev \"R1A\"");
        BuildParametersMocker.newMock().addComponentParameterValue("testPcscf", pcscf, 1).
                                        addReservedViseChannelParameterValue("VISE0308", 1).finishMock();
        assertPerformNoOutput(true);
    }
}
