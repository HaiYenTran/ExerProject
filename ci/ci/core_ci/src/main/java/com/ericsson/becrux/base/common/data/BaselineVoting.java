package com.ericsson.becrux.base.common.data;

import com.ericsson.becrux.base.common.core.NwftDownstreamJob;
import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * TODO: Generic for inherit
 */
public class BaselineVoting {

    private Boolean vote;
    private String signum;
    private String comment;
//    private BTFEvent btf;
    private Date deadline;
    private boolean notifiedTimeout = false;
    private NwftDownstreamJob timeoutJob;

//    public BaselineVoting(Date deadline, BTFEvent btf) {
//        super();
//        this.deadline = deadline;
//        this.btf = btf;
//    }

    public static BaselineVoting getInstancefromJsonString(String jsonTxt) {
        return (BaselineVoting) (new GsonBuilder().create().fromJson(jsonTxt, BaselineVoting.class));
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public boolean isNotifiedTimeout() {
        return notifiedTimeout;
    }

    public void setNotifiedTimeout(boolean notifiedTimeout) {
        this.notifiedTimeout = notifiedTimeout;
    }

    public VotingResult getResult() {
        if (deadline != null && beforeDeadline()) {
            return VotingResult.TIMEOUT;
        }
        if (vote == null)
            return VotingResult.IN_PROGRESS;
        else if (vote == false)
            return VotingResult.REJECTED;

        return VotingResult.APPROVED;
    }

    public Boolean getVote() {
        return vote;
    }

    public void setVote(Boolean vote) {
        this.vote = vote;
    }

    public void setVoteSafe(Boolean vote) {
        if (vote != null)
            setVote(vote);
    }

    public String getSignum() {
        return signum;
    }

    public void setSignum(String signum) {
        this.signum = signum;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

//    public BTFEvent getBtf() {
//        return btf;
//    }
//
//    public void setBtf(BTFEvent btf) {
//        this.btf = btf;
//    }

    private boolean beforeDeadline() {
        Date now = new Date();
        if (deadline.compareTo(now) > 0) {
            if (deadline.getYear() == now.getYear() &&
                    deadline.getMonth() == now.getMonth() &&
                    deadline.getDay() == now.getDay() &&
                    deadline.getHours() == now.getHours() &&
                    deadline.getMinutes() == now.getMinutes()) {
                return true;
            } else {
                return false;
            }
        } else return true;
    }

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

    public void setTimeoutJob(NwftDownstreamJob timeoutJob) {
        this.timeoutJob = timeoutJob;
    }

    public NwftDownstreamJob getTimeoutJob() {
        return timeoutJob;
    }

    public enum VotingResult {
        IN_PROGRESS, APPROVED, REJECTED, TIMEOUT
    }

}
