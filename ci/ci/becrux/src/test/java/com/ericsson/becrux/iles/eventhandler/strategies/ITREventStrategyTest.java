package com.ericsson.becrux.iles.eventhandler.strategies;

import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategy;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.vise.VisePool;
import com.ericsson.becrux.iles.common.eventstrategyhandlingtestbase.EventHandlingStrategyTestBase;
import com.ericsson.becrux.iles.configuration.IlesDirectory;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Run;
import jenkins.model.Jenkins;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

/******************************************************************************
 * Class under test: {@link ITREventStrategy}
 *****************************************************************************/
@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class,
        IlesGlobalConfig.class,
        AbstractBuild.class,
        NwftParametersAction.class,
        BuildParametersExtractor.class,
        IlesDirectory.class,
        ViseChannelGlobalConfig.class,
        JenkinsGlobalConfig.class,
        Run.class,
        FilePath.class,
        ITREventStrategy.class,
        Cause.UpstreamCause.class})
public class ITREventStrategyTest extends EventHandlingStrategyTestBase {
    @Override
    public EventHandlingStrategy getEventHandlingStrategy() {
        return new ITREventStrategy("WORKER", "pool300", getAbstractBuild());
    }

    @Before
    public void setUp() throws Exception {
        super.init();

        globalConfigurationMocker.mockLoadNewestComponent(Component.State.BASELINE_APPROVED);
        globalConfigurationMocker.mockLoadAllComponents();
        globalConfigurationMocker.mockloadOneNewestComponent("MTAS");
        globalConfigurationMocker.mockLoadViseChannel(new ViseChannel("VISE0301"));
        globalConfigurationMocker.mockLoadVisePool(new VisePool("pool300"), new ViseChannel("VISE0301"));
    }

    @After
    public void tearDown() {
        super.destroy();
    }

    private void setITREvent(String product, String version, String loopType, Map<String, String> params) {
        ITREvent itr = new ITREvent();
        itr.setProduct(product);

        itr.setBaseline(version);

        itr.setLoopType(loopType.toLowerCase() == "test" ?
                        ITREvent.LoopType.TEST : loopType.toLowerCase() == "baseline" ?
                                                 ITREvent.LoopType.BASELINE : null);
        itr.setParameters(params);
        setEvent(itr);
    }

