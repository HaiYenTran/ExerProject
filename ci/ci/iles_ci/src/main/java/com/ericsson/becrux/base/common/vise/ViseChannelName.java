package com.ericsson.becrux.base.common.vise;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Created by thien.d.vu on 12/8/2016.
 */
public class ViseChannelName extends AbstractDescribableImpl<ViseChannelName> {

    private String name;
    private String ipAddr;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    @DataBoundConstructor
    public ViseChannelName(String name, String ipAddr) {
        this.name = name;
        this.ipAddr = ipAddr;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<ViseChannelName> {
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
