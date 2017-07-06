package com.ericsson.eiffel.becrux.core;

import java.util.Queue;

import com.ericsson.eiffel.becrux.events.Event;


public interface IEventReceiver extends AutoCloseable {

	Queue<Event> getEventQueue();

	void start() throws EiffelException;

	void stop() throws EiffelException;
	
	boolean isStarted();
	
	void waitForEvent() throws EiffelException;
	
	void waitForEvent(long timeout) throws EiffelException;
}
