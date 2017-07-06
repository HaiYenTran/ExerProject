package com.ericsson.becrux.iles.eiffel.events;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link IlesEventFactory}
 */
public class IlesEventFactoryTest {

    /**
     * Tests {@link IlesEventFactory#createEvent(String)}
     * @throws Exception if anything fail
     */
    @Test
    public void testcCreateEvent() throws Exception{
        Event itr = IlesEventFactory.getInstance().createEvent("ITREvent");
        Event btf = IlesEventFactory.getInstance().createEvent("BTFEvent");

        assertTrue(itr != null && itr instanceof ITREvent);
        assertTrue(btf != null && btf instanceof BTFEvent);

    }

    /**
     * Tests {@link IlesEventFactory#createEvent(String)}
     * @throws Exception if anything fail
     */
    @Test(expected = Exception.class)
    public void testConvertUnknownFormat() throws Exception {
        Event event = IlesEventFactory.getInstance().createEvent("UnknownEvent");
    }

    @Test
    public void testConvertEventToJson() throws Exception {
        Event itr = IlesEventFactory.getInstance().createEvent("ITREvent");
        String itrString = IlesEventFactory.getInstance().toJson(itr);
        assertTrue(itrString != null);
        Event result = IlesEventFactory.getInstance().fromJson(itrString);
        assertTrue(result instanceof ITREvent);
        assertTrue(result != null);

    }

}
