package com.ericsson.becrux.base.common.core;

import com.ericsson.becrux.base.common.configuration.FormValidator;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;

public class NodeGuardian extends AbstractDescribableImpl<NodeGuardian> {

    private String mail;
    private String signum;

    @DataBoundConstructor
    public NodeGuardian(@Nonnull String mail, String signum) {
        this.mail = mail;
        this.signum = signum;
    }

    public String getSignum() {
        return signum;
    }

    public String getMail() {
        return this.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setSignum(String signum) {
        this.signum = signum;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<NodeGuardian> {
        @Override
        public String getDisplayName() {
            return "";
        }

        public FormValidation doCheckMail(@QueryParameter String value) {
            return FormValidator.isValidEmail(value);
        }
    }
}