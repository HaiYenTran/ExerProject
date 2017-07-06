package com.ericsson.becrux.communicator.nodesimulator;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * The parameters for using in ITR Sender buildstep.
 * @author TrongLe
 */
public class Parameters extends AbstractDescribableImpl<Parameters> {

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isExtendedCondition() {
        return extendedCondition;
    }

    public void setExtendedCondition(boolean extendedCondition) {
        this.extendedCondition = extendedCondition;
    }

    private String key;
    private String value;
    private String condition = "true";
    private boolean extendedCondition;

    @DataBoundConstructor
    public Parameters(String key, String value, String condition, boolean extendedCondition) {
        this.key = key;
        this.value = value;
        this.condition = condition;
        this.extendedCondition = extendedCondition;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Parameters> {

        @Override
        public String getDisplayName() {
            return "";
        }
    }
}
