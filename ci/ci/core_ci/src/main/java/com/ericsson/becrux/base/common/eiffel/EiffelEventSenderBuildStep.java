package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.eiffel.events.EventFactory;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventFactory;
import com.ericsson.becrux.base.common.eiffel.parameters.EiffelEventParameterValue_ToSend;
import com.ericsson.duraci.eiffelmessage.sending.exceptions.EiffelMessageSenderException;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a build step to send eiffel event.
 * Created by emiwaso on 2016-12-01.
 */
public class EiffelEventSenderBuildStep extends NwftBuildStep {

    private String tag;
    private boolean customTag;
    protected EventFactory eventFactory = new BaseEventFactory();

    /**
     * Constructor.
     * @param tag custom tag name
     * @param customTag true/false to use the custom tag for sent events
     */
    @DataBoundConstructor
    public EiffelEventSenderBuildStep(String tag, boolean customTag) {
        this.tag = tag;
        this.customTag = customTag;
    }

    public String getTag() { return tag; }

    public boolean getCustomTag() { return customTag; }

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        try{
            // Initialize eiffel event sender
            IEventSender sender = initSender();
            // Get all events need to be sent
            List<EiffelEventParameterValue_ToSend> events =
                    getAllNwftParametersOfType(build, EiffelEventParameterValue_ToSend.class)
                            .stream().filter(param -> !param.isUsed()).collect(Collectors.toList());
            for(EiffelEventParameterValue_ToSend e : events) {

                // Check if EiffelEventParameterValue_ToSend has tag
                String currentTag = this.tag;
                if (!customTag || (customTag && (tag == null || tag.isEmpty()))) {
                    currentTag = e.getTag();
                }

                // Sending event
                listener.getLogger().println("\nSending EVENT: " + eventFactory.toJson(e.getEvent()) + " with TAG_IN_ROUTING_KEY: " + currentTag);

                sender.sendEvent(e.getEvent(), currentTag);

                // After sending each value, set value used = true
                e.setUsed(true);
            }

        } catch(EiffelMessageSenderException ex) {
            listener.getLogger().println(ex);
            return false;
        }
        return true;
    }

    protected IEventSender initSender() {
        return new EiffelEventSender();
    }

    // need to implement this to child class
//    @Override
//    public DescriptorImpl getDescriptor() {
//        return (DescriptorImpl) super.getDescriptor();
//    }
//
//    @Extension
//    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
//        private String displayName = "CI: Eiffel Event Sender";
//
//        public DescriptorImpl() {
//            load();
//        }
//
//        @SuppressWarnings("rawtypes")
//        @Override
//        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
//            return true;
//        }
//
//        @Override
//        public String getDisplayName() {
//            return displayName;
//        }
//
//        public void setDisplayName(String displayName) {
//            this.displayName = displayName;
//        }
//
//        @Override
//        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
//            save();
//            return super.configure(req, formData);
//        }
//    }
}
