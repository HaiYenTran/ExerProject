package com.ericsson.becrux.iles.eiffel.events;

import static org.junit.Assert.*;

import java.util.*;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import com.ericsson.becrux.base.common.loop.Phase;
import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.base.common.testexec.TestStatus;
import com.ericsson.becrux.iles.data.IlesComponentFactory;
import org.junit.Assert;
import org.junit.Test;

public class EventTests
{
	@Test
	public void testSerializationAndDeserializationITR()
	{
		ITREvent event = new ITREvent();
		event.setArtifact("http://artifactory.com/artifact.tar.gz");
		event.setBaseline("1.0");
		event.setBuildId("1");
		event.setProduct("MTAS");
		if(!event.validate().isSuccessful())
			fail("Primary value validation failed");
		String json = IlesEventFactory.getInstance().toJson(event);
		ITREvent event2 = (ITREvent) IlesEventFactory.getInstance().fromJson(json);
		if(!event2.validate().isSuccessful())
			fail("Secondary value validation failed");
		assertEquals(event, event2);
		assertEquals(event.getArtifact(), event2.getArtifact());
		Assert.assertEquals(event.getBaseline(), event2.getBaseline());
		assertEquals(event.getBuildId(), event2.getBuildId());
		assertEquals(event.getProduct(), event2.getProduct());
		assertEquals(event.getParameters(), event2.getParameters());
	}

	@Test
	public void testSerializationAndDeserializationBTF()
	{
		BTFEvent event = new BTFEvent();
		List<String> baselines = new ArrayList<>();
		baselines.add("1.0");
		baselines.add("R1A");
		Map<TestStatus, Integer> testScores = new HashMap<>();
		testScores.put(TestStatus.FAILED, 1);
		testScores.put(TestStatus.SKIPPED, 0);
		testScores.put(TestStatus.PASSED, 10);
		testScores.put(TestStatus.TOTAL, 11);
		
		event.setBaselines(baselines);
		event.setBuildId("1");
		event.getProducts().add("MTAS");
		event.getProducts().add("CSCF");
		event.setMessage("Test message");
		event.setPhase(Phase.PROCESSING);
		event.setPhaseStatus(PhaseStatus.SUCCESS);
		event.setJobId(1);
		event.getResults().add("http://artifactory.com/artifact.tar.gz");
		event.setTestScores(testScores);
		event.setBtfId("123");
		event.setBtfType(BTFEvent.BtfType.STARTLOOP);
		if(!event.validate().isSuccessful())
			fail("Primary value validation failed");
		String json = IlesEventFactory.getInstance().toJson(event);
		BTFEvent event2 = (BTFEvent)IlesEventFactory.getInstance().fromJson(json);
		if(!event2.validate().isSuccessful())
			fail("Secondary value validation failed");
		assertEquals(event, event2);
		assertEquals(event.getBaselines(), event2.getBaselines());
		assertEquals(event.getBuildId(), event2.getBuildId());
		assertEquals(event.getProducts(), event2.getProducts());
		assertEquals(event.getMessage(), event2.getMessage());
		assertEquals(event.getPhase(), event2.getPhase());
		assertEquals(event.getResults(), event2.getResults());
		assertEquals(event.getTestScores(), event2.getTestScores());
		assertEquals(event.getBtfId(), event2.getBtfId());
		assertEquals(event.getBtfType(), event2.getBtfType());
	}


	@Test
	public void testMultithreadedDeserialization() throws Exception
	{
		ITREvent event = new ITREvent();
		event.setArtifact("http://artifactory.com/artifact.tar.gz");
		event.setBaseline("1.0");
		event.setBuildId("1");
		event.setProduct("MTAS");
		if(!event.validate().isSuccessful())
			fail("Primary value validation failed");

		List<Thread> threads = new ArrayList<>();
		List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
		int count = 50;
		for(int i = 0; i < count; ++i) {
			threads.add(new Thread(() ->
			{
				String json = IlesEventFactory.getInstance().toJson(event);
				Event des = IlesEventFactory.getInstance().fromJson(json);
				try {
					assertEquals(event, des);
				} catch(Exception ex) {
					exceptions.add(ex);
				}
			}));
		}
		for(Thread t : threads)
			t.start();
		for(Thread t : threads)
			t.join();

		if(exceptions.size() > 0) {
			Exception e = new Exception("Error in multithreaded deserialization acccess");
			for(Exception ex : exceptions)
				e.addSuppressed(ex);
			throw e;
		}
	}

	@Test
	public void testNewITREventWithUniqueID() {
		ITREvent e1 = new ITREvent();
		ITREvent e2 = new ITREvent();

		assertTrue(e1.getID() != null && e2.getID() != null);
		assertTrue(e1.getID() != e2.getID());
	}
}