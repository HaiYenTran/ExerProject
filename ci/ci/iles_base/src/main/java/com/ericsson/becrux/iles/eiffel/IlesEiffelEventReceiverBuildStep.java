package com.ericsson.becrux.iles.eiffel;

import com.ericsson.becrux.base.common.eiffel.EiffelEventReceiver;
import com.ericsson.becrux.base.common.eiffel.EiffelEventReceiverBuildStep;
import com.ericsson.becrux.base.common.eiffel.exceptions.EiffelException;
import com.ericsson.becrux.base.common.utils.StringHelper;
import com.ericsson.becrux.iles.eiffel.events.IlesEventFactory;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;

/**
 * Build step for receiving ILES Eiffel Events.
 */
public class IlesEiffelEventReceiverBuildStep extends EiffelEventReceiverBuildStep {

    @DataBoundConstructor
    public IlesEiffelEventReceiverBuildStep(String tag,@Nonnull String receivedEventTypes, @Nonnull String secondaryBindings, @Nonnull String timeout, boolean customBindings) {
        super(tag, StringHelper.convertStringToList(receivedEventTypes), secondaryBindings, timeout, customBindings);
    }

    @Override
    protected EiffelEventReceiver initReceiver() throws EiffelException {
        if (getTag() != null)
            return new IlesEiffelEventReceiver(getTag(), null, true, null, getSecondaryBindings());
        else
            return new IlesEiffelEventReceiver(null, null, true, null, getSecondaryBindings());
    }

    public String getDefaultReceivedEventTypes() {
        StringBuilder types = new StringBuilder();
        IlesEventFactory.getInstance().getRegisteredClassNames().forEach(x -> types.append(x).append(", "));

        return types.toString().substring(0, types.length() - 2);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "ILES: Eiffel Event Receiver";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
