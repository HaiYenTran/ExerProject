package com.ericsson.becrux.iles.eiffel;

import com.ericsson.becrux.base.common.eiffel.EiffelEventConverter;
import com.ericsson.becrux.base.common.eiffel.EiffelEventSender;
import com.ericsson.becrux.base.common.eiffel.IEventSender;
import com.ericsson.becrux.iles.eiffel.events.IlesEventFactory;
import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.configuration.EiffelJenkinsGlobalConfiguration;

/**
 * Represents ILES CI eiffel event sender.
 */
public class IlesEiffelEventSender extends EiffelEventSender implements IEventSender {

    private static EiffelEventConverter converter = new EiffelEventConverter(IlesEventFactory.getInstance());

    public IlesEiffelEventSender() {
        this(new EiffelJenkinsGlobalConfiguration.Provider().provide());
    }

    public IlesEiffelEventSender(EiffelConfiguration configuration) {
        super(configuration, converter);
    }
}