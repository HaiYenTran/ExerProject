package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.eiffel.configuration.SecondaryBinding;
import com.ericsson.becrux.base.common.eiffel.exceptions.EiffelException;
import com.ericsson.becrux.base.common.eiffel.parameters.EiffelEventParameterValue_Received;
import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.utils.StringHelper;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract build step for receiving Eiffel Events.
 */
public abstract class EiffelEventReceiverBuildStep extends NwftBuildStep {

    private String tag;
    private List<SecondaryBinding> secondaryBindings;
    private int timeout;
    private boolean customBindings;
    private List<String> receivedEventTypes = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param tag specify which specific value will be received, tag can be null.
     * @param receivedEventTypes specify the event types that the buildstep will receive, base on the issue from RabbitMQ
     *                           the received events but not in list of event type will be remove, not yet find the
     *                           better solution.
     * @param secondaryBindings This is a list of machines that we want to get value to, secondaryBindings can be null
     * @param timeout Amount of time in milliseconds that Eiffel Receiver will spend on pulling events from the queue.
     *                Decrease to speed up Eiffel Receiver execution, increase in case of value loss.
     * @param customBindings
     */
    public EiffelEventReceiverBuildStep(String tag,@Nonnull List<String> receivedEventTypes,@Nonnull String secondaryBindings, @Nonnull String timeout, boolean customBindings) {

        if (tag == null || tag.isEmpty())
            this.tag = "*";
        else
            this.tag = tag;

        this.receivedEventTypes.addAll(receivedEventTypes);

        // Adding secondary bindings to listening events from other domain
        // Get secondary bindings from user input
        this.customBindings = customBindings;

        if (customBindings) {
            this.secondaryBindings = Arrays.asList(secondaryBindings.split("\\r?\\n"))
                    .stream()
                    .filter(b -> !b.isEmpty())
                    .map(b -> new SecondaryBinding(b, ""))
                    .collect(Collectors.toList());
        } else {
            // Only use secondary bindings from Global Config

            JenkinsGlobalConfig config = new JenkinsGlobalConfig();
            this.secondaryBindings = config.getSecondaryBindings();
        }

        try {
            setTimeout(Integer.parseInt(timeout));
        } catch (NumberFormatException ex) {
            setTimeout(5000); //Set default timeout
        }
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getReceivedEventTypes() {
        StringBuilder types = new StringBuilder();
        receivedEventTypes.forEach(x -> types.append(x).append(", "));

        return types.toString().substring(0, types.length() - 2);
    }

    public void setReceivedEventTypes(String receivedEventTypes) {
        this.receivedEventTypes = StringHelper.convertStringToList(receivedEventTypes);
    }

    public List<SecondaryBinding> getSecondaryBindings() {
        return secondaryBindings;
    }

    public void setSecondaryBindings(List<SecondaryBinding> secondaryBindings) {
        this.secondaryBindings = secondaryBindings;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        if (timeout <= 0)
            throw new IllegalArgumentException("Invalid timeout value");
        this.timeout = timeout;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        listener.getLogger().println("List of receivedEvent Types: " + receivedEventTypes);

        IEventReceiver receiver = null;

        try {
            receiver = initReceiver();

            receiver.start();
            Thread.sleep(timeout);
            receiver.stop();

            receiver.getEventQueue()
                    .stream()
                    .filter(event -> receivedEventTypes.contains(event.getType()))
                    .map(e -> new EiffelEventParameterValue_Received(e.getType(), e))
                    .forEach(e -> addNwftBuildParameter(build, e));

        } catch (EiffelException | InterruptedException ex) {
            listener.getLogger().println(ex);
            return false;
        } finally {
            if (receiver != null) {
                try {
                    receiver.close();
                } catch (Exception e) {
                    e.printStackTrace(listener.getLogger());
                }
            }
        }
        return true;
    }

    protected EiffelEventReceiver initReceiver() throws EiffelException {
        if (tag != null)
            return new EiffelEventReceiver(tag, null, true, null, secondaryBindings);
        else
            return new EiffelEventReceiver(null, null, true, null, secondaryBindings);
    }

    // need to implement this to child class
//    @Override
//    public DescriptorImpl getDescriptor() {
//        return (DescriptorImpl) super.getDescriptor();
//    }
//
//    @Extension
//    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
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
//            return "";
//        }
//
//        @Override
//        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
//            save();
//            return super.configure(req, formData);
//        }
//    }
}
