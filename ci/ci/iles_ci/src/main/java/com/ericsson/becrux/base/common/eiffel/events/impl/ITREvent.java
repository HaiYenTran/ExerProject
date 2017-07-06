package com.ericsson.becrux.base.common.eiffel.events.impl;

import com.ericsson.becrux.base.common.data.Version;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eventhandler.EventValidationResult;

import java.util.Map;

/**
 * Present Integration Test Request Event.
 */
public class ITREvent extends Event {


    private String product;
    private String baseline;
    private String artifact;
    private LoopType loopType; //LoopType is BASELINE/TEST
    private Map<String, String> parameters;
    private Long jobId;

    public final static String STANDALONE_TEST_BASELINE_CONFIG = "UNKNOWN";

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Version getBaseline() {
        if (baseline != null && !baseline.equals(STANDALONE_TEST_BASELINE_CONFIG))
            return Version.createReleaseVersion(baseline);
        return null;
    }

    public void setBaseline(Version baseline) {
        this.baseline = baseline.getVersion();
    }

    public void setBaseline(String baseline) {
        this.baseline = baseline;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public LoopType getLoopType() {
        return loopType;
    }

    public void setLoopType(LoopType loopType) {
        this.loopType = loopType;
    }

    public Map<String, String> getParameters() {

        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public EventValidationResult validate() {

        EventValidationResult result = new EventValidationResult();
        if (product == null)
            result.addError("Product is null");

        try {
            // in case of Standalone Test we will skip checking version
            if(!STANDALONE_TEST_BASELINE_CONFIG.equals(baseline)) {
                Version.createReleaseVersion(baseline);
            }
        } catch (Exception ex) {
            result.addError(ex.getMessage());
        }

        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        //result = prime * result + ((artifact == null) ? 0 : artifact.hashCode());
        result = prime * result + ((baseline == null) ? 0 : baseline.hashCode());
        result = prime * result + ((loopType == null) ? 0 : loopType.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((product == null) ? 0 : product.hashCode());
        result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ITREvent other = (ITREvent) obj;
        /*
        if (artifact == null) {
            if (other.artifact != null)
                return false;
        } else if (!artifact.equals(other.artifact))
            return false;
        */
        if (baseline == null) {
            if (other.baseline != null)
                return false;
        } else if (!baseline.equals(other.baseline))
            return false;

        if (loopType == null) {
            if (other.loopType != null) {
                return false;
            }
        } else if (!loopType.equals(other.loopType))
                return false;

        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;

        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;

        if (jobId == null) {
            if (other.jobId != null)
                return false;
        } else if (!jobId.equals(other.jobId))
            return false;

        return true;
    }

    public enum LoopType {
        BASELINE, TEST
    }

}