    @Test
    public void testHandleWithLoopSuccess() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("INT_CONFIG", "{installable:false, version:3.0, config_properties:/tmp/config.properties}");
        params.put(ITREventStrategy.ITR_SENDER_JOB_NAME, "ITR_Sender_Test");
        params.put(ITREventStrategy.ITR_SENDER_JOB_BUILD, "100");
        params.put(ITREventStrategy.CLIENT_JENKINS, "https://fem036-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/");
        setITREvent("MTAS", "R1A01", "test", params);
        assertHandleSuccessful();
        assertLogContains("List of Components will be used for testing");
        assertLogContains("MTAS");
        assertLogContains("R1A01");
    }

    @Test
    public void testHandleWithLoopSpecificViseSuccess() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ITREventStrategy.INSTALLATION_ABLE, "true");
        params.put(ITREventStrategy.PROVISIONING_ABLE, "false");
        params.put(ITREventStrategy.TESTEXEC_ABLE, "true");
        params.put("PCSCF_CONFIG", "{installable:true, version:R1A01}");
        params.put(ITREventStrategy.VISE_INFO, "{ name:299 , ip:1.1.1.0 }");
        params.put("INT_CONFIG", "{installable:false, version:3.0, config_properties:/tmp/config.properties}");
        params.put(ITREventStrategy.ITR_SENDER_JOB_NAME, "ITR_Sender_Test");
        params.put(ITREventStrategy.ITR_SENDER_JOB_BUILD, "101");
        params.put(ITREventStrategy.CLIENT_JENKINS, "https://fem036-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/");
        setITREvent("UNKNOWN", "UNKNOWN", "test", params);

        assertHandleSuccessful();
        assertLogContains("List of Components will be used for testing");
        assertLogNotContains("UNKNOWN");
        assertLogContains("Pcscf");
        assertLogContains("\"version\":\"R1A01\"");
        assertLogContains("\"installable\":true");
        assertLogContains("VISE0299");
    }

    @Test
    public void testHandleIntWithLoopSuccess() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ITREventStrategy.INSTALLATION_ABLE, "false");
        params.put(ITREventStrategy.PROVISIONING_ABLE, "false");
        params.put(ITREventStrategy.TESTEXEC_ABLE, "true");
        params.put("INT_CONFIG", "{installable:false, version:3.0, config_properties:/tmp/config.properties}");
        params.put(ITREventStrategy.ITR_SENDER_JOB_NAME, "ITR_Sender_Test");
        params.put(ITREventStrategy.ITR_SENDER_JOB_BUILD, "102");
        params.put(ITREventStrategy.CLIENT_JENKINS, "https://fem036-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/");
        setITREvent("UNKNOWN", "UNKNOWN", "test", params);

        assertHandleSuccessful();
        assertLogContains("List of Components will be used for testing");
        assertLogContains("INT");
        assertLogContains("\"installable\":false");
        assertLogContains("\"version\":\"3.0\"");
    }

    @Test
    public void testInvalidateNewBaseline() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("INT_CONFIG", "{installable:false, version:3.0, config_properties:/tmp/config.properties}");
        params.put(ITREventStrategy.ITR_SENDER_JOB_NAME, "ITR_Sender_Test");
        params.put(ITREventStrategy.ITR_SENDER_JOB_BUILD, "103");
        params.put(ITREventStrategy.CLIENT_JENKINS, "https://fem036-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/");
        setITREvent("MTAS", "R1A01", "baseline", params);

        assertValidateInvocation(false);
    }

    @Test
    public void testHandleNewBaseline() throws Exception {

        Map<String, String> params = new HashMap<>();
        params.put("INT_CONFIG", "{installable:false, version:R1A01, config_properties:/tmp/config.properties}");
        params.put(ITREventStrategy.ITR_SENDER_JOB_NAME, "ITR_Sender_Test");
        params.put(ITREventStrategy.ITR_SENDER_JOB_BUILD, "104");
        params.put(ITREventStrategy.CLIENT_JENKINS, "https://fem036-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/");
        setITREvent("MTAS", "vMTAS_1_2_LSV1", "baseline", params);

        assertHandleSuccessful();
        assertLogContains("List of Components will be used for testing");
        assertLogContains("MTAS");
        assertLogContains("\"installable\":true");
        assertLogContains("\"version\":\"vMTAS_1_2_LSV1\"");
    }


    @Test
    public void testSvnfRequestOneNode() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ITREventStrategy.PROVISIONING_ABLE, "false");
        params.put(ITREventStrategy.TESTEXEC_ABLE, "false");
        params.put("PCSCF_CONFIG", "{installable:true, version:R1A01}");
        params.put("VISE_INFO", "{name:203, ip:10.50.194.132}");
        params.put("INT_CONFIG", "{installable:false, version:3.0, config_properties:/tmp/config.properties}");
        params.put(ITREventStrategy.ITR_SENDER_JOB_NAME, "ITR_Sender_Test");
        params.put(ITREventStrategy.ITR_SENDER_JOB_BUILD, "105");
        params.put(ITREventStrategy.CLIENT_JENKINS, "https://fem036-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/");
        setITREvent("UNKNOWN", "UNKNOWN", "test", params);

        assertHandleSuccessful();
        assertLogContains("List of Components will be used for testing");
        assertLogContains("PCSCF");
        assertLogContains("R1A01");
    }

    @Test
    public void testSvnfRequestTwoNode() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ITREventStrategy.PROVISIONING_ABLE, "false");
        params.put(ITREventStrategy.TESTEXEC_ABLE, "false");
        params.put("PCSCF_CONFIG", "{installable:true, version:R1A01}");
        params.put("IBCF_CONFIG", "{installable:true, version:R1A01}");
        params.put("VISE_INFO", "{name:203, ip:10.50.194.132}");
        params.put("INT_CONFIG", "{installable:false, version:3.0, config_properties:/tmp/config.properties}");
        params.put(ITREventStrategy.ITR_SENDER_JOB_NAME, "ITR_Sender_Test");
        params.put(ITREventStrategy.ITR_SENDER_JOB_BUILD, "106");
        params.put(ITREventStrategy.CLIENT_JENKINS, "https://fem036-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/");
        setITREvent("UNKNOWN", "UNKNOWN", "test", params);

        assertHandleSuccessful();
        assertLogContains("List of Components will be used for testing");
        assertLogContains("PCSCF");
        assertLogContains("IBCF");
        assertLogContains("R1A01");
    }

    @Test
    public void testVersionType() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ITREventStrategy.PROVISIONING_ABLE, "false");
        params.put(ITREventStrategy.TESTEXEC_ABLE, "false");
        params.put("PCSCF_CONFIG", "{installable:true, version:sw_R1A01, version-type:custom}");
        params.put("VISE_INFO", "{name:203, ip:10.50.194.132}");
        params.put("INT_CONFIG", "{installable:false, version:3.0, config_properties:/tmp/config.properties}");
        params.put(ITREventStrategy.ITR_SENDER_JOB_NAME, "ITR_Sender_Test");
        params.put(ITREventStrategy.ITR_SENDER_JOB_BUILD, "107");
        params.put(ITREventStrategy.CLIENT_JENKINS, "https://fem036-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/");
        setITREvent("UNKNOWN", "UNKNOWN", "test", params);

        assertHandleSuccessful();
        assertLogContains("sw_R1A01");
    }

    @Test
    public void testJustProvisioning() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ITREventStrategy.TESTEXEC_ABLE, "false");
        params.put(ITREventStrategy.PROVISIONING_ABLE, "true");
        params.put(ITREventStrategy.INSTALLATION_ABLE, "false");
        params.put("VISE_INFO", "{name:201, ip:10.50.194.130}");
        params.put("INT_CONFIG", "{installable:false, version:3.0, config_properties:/tmp/config.properties}");
        params.put(ITREventStrategy.ITR_SENDER_JOB_NAME, "ITR_Sender_Test");
        params.put(ITREventStrategy.ITR_SENDER_JOB_BUILD, "108");
        params.put(ITREventStrategy.CLIENT_JENKINS, "https://fem036-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/");
        setITREvent("UNKNOWN", "UNKNOWN", "test", params);

        assertHandleSuccessful();
    }
}
