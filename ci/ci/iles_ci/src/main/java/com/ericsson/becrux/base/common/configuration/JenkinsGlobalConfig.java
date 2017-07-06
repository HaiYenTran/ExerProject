package com.ericsson.becrux.base.common.configuration;

import com.ericsson.becrux.base.common.core.BecruxDirectory;
import com.ericsson.becrux.base.common.core.NodeGuardian;
import com.ericsson.becrux.base.common.eiffel.configuration.SecondaryBinding;
import com.ericsson.becrux.base.common.eiffel.EiffelEventReceiver;
import com.ericsson.becrux.base.common.exceptions.BecruxDirectoryException;
import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.datawrappers.MessageBus;
import com.ericsson.duraci.eiffelmessage.binding.MBConnFactoryProvider;
import com.ericsson.duraci.eiffelmessage.binding.MessageBusBindings;
import com.ericsson.duraci.eiffelmessage.binding.configuration.BindingConfiguration;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.List;

public class JenkinsGlobalConfig extends Builder {

    public JenkinsGlobalConfig() {}

    // Singleton pattern implementation
    public static JenkinsGlobalConfig getInstance() {
        return JenkinsGlobalConfig.Holder.INSTANCE;
    }

    public String getLeoUrl() {
        return getDescriptor().getLeoUrl();
    }

    public List<SecondaryBinding> getSecondaryBindings() {
        return getDescriptor().getSecondaryBindings();
    }

    public String getLeoRoutingKey() {
        return getDescriptor().getLeoRoutingKey();
    }

    public String getSslFile() {
        return getDescriptor().getSslFile();
    }

    public boolean isNetconf() {
        return getDescriptor().isNetconf();
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        // All Jenkins global configuration fields must have getters and setters in this class
        // and textboxes in global.jelly file!!!
        private String leoUrl;
        private String leoRoutingKey;
        private String timeout;
        private boolean debugMode;
        private List<NodeGuardian> nodeGuardians;
        private List<SecondaryBinding> secondaryBindings;
        private List<SecondaryBinding> oldSecondaryBindings;
        private String sslFile;
        private boolean netconf;

        public List<NodeGuardian> getNodeGuardians() {
            return nodeGuardians;
        }

        public void setNodeGuardians(List<NodeGuardian> guardians) {
            this.nodeGuardians = guardians;
        }

        public String getLeoUrl() {
            return leoUrl;
        }

        public void setLeoUrl(String leoUrl) {
            this.leoUrl = leoUrl;
        }

        public String getLeoRoutingKey() {
            return leoRoutingKey;
        }

        public void setLeoRoutingKey(String leoRoutingKey) {
            this.leoRoutingKey = leoRoutingKey;
        }

        public String getTimeout() {
            return timeout;
        }

        public void setTimeout(String timeout) {
            this.timeout = timeout;
        }

        public boolean isDebugMode() {
            return debugMode;
        }

        public void setDebugMode(boolean debugMode) {
            this.debugMode = debugMode;
        }

        public List<SecondaryBinding> getSecondaryBindings() {
            return secondaryBindings;
        }

        public void setSecondaryBindings(List<SecondaryBinding> secondaryBindings) {
            this.secondaryBindings = secondaryBindings;
        }

        public String getSslFile() {
            return sslFile;
        }

        public void setSslFile(String sslFile) {
            this.sslFile = sslFile;
        }

        public boolean isNetconf() {
            return netconf;
        }

        public void setNetconf(boolean netconf) {
            this.netconf = netconf;
        }

        public DescriptorImpl() {
            load();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Core CI Engine Configuration";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);

            try (EiffelEventReceiver eventReceiver = new EiffelEventReceiver()) {

                MessageBusBindings bindings = eventReceiver.getBindings();
                BindingConfiguration bindingConfiguration = eventReceiver.getBindingConfiguration();
                EiffelConfiguration configuration = eventReceiver.getConfiguration();
                MessageBus mb = configuration.getMessageBus();

                ConnectionFactory fac = MBConnFactoryProvider.setupFactory(new ConnectionFactory(), mb);
                Connection conn = null;
                Channel channel = null;
                try {
                    conn = fac.newConnection();
                    channel = conn.createChannel();

                    String queueName = bindings.computeQueueName(bindingConfiguration);

                    if (!bindings.queueExists(bindingConfiguration))
                        channel.queueDeclare(queueName, true, false, false, null);

                    channel.queueBind(queueName, mb.getExchangeName(), eventReceiver.getFullBindingKey());
                    if (secondaryBindings != null) {
                        for (SecondaryBinding b : secondaryBindings)
                            channel.queueBind(queueName, mb.getExchangeName(), b.getBindingKey());
                    }
                    if (oldSecondaryBindings != null) {
                        for (SecondaryBinding b : oldSecondaryBindings) {

                            if (secondaryBindings == null || !secondaryBindings.stream().anyMatch(p -> p.getBindingKey().equals(b.getBindingKey())))
                                channel.queueUnbind(queueName, mb.getExchangeName(), b.getBindingKey());
                        }
                    }
                    oldSecondaryBindings = secondaryBindings;
                } finally {
                    if (channel != null)
                        channel.close();
                    if (conn != null)
                        conn.close();
                }
            } catch (Exception ex) {
                throw new FormException(ex, "Eiffel secondary bindings");
            }

            save();

            return super.configure(req, formData);
        }

        public FormValidation doCheckViseChannel(@QueryParameter String value) {
            return FormValidator.isValidViseChannel(value);
        }
    }

    /**
     * Holder of Singleton instance.
     */
    private static class Holder {
        static final JenkinsGlobalConfig INSTANCE = new JenkinsGlobalConfig();
    }
}