package com.ericsson.becrux.iles.eiffel;

import com.ericsson.becrux.base.common.eiffel.EiffelEventReceiver;
import com.ericsson.becrux.base.common.eiffel.EiffelEventReceiverPostBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

/**
 * Created by emiwaso on 2016-12-19.
 */
public class IlesEiffelEventReceiverPostBuildStep extends EiffelEventReceiverPostBuildStep {

    @DataBoundConstructor
    public IlesEiffelEventReceiverPostBuildStep(String tag, @Nonnull String secondaryBindings, @Nonnull String timeout, boolean customBindings) {
        super(tag, secondaryBindings, timeout, customBindings);
    }

    @Override
    protected EiffelEventReceiver initReceiver() throws com.ericsson.becrux.base.common.eiffel.exceptions.EiffelException {
        if (getTag() != null)
            return new IlesEiffelEventReceiver(getTag(), null, true, null, getSecondaryBindings());
        else
            return new IlesEiffelEventReceiver(null, null, true, null, getSecondaryBindings());
    }

}
