package com.ericsson.becrux.base.common.vise;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class ViseChannelField extends AbstractDescribableImpl<ViseChannelField> {

    private String name;

    @DataBoundConstructor
    public ViseChannelField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<ViseChannelField> {

        public FormValidation doCheckName(@QueryParameter String value) {
            try {
                ViseChannel vise = new ViseChannel(value);
                return FormValidation.okWithMarkup(
                        "VISE value <b>" + vise.getFullName() + "</b> recognized.");

            } catch (Exception e) {
                return FormValidation.errorWithMarkup(e.getMessage());
            }
        }

        @Override
        public String getDisplayName() {
            return "";
        }
    }

}
