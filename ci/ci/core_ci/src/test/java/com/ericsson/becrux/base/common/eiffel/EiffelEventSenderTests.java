package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.eiffel.EiffelEventConverter;
import com.ericsson.becrux.base.common.eiffel.EiffelEventSender;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventImpl;
import com.ericsson.becrux.base.common.eiffel.events.impl.TestDummyEvent;
import com.ericsson.becrux.base.common.eiffel.events.impl.TestInheritEventFactory;
import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import com.ericsson.duraci.eiffelmessage.mmparser.clitool.EiffelConfig;
import com.ericsson.duraci.eiffelmessage.sending.MessageSendWrapper;
import com.ericsson.duraci.eiffelmessage.sending.MessageSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests {@link EiffelEventSender}
 */
public class EiffelEventSenderTests {

    EventFactory eventFactory;
    EiffelEventSender eiffelEventSender;
    EiffelConfiguration eiffelConfiguration;
    @Mock MessageSender messageSender;
    @Mock MessageSendWrapper messageSendWrapper;

    /**
     * Set up before testing.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eiffelConfiguration = new EiffelConfig("domainId", "exchangeName", "messageBusHost");
        eventFactory = new TestInheritEventFactory();
        eiffelEventSender = new EiffelEventSender(eiffelConfiguration, new EiffelEventConverter(eventFactory));

        ReflectionTestUtils.setField(eiffelEventSender, "sender", messageSender);
        ReflectionTestUtils.setField(eiffelEventSender, "sendWrapper", messageSendWrapper);
    }

    /**
     * Tests {@link EiffelEventSender#sendEvent(Event, String)}
     */
    @Test
    public void testSendEvent() throws Exception{
        Event event = eventFactory.createEvent(BaseEventImpl.class.getSimpleName());
        String routingKey = "";
        eiffelEventSender.sendEvent(event, routingKey);
        verify(messageSender, times(1)).send(any(EiffelMessage.class));

        event = eventFactory.createEvent(TestDummyEvent.class.getSimpleName());
        eiffelEventSender.sendEvent(event, routingKey);
        verify(messageSender, times(2)).send(any(EiffelMessage.class));
    }

}
