package com.ericsson.becrux.iles.eiffel;

import com.ericsson.becrux.base.common.eiffel.EiffelEventSenderBuildStep;
import com.ericsson.becrux.base.common.eiffel.IEventSender;
import com.ericsson.becrux.iles.eiffel.events.IlesEventFactory;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Represents a build step to send Iles eiffel event.
 * Created by emiwaso on 2016-12-01.
 */
public class IlesEiffelEventSenderBuildStep extends EiffelEventSenderBuildStep {

    /**
     * Constructor.
     * @param tag custom tag name
     * @param customTag true/false to use the custom tag for sent events
     */
    @DataBoundConstructor
    public IlesEiffelEventSenderBuildStep(String tag, boolean customTag) {
        super(tag, customTag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        this.eventFactory = IlesEventFactory.getInstance();
        return super.perform(build, launcher, listener);
    }

    protected IEventSender initSender() {
        return new IlesEiffelEventSender();
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
            return "ILES: Eiffel Event Sender";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
