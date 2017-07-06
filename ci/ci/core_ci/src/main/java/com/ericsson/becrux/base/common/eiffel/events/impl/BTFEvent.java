package com.ericsson.becrux.base.common.eiffel.events.impl;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eventhandler.EventValidationResult;
import com.ericsson.becrux.base.common.loop.Phase;
import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.base.common.testexec.TestStatus;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Present Baseline Testing Feedback Event.
 */
public class BTFEvent extends Event {

    private List<String> products = new ArrayList<>();
    private List<String> baselines = new ArrayList<>();
    private Map<String, String> testScores = new HashMap<>();
    private Long jobId;
    private Phase phase;
    private PhaseStatus phaseStatus;
    private String message;
    private List<String> results = new ArrayList<>();
    private String viseChannel;
    private String componentDetails;
    //support log one site
    private String btfId;  //get from ITR id
    private BtfType btfType;    //BTF type
    private String buildLogUrl;
    private String requester;

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getBuildLogUrl() {
        return buildLogUrl;
    }

    public void setBuildLogUrl(String buildLogUrl) {
        this.buildLogUrl = buildLogUrl;
    }

    public String getComponentDetails() {
        return componentDetails;
    }

    public void setComponentDetails(String componentDetail) {
        this.componentDetails = componentDetail;
    }

    public String getViseChannel() {
        return viseChannel;
    }

    public void setViseChannel(String viseChannel) {
        this.viseChannel = viseChannel;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(@Nonnull List<String> products) {
        this.products = products;
    }

    public List<String> getBaselines() {
        return baselines;
    }

    public void setBaselines(@Nonnull List<String> baselines) {
        this.baselines = baselines;
    }

    public Map<TestStatus, Integer> getTestScores() {
        Map<TestStatus, Integer> map = new HashMap<>();
        for (Entry<String, String> entry : testScores.entrySet())
            map.put(TestStatus.valueOf(entry.getKey().toUpperCase()), Integer.parseInt(entry.getValue()));
        return map;
    }

    public void setTestScores(@Nonnull Map<TestStatus, Integer> testScores) {
        Map<String, String> map = new HashMap<>();
        if (testScores != null)
            for (Entry<TestStatus, Integer> entry : testScores.entrySet())
                map.put(entry.getKey().toString(), entry.getValue().toString());
        this.testScores = map;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public boolean isSuccessful() {
        return phaseStatus.equals(PhaseStatus.SUCCESS);
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public PhaseStatus getPhaseStatus() {
        return phaseStatus;
    }

    public void setPhaseStatus(PhaseStatus phaseStatus) {
        this.phaseStatus = phaseStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(@Nonnull List<String> results) {
        this.results = results;
    }

    public String getBtfId() {
        return btfId;
    }

    public void setBtfId(String btfId) {
        this.btfId = btfId;
    }

    public BtfType getBtfType() {
        return btfType;
    }

    public void setBtfType(BtfType btfType) {
        this.btfType = btfType;
    }

    public EventValidationResult validate() {

        EventValidationResult result = new EventValidationResult();
        if (products.isEmpty())
            result.addComment("Info: List of products is empty");
        else if (products.stream().anyMatch(p -> p == null))
            result.addError("There is a null product in the list of products");

        if (baselines.isEmpty())
            result.addComment("Info: List of baselines is empty");

        if (products.size() != baselines.size())
            result.addError("Lists of products and baselines differ in size");

        if (jobId == null)
            result.addError("JobId is empty");

        if (phase == null)
            result.addError("Phase is null");
        if (phaseStatus == null)
            result.addError("Phase status is null");

        if (btfId == null)
            result.addError("BtfId is null");

        if (btfType == null)
            result.addError("BTF Type is null");
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BTFEvent btfEvent = (BTFEvent) o;

        if (products != null ? !products.equals(btfEvent.products) : btfEvent.products != null) return false;
        if (baselines != null ? !baselines.equals(btfEvent.baselines) : btfEvent.baselines != null) return false;
        if (testScores != null ? !testScores.equals(btfEvent.testScores) : btfEvent.testScores != null) return false;
        if (jobId != null ? !jobId.equals(btfEvent.jobId) : btfEvent.jobId != null) return false;
        if (phase != btfEvent.phase) return false;
        if (phaseStatus != btfEvent.phaseStatus) return false;
        if (message != null ? !message.equals(btfEvent.message) : btfEvent.message != null) return false;
        if (btfId != null ? !btfId.equals(btfEvent.btfId) : btfEvent.btfId != null) return false;
        if (btfType != btfEvent.btfType) return false;
        return results != null ? results.equals(btfEvent.results) : btfEvent.results == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (products != null ? products.hashCode() : 0);
        result = 31 * result + (baselines != null ? baselines.hashCode() : 0);
        result = 31 * result + (testScores != null ? testScores.hashCode() : 0);
        result = 31 * result + (jobId != null ? jobId.hashCode() : 0);
        result = 31 * result + (phase != null ? phase.hashCode() : 0);
        result = 31 * result + (phaseStatus != null ? phaseStatus.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (btfId != null ? btfId.hashCode() : 0);
        result = 31 * result + (btfType != null ? btfType.hashCode() : 0);
        result = 31 * result + (results != null ? results.hashCode() : 0);
        return result;
    }

    public enum BtfType {
        STARTLOOP, ENDLOOP, REJECT, WAITING, RESTART
    }
}
