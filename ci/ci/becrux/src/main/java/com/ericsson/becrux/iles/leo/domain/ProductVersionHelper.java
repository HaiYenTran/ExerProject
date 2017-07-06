package com.ericsson.becrux.iles.leo.domain;

import com.ericsson.becrux.base.common.core.NodeGuardian;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by thien.d.vu on 12/7/2016.
 */
public class ProductVersionHelper extends AbstractDescribableImpl<ProductVersionHelper> {

    private String productVersion;
    private long releaseId;
    private String description;
    private String productName;
    private String signum;

    @DataBoundConstructor
    public ProductVersionHelper(String productVersion, long releaseId, String description, String productName, String signum) {
        this.productVersion = productVersion;
        this.releaseId = releaseId;
        this.description = description;
        this.productName = productName;
        this.signum = signum;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public long getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(long releaseId) {
        this.releaseId = releaseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSignum() {
        return signum;
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
    }
}
