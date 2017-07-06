package com.ericsson.becrux.iles.eiffel;

import com.ericsson.becrux.base.common.eiffel.EiffelEventConverter;
import com.ericsson.becrux.base.common.eiffel.EiffelEventReceiver;
import com.ericsson.becrux.base.common.eiffel.IEventReceiver;
import com.ericsson.becrux.base.common.eiffel.configuration.SecondaryBinding;
import com.ericsson.becrux.base.common.eiffel.exceptions.EiffelException;
import com.ericsson.becrux.iles.eiffel.events.IlesEventFactory;
import com.ericsson.duraci.configuration.EiffelConfiguration;

import java.util.List;

public class IlesEiffelEventReceiver extends EiffelEventReceiver implements IEventReceiver {

    public IlesEiffelEventReceiver() throws EiffelException {
        super(null, null, true, null, null, new EiffelEventConverter(IlesEventFactory.getInstance()));
    }

    public IlesEiffelEventReceiver(String tag, EiffelConfiguration configuration, boolean durable, String consumerName, List<SecondaryBinding> secondaryBindings) throws EiffelException {
        super(tag, configuration, durable, consumerName, secondaryBindings, new EiffelEventConverter(IlesEventFactory.getInstance()));
    }
}
