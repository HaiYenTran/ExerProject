package com.ericsson.becrux.iles.eventhandler;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.becrux.base.common.eventhandler.EventQueue;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import org.junit.Test;

import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;


public class EventSchedulerComparatorTests {

	@Test
	public void CorrectCompare() {
		EventSchedulerComparator comparator = new EventSchedulerComparator();

		assertEquals(0, comparator.compare(new ITREvent(), new ITREvent()));
		assertEquals(-1, comparator.compare(new ITREvent(), null));
		assertEquals(1, comparator.compare(null,  new ITREvent()));
		assertEquals(0, comparator.compare(null, null));


		assertEquals(-1, comparator.compare(new BTFEvent(), new ITREvent()));


	}

	@Test
	public void CorrectOrdering() {
		EventQueue queue = new EventQueue(new EventSchedulerComparator());

		ITREvent i1 = new ITREvent();
		ITREvent i2 = new ITREvent();
		i1.setBaseline("1.0");
		i2.setBaseline("2.0");
		queue.pushEvent(i1);
		queue.pushEvent(i2);

		BTFEvent b1 = new BTFEvent();
		BTFEvent b2 = new BTFEvent();
		List<String> b1Baselines = new ArrayList<>();
		List<String> b2Baselines = new ArrayList<>();
		b1Baselines.add("1.0");
		b1Baselines.add("2.0");
		b1Baselines.add("1.0");
		b1Baselines.add("1.0");
		b2Baselines.add("1.0");
		b2Baselines.add("1.0");
		b2Baselines.add("1.0");
		b2Baselines.add("1.0");
		b1.setBaselines(b1Baselines);
		b2.setBaselines(b2Baselines);
		queue.pushEvent(b1);
		queue.pushEvent(b2);

		assertEquals(b2, queue.pullEvent());
		assertEquals(b1, queue.pullEvent());
		assertEquals(i2, queue.pullEvent());
		assertEquals(i1, queue.pullEvent());
	}
}
