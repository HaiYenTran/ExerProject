package com.ericsson.becrux.base.common.eiffel.buildstep;

import com.ericsson.becrux.base.common.buildsteptestbase.NwftBuildStepTestBase;
import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.eiffel.EiffelEventReceiver;
import com.ericsson.becrux.base.common.eiffel.EiffelEventReceiverBuildStep;
import com.ericsson.becrux.base.common.eiffel.configuration.SecondaryBinding;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventImpl;
import com.ericsson.becrux.base.common.eiffel.events.impl.TestInheritEventFactory;
import com.ericsson.becrux.base.common.mockers.AbstractBuildMocker;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

/******************************************************************************
 * Class under test: {@link EiffelEventReceiverBuildStep}
 *****************************************************************************/
@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class,
        AbstractBuild.class,
        NwftParametersAction.class,
        BuildParametersExtractor.class,
        FilePath.class,
        JenkinsGlobalConfig.class,
        EiffelEventReceiver.class,
        EiffelEventReceiverBuildStep.class,
})
public class EiffelEventReceiverBuildStepTest extends NwftBuildStepTestBase{

    @Before
    public void setUp() throws Exception {
        List<SecondaryBinding> secondaryBindings = new LinkedList<SecondaryBinding>();
        secondaryBindings.add(new SecondaryBinding("experimental.generic.iles.eiffel021.seki.fem004", "MTAS"));

        JenkinsGlobalConfig jenkinsGlobalConfig = PowerMockito.mock(JenkinsGlobalConfig.class);
        PowerMockito.whenNew(JenkinsGlobalConfig.class).withNoArguments().thenReturn(jenkinsGlobalConfig);
        when(jenkinsGlobalConfig.getSecondaryBindings()).thenReturn(secondaryBindings);

        init();

        EiffelEventReceiver receiver = PowerMockito.mock(EiffelEventReceiver.class);
        PowerMockito.whenNew(EiffelEventReceiver.class).withAnyArguments().thenReturn(receiver);
        Queue<Event> eventQueue = new PriorityQueue<>();
        eventQueue.add(createEffeilEvent());
        when(receiver.getEventQueue()).thenReturn(eventQueue);
    }

    @After
    public void tearDown() throws Exception {
        destroy();
    }

    @Test
    public void testPerformWithNullTag() throws  Exception{
        EiffelEventReceiverBuildStep buildStep = new TestImplEiffelEventReceiverBuildStep(null, "experimental.generic.iles.eiffel021.seki.fem004", "6000",true);
        assertEquals(true, buildStep.perform(AbstractBuildMocker.createMock().getMock(), launcherMock, buildListenerMock));
    }

    @Test
    public void testPerformWithTag() throws  Exception{
        assertPerformNoOutput(true);
    }

    @Test
    public void testPerformWithNoCustomBindings() throws  Exception{
        EiffelEventReceiverBuildStep buildStep = new TestImplEiffelEventReceiverBuildStep("Test", "experimental.generic.iles.eiffel021.seki.fem004", "6000",false);
        assertEquals(true, buildStep.perform(AbstractBuildMocker.createMock().getMock(), launcherMock, buildListenerMock));
    }

    @Test
    public void testPerformFailure() throws  Exception{
        Thread.currentThread().interrupt();
        assertPerformException("");

    }

    private Event createEffeilEvent() throws Exception {
        TestInheritEventFactory eventFactory = new TestInheritEventFactory();
        Event event = eventFactory.createEvent(BaseEventImpl.class.getSimpleName());
        return event;
    }

    @Override
    public NwftBuildStep getBuildStep() {
        return new TestImplEiffelEventReceiverBuildStep("Test", "experimental.generic.iles.eiffel021.seki.fem004", "6000",true);
    }

    @Override
    public Descriptor getDescriptor() {
        return null;
    }
}
