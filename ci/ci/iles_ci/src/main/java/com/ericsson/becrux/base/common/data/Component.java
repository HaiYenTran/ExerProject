package com.ericsson.becrux.base.common.data;

import com.ericsson.becrux.base.common.deploy.NodeConfiguration;
import org.joda.time.DateTime;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public abstract class Component implements Comparable<Object> {
    private String type;
    private State state;
    private String artifact;
    private Version version;
    private String pdb;
    private Long jobId;
    private DateTime votingTimeOut;

    private boolean installable;

    protected Component() {
        this.type = this.getClass().getSimpleName();
        installable = true;
    }

    public Component(@Nonnull Version version) {
        this();
        this.version = version;
    }

    public Component(@Nonnull Version version, String artifact) {
        this(version);
        this.artifact = artifact;
    }

    public Component(@Nonnull Version version, State state) {
        this(version);
        this.state = state;
    }

    public DateTime getVotingTimeOut() {
        return votingTimeOut;
    }

    public void setVotingTimeOut(DateTime votingTimeOut) {
        this.votingTimeOut = votingTimeOut;
    }

    public String getType() {
        return type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setPdb(String pdb) {
        this.pdb = pdb;
    }

    public String getPdb() {
        return this.pdb;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public boolean isInstallable() {
        return installable;
    }

    public void setInstallable(boolean installable) {
        this.installable = installable;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((artifact == null) ? 0 : artifact.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Component other = (Component) obj;
        if (artifact == null && other.artifact != null)
            return false;
        if (artifact != null && !artifact.equals(other.artifact))
            return false;

        if (state != other.state)
            return false;

        if (version == null && other.version != null)
            return false;
        if (version != null && !version.equals(other.version))
            return false;

        return true;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null)
            return 1;

        Component that = (Component) o;
        if (this.type.equals(that.getType()))
            return this.version.compareTo(that.getVersion());

        return 0;
    }

    public HashMap<String, String> getDeployParameters() throws IllegalArgumentException, MalformedURLException, IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        if (getArtifact() == null || getArtifact().isEmpty())
            throw new IllegalArgumentException("Missing nodes artifact");
        if (getType() == null)
            throw new IllegalArgumentException("Missing nodes type");
        map.put(NodeConfiguration.VNF_FILE, new URL(getArtifact()).getPath());
        return map;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public enum State {
        NEW_BUILD, BASELINE_CANDIDATE, BASELINE_VOTING, BASELINE_APPROVED, BASELINE_REJECTED
    }
}
