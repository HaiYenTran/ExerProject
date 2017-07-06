package com.ericsson.becrux.base.common.eiffel.buildstep;

import com.ericsson.becrux.base.common.buildsteptestbase.NwftBuildStepTestBase;
import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.eiffel.EiffelEventSender;
import com.ericsson.becrux.base.common.eiffel.EiffelEventSenderBuildStep;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventImpl;
import com.ericsson.becrux.base.common.eiffel.events.impl.TestInheritEventFactory;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.base.common.mockers.BuildParametersMocker;
import com.ericsson.becrux.base.common.mockers.AbstractBuildMocker;
import com.ericsson.duraci.eiffelmessage.sending.exceptions.EiffelMessageSenderException;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

/******************************************************************************
 * Class under test: {@link EiffelEventSenderBuildStep}
 *****************************************************************************/
@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class,
        AbstractBuild.class,
        NwftParametersAction.class,
        BuildParametersExtractor.class,
        FilePath.class,
        JenkinsGlobalConfig.class,
        EiffelEventSender.class,
        EiffelEventSenderBuildStep.class,
        })
public class EiffelEventSenderBuildStepTest extends NwftBuildStepTestBase{

    EiffelEventSender sender;
    TestInheritEventFactory eventFactory = new TestInheritEventFactory();

    @Before
    public void setUp() throws Exception {
        init();
        sender = PowerMockito.mock(EiffelEventSender.class);
        PowerMockito.whenNew(EiffelEventSender.class).withNoArguments().thenReturn(sender);
    }

    @After
    public void tearDown() throws Exception {
       destroy();
    }

    @Test
    public void testPerform() throws Exception{

        Event event = eventFactory.createEvent(BaseEventImpl.class.getSimpleName());
        BuildParametersMocker.newMock().addEiffelEventParameterValue(event, 1).finishMock();
        assertPerformNoOutput(true);
    }

    @Test
    public void testPerformWithCustomTag() throws Exception{
        EiffelEventSenderBuildStep buildStep = new EiffelEventSenderBuildStep("Test", true);
        Event event = eventFactory.createEvent(BaseEventImpl.class.getSimpleName());
        BuildParametersMocker.newMock().addEiffelEventParameterValue(event, 1).finishMock();
        assertEquals(true, buildStep.perform(AbstractBuildMocker.createMock().getMock(), launcherMock, buildListenerMock));
    }

    @Test
    public void testPerformException() throws Exception{
        Event event = eventFactory.createEvent(BaseEventImpl.class.getSimpleName());
        BuildParametersMocker.newMock().addEiffelEventParameterValue(event, 1).finishMock();
        PowerMockito.doThrow(new EiffelMessageSenderException("")).when(sender).sendEvent(event,(String) null);
        assertPerformException("");
    }

    @Override
    public NwftBuildStep getBuildStep() {
        return new EiffelEventSenderBuildStep("Test", false);
    }

    @Override
    public Descriptor getDescriptor() {
        return null;
    }
}
