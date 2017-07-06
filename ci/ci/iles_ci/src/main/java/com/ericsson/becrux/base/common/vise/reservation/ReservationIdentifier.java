package com.ericsson.becrux.base.common.vise.reservation;

import hudson.model.AbstractBuild;

import javax.annotation.Nonnull;

/**
 * Created by emiwaso on 2016-11-29.
 */
public class ReservationIdentifier {

    private final String jobName;
    private final String jobNumber;

    public ReservationIdentifier() {
        jobName = null;
        jobNumber = null;
    }

    public ReservationIdentifier(@Nonnull AbstractBuild<?, ?> build) {

        this.jobName = build.getEnvVars().get("JOB_NAME");
        this.jobNumber = build.getId();
    }

    public ReservationIdentifier(@Nonnull String jobName, @Nonnull String jobNumber) {

        if(jobName.isEmpty())
            throw new IllegalArgumentException("Job name cannot be empty");
        if(jobNumber.isEmpty())
            throw new IllegalArgumentException("Job number cannot be empty");

        this.jobName = jobName;
        this.jobNumber = jobNumber;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReservationIdentifier that = (ReservationIdentifier) o;

        if (jobName != null ? !jobName.equals(that.jobName) : that.jobName != null) return false;
        return jobNumber != null ? jobNumber.equals(that.jobNumber) : that.jobNumber == null;
    }

    @Override
    public int hashCode() {
        int result = jobName != null ? jobName.hashCode() : 0;
        result = 31 * result + (jobNumber != null ? jobNumber.hashCode() : 0);
        return result;
    }
}
