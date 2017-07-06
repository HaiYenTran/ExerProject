package com.ericsson.becrux.base.common.eiffel.configuration;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class SecondaryBinding extends AbstractDescribableImpl<SecondaryBinding> {
    private String bindingKey;
    private String description;

    @DataBoundConstructor
    public SecondaryBinding(String bindingKey, String description) {
        this.bindingKey = bindingKey;
        this.description = description;
    }

    public String getBindingKey() {
        return bindingKey;
    }

    public void setBindingKey(String bindingKey) {
        this.bindingKey = bindingKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<SecondaryBinding> {
        public String getDisplayName() {
            return "";
        }
    }
}
