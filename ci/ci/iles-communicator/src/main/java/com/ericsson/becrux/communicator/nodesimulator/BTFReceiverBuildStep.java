package com.ericsson.becrux.communicator.nodesimulator;
import com.ericsson.becrux.base.common.eiffel.EiffelEventConverter;
import com.ericsson.becrux.base.common.eiffel.EiffelEventReceiver;
import com.ericsson.becrux.base.common.eiffel.configuration.SecondaryBinding;
import com.ericsson.becrux.base.common.utils.Timestamped;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.communicator.eiffel.events.CommunicatorEventFactory;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a build step in a build process which
 * receives and process BTF event. BTF event sent by the system
 * to IMS nodes is received using EiffelEventReceiver and logged
 * using BuildListener.
 */
public class BTFReceiverBuildStep extends Builder implements BuildStep {

    private final String nodeType;
    private final String bindingKey;

    @DataBoundConstructor
    public BTFReceiverBuildStep(@Nonnull String nodeType, @Nonnull String bindingKey) {
        this.nodeType = nodeType;
        this.bindingKey = bindingKey;
    }
    public String getNodeType() {
        return nodeType;
    }

    public String getBindingKey() {
        return bindingKey;
    }

    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        EiffelEventReceiver receiver = null;
        try {
            // Create queue and bind new secondary binding key to receive BTF event
            List<SecondaryBinding> secondaryBindings;
            secondaryBindings = new LinkedList<SecondaryBinding>();
            secondaryBindings.add(new SecondaryBinding(bindingKey, ""));

            //Created a queue with name:
            //<domainId>.<componentId>.<node>-btfreceiverbuildstep.durable
            //Example: eiffel021.seki.fem002.fem002-eiffel021.mtas-btfreceiverbuildstep.durable
            receiver = new EiffelEventReceiver(getNodeType().toLowerCase(), null, true,
                    getNodeType().toLowerCase()+ "-" + this.getClass().getSimpleName().toLowerCase(), secondaryBindings, new EiffelEventConverter(CommunicatorEventFactory.getInstance()));

            receiver.start();
            Thread.sleep(5000);
            receiver.stop();
            List<Timestamped<BTFEvent>> BTFReceiver = receiver.getEventQueue()
                    .stream()
                    .filter(e -> e instanceof BTFEvent)
                    .map(e -> (BTFEvent) e)
                    .map(e -> new Timestamped<BTFEvent>(e))
                    .collect(Collectors.toList());

            if (BTFReceiver.size() > 0) {
                for (Timestamped<BTFEvent> e : BTFReceiver) {
                    listener.getLogger().println("\n*" + e.getDate() + " | Receive BTF event for " + e.getObject().getComponentDetails());
                    listener.getLogger().println("\n" + e.getDate() + ": " + CommunicatorEventFactory.getInstance().toJson(e.getObject()));

                    listener.getLogger().println("Phase: " + e.getObject().getPhase());
                    listener.getLogger().println("Phase Status: " + e.getObject().getPhaseStatus());
                    listener.getLogger().println("Message: "+ e.getObject().getMessage());
                    listener.getLogger().println("VISE channel used: " + e.getObject().getViseChannel());
                    listener.getLogger().println("LEO JobID: " + e.getObject().getJobId());
                    listener.getLogger().println("BTF Id: " + e.getObject().getBtfId());
                    listener.getLogger().println("BTF Type: " + e.getObject().getBtfType());
                    listener.getLogger().println("ITR Job Requester: " + e.getObject().getRequester());
                    listener.getLogger().println("\n-------More details for each process loop-------");
//                    String resultsWithoutBrackets = e.getObject().getResults().toString().replace("[", "").replace("]", "");
//                    listener.getLogger().println(resultsWithoutBrackets);
                    listener.getLogger().println(e.getObject().getResults().stream().map(m->m.toString()).collect(Collectors.joining(",")));

                    if (!e.getObject().getTestScores().isEmpty())
                        listener.getLogger().println("Test scores: " + e.getObject().getTestScores());

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(listener.getLogger());
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

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @SuppressWarnings("rawtypes")
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "ILES-Communicator: BTF Receiver";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }

}
