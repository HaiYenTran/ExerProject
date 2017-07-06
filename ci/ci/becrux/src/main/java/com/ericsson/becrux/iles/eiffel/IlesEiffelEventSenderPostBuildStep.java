package com.ericsson.becrux.iles.eiffel;

import com.ericsson.becrux.base.common.eiffel.EiffelEventSenderPostBuildStep;
import com.ericsson.becrux.base.common.eiffel.IEventSender;

/**
 * Represents a post build step to send Iles eiffel event.
 * Created by emiwaso on 2016-12-19.
 */
public class IlesEiffelEventSenderPostBuildStep extends EiffelEventSenderPostBuildStep {

    /**
     * Constructor.
     * @param tag custom tag name
     * @param customTag true/false to use the custom tag for sent events
     */
    public IlesEiffelEventSenderPostBuildStep(String tag, boolean customTag) {
        super(tag, customTag);
    }

    /**
     * Initialize eiffel event sender.
     * @return instance of IlesEiffelEventSender
     */
    protected IEventSender initSender() {
        return new IlesEiffelEventSender();
    }

}
