package com.ericsson.becrux.iles.eiffel;

import com.ericsson.becrux.base.common.eiffel.EiffelEventSenderPostBuildStep;
import com.ericsson.becrux.base.common.eiffel.IEventSender;

/**
 * Created by emiwaso on 2016-12-19.
 */
public class IlesEiffelEventSenderPostBuildStep extends EiffelEventSenderPostBuildStep {

    public IlesEiffelEventSenderPostBuildStep(String tag, boolean customTag) {
        super(tag, customTag);
    }

    protected IEventSender initSender() {
        return new IlesEiffelEventSender();
    }

}
