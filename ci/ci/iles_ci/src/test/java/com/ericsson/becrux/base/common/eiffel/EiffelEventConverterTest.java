package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.data.impl.BaseComponentFactory;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventFactory;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventImpl;
import com.ericsson.becrux.base.common.eiffel.events.impl.TestDummyEvent;
import com.ericsson.becrux.base.common.eiffel.events.impl.TestInheritEventFactory;
import com.ericsson.duraci.eiffelmessage.messages.EiffelEvent;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link EiffelEventConverter}.
 */
public class EiffelEventConverterTest {

    @Test
    public void testConvertToEiffelEvent() throws Exception{
        Event event = new BaseEventFactory().createEvent(BaseEventImpl.class.getSimpleName());
        EiffelEvent eiffelEvent = new EiffelEventConverter(new BaseEventFactory()).convertToEiffelEvent(event);
        assertTrue(eiffelEvent != null);
    }

    @Test
    public void testAdditionParams() throws Exception{
        TestInheritEventFactory factory = new TestInheritEventFactory();
        TestDummyEvent event = (TestDummyEvent)(factory.createEvent(TestDummyEvent.class.getSimpleName()));
        Map<String, String> pams = new HashMap<>();
        pams.put("key1","value1");
        pams.put("key2","value2");
        event.setParams(pams);
        String json = factory.toJson(event);
        TestDummyEvent event2 = (TestDummyEvent)factory.fromJson(json);
        assertTrue(event != null);
    }

}
