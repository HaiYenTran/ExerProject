package com.ericsson.becrux.iles.eiffel.eventrepository;

import static org.junit.Assert.*;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import org.junit.Ignore;
import org.junit.Test;
import java.util.List;


/*
 * Ignore this test because rename Type Event: INT -> ITR, in Eiffel we have old type event
 */
public class EventRepositoryTests
{
	public static final String eventRepository = "https://er001-eiffel021.rnd.ki.sw.ericsson.se:8443/eventrepository";
	
	@Ignore
	@Test
	public void testGetEventsNoSlash() throws EventRepositoryException
	{
		EventRepositoryCommunicator comm = new EventRepositoryCommunicator(eventRepository);
		List<Event> events = comm.getEventList();
		assertTrue(events.size() > 0);
	}
	
	@Ignore
	@Test
	public void testGetEventWithSlash() throws EventRepositoryException
	{
		EventRepositoryCommunicator comm = new EventRepositoryCommunicator(eventRepository + "/");
		List<Event> events = comm.getEventList();
		assertTrue(events.size() > 0);
	}
}
