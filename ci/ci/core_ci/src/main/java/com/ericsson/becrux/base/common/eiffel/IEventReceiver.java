package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.exceptions.EiffelException;
import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.eiffelmessage.binding.MessageBusBindings;
import com.ericsson.duraci.eiffelmessage.binding.configuration.BindingConfiguration;

import java.util.Queue;

public interface IEventReceiver extends AutoCloseable {

    Queue<Event> getEventQueue();

    void start() throws EiffelException;

    void stop() throws EiffelException;

    boolean isStarted();

    void waitForEvent() throws EiffelException;

    void waitForEvent(long timeout) throws EiffelException;

    String getFullBindingKey();

    EiffelConfiguration getConfiguration();

    BindingConfiguration getBindingConfiguration();

    MessageBusBindings getBindings();
}
