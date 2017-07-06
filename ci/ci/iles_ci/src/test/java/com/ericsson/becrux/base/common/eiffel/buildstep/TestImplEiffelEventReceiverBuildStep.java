package com.ericsson.becrux.base.common.eiffel.buildstep;

import com.ericsson.becrux.base.common.eiffel.EiffelEventReceiverBuildStep;
import com.ericsson.becrux.base.common.eiffel.events.impl.TestInheritEventFactory;

import javax.annotation.Nonnull;

/**
 * An base implement of {@link EiffelEventReceiverBuildStep} for testing purpose
 */
public class TestImplEiffelEventReceiverBuildStep extends EiffelEventReceiverBuildStep {

    /**
     * Constructor.
     *
     * @param tag                specify which specific value will be received, tag can be null.
     * @param secondaryBindings  This is a list of machines that we want to get value to, secondaryBindings can be null
     * @param timeout            Amount of time in milliseconds that Eiffel Receiver will spend on pulling events from the queue.
     *                           Decrease to speed up Eiffel Receiver execution, increase in case of value loss.
     * @param customBindings
     */
    public TestImplEiffelEventReceiverBuildStep(String tag, @Nonnull String secondaryBindings, @Nonnull String timeout, boolean customBindings) {
        super(tag, new TestInheritEventFactory().getRegisteredClassNames(), secondaryBindings, timeout, customBindings);
    }
}
