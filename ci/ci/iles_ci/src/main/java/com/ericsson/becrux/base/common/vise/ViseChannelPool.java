package com.ericsson.becrux.base.common.vise;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/**
 * Created by thien.d.vu on 12/8/2016.
 */
public class ViseChannelPool extends AbstractDescribableImpl<ViseChannelPool> {

    private String viseChannelPoolName;
    private List<ViseChannelName> viseChannelName;

    public String getViseChannelPoolName() {
        return viseChannelPoolName;
    }

    public void setViseChannelPoolName(String viseChannelPoolName) {
        this.viseChannelPoolName = viseChannelPoolName;
    }

    public List<ViseChannelName> getViseChannelName() {
        return viseChannelName;
    }

    public void setViseChannelName(List<ViseChannelName> viseChannelName) {
        this.viseChannelName = viseChannelName;
    }

    @DataBoundConstructor
    public ViseChannelPool(String viseChannelPoolName, List<ViseChannelName> viseChannelName) {
        this.viseChannelPoolName = viseChannelPoolName;
        this.viseChannelName = viseChannelName;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<ViseChannelPool> {

        @Override
        public String getDisplayName() {
            return "";
        }
    }
}
