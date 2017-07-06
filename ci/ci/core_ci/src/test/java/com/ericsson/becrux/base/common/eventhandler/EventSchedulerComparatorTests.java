package com.ericsson.becrux.base.common.eventhandler;

//import com.ericsson.becrux.iles.eiffel.events.ITREvent;
//import com.ericsson.becrux.iles.eiffel.events.OPBEvent;
//import com.ericsson.becrux.iles.eventhandler.EventSchedulerComparator;

//import com.ericsson.becrux.iles.eiffel.events.BTFEvent;
//import com.ericsson.becrux.iles.eiffel.events.NTAEvent;
//import com.ericsson.becrux.iles.eiffel.events.NTFEvent;

// TODO: need refactor
public class EventSchedulerComparatorTests {
//
//	@Test
//	public void CorrectCompare() {
//		EventSchedulerComparator comparator = new EventSchedulerComparator();
//
//		assertEquals(0, comparator.compare(new ITREvent(), new ITREvent()));
//		assertEquals(-1, comparator.compare(new ITREvent(), null));
//		assertEquals(1, comparator.compare(null,  new ITREvent()));
//		assertEquals(0, comparator.compare(null, null));
//
//		assertEquals(-1, comparator.compare(new OPBEvent(), new ITREvent()));
//		assertEquals(-1, comparator.compare(new NTAEvent(), new ITREvent()));
//		assertEquals(-1, comparator.compare(new BTFEvent(), new ITREvent()));
//
//		assertEquals(-1, comparator.compare(new NTFEvent(), new NTAEvent()));
//		assertEquals(-1, comparator.compare(new OPBEvent(), new BTFEvent()));
//
//		assertEquals(1, comparator.compare(new EventForTests(), new ITREvent()));
//	}
//
//	@Test
//	public void CorrectOrdering() {
//		EventQueue queue = new EventQueue();
//
//		ITREvent i1 = new ITREvent();
//		ITREvent i2 = new ITREvent();
//		i1.setBaseline(Version.create("1.0"));
//		i2.setBaseline(Version.create("2.0"));
//		queue.pushEvent(i1);
//		queue.pushEvent(i2);
//
//		BTFEvent b1 = new BTFEvent();
//		BTFEvent b2 = new BTFEvent();
//		List<Version> b1Baselines = new ArrayList<>();
//		List<Version> b2Baselines = new ArrayList<>();
//		b1Baselines.add(Version.create("1.0"));
//		b1Baselines.add(Version.create("2.0"));
//		b1Baselines.add(Version.create("1.0"));
//		b1Baselines.add(Version.create("1.0"));
//		b2Baselines.add(Version.create("1.0"));
//		b2Baselines.add(Version.create("1.0"));
//		b2Baselines.add(Version.create("1.0"));
//		b2Baselines.add(Version.create("1.0"));
//		b1.setBaselines(b1Baselines);
//		b2.setBaselines(b2Baselines);
//		queue.pushEvent(b1);
//		queue.pushEvent(b2);
//
//		NTAEvent nt1 = new NTAEvent();
//		queue.pushEvent(nt1);
//
//		NTFEvent nf1 = new NTFEvent();
//		queue.pushEvent(nf1);
//
//		assertEquals(nf1, queue.pullEvent());
//		assertEquals(nt1, queue.pullEvent());
//		assertEquals(b2, queue.pullEvent());
//		assertEquals(b1, queue.pullEvent());
//		assertEquals(i2, queue.pullEvent());
//		assertEquals(i1, queue.pullEvent());
//	}
}
