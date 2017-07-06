package com.ericsson.becrux.iles.loop;

import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.iles.leo.domain.StatusType;
import hudson.model.Result;

/**
 * Class used to standardize statuses occurring in CI loop:<ul>
 * <li>Jenkins build status</li>
 * <li>Eiffel status</li>
 * <li>Leo job status</li></ul>
 */
public class StatusHandler {

    private PhaseStatus eiffelStatus;
    private Result jenkinsStatus;
    private StatusType leoStatus;

    /**
     * Create StatusHandler, set "ongoing" statuses
     * (or "success" if "ongoing" is not defined).
     */
    public StatusHandler() {
        this.eiffelStatus = PhaseStatus.PROGRESS;
        this.jenkinsStatus = Result.SUCCESS;
        this.leoStatus = StatusType.STARTED;
    }

    private void set(PhaseStatus eiffelStatus, Result jenkinsStatus, StatusType leoStatus) {
        setEiffelStatus(eiffelStatus);
        setJenkinsStatus(jenkinsStatus);
        setLeoStatus(leoStatus);
    }

    private void setAfterBuild(Result result, boolean last) {
        if (result.equals(Result.SUCCESS)) {
            set(last ? PhaseStatus.SUCCESS : PhaseStatus.PROGRESS, Result.SUCCESS, StatusType.FINISHED);

        } else if (result.equals(Result.UNSTABLE)) {
            // TODO: set eiffel status when downstream job is unstable
            // (maybe new status should be created)
            set(PhaseStatus.FAILURE, Result.UNSTABLE, StatusType.FINISHED);

        } else if (result.equals(Result.ABORTED)) {
            set(PhaseStatus.ERROR, Result.FAILURE, StatusType.ABORTED);

        } else if (result.equals(Result.FAILURE)) {
            set(PhaseStatus.FAILURE, Result.FAILURE, StatusType.FAILED);

        } else {
            set(PhaseStatus.ERROR, Result.FAILURE, StatusType.ERROR);
        }
    }

    public PhaseStatus getEiffelStatus() {
        return eiffelStatus;
    }

    /**
     * Set, but do not overwrite the worse one.
     *
     * @param eiffelStatus Eiffel status
     */
    public void setEiffelStatus(PhaseStatus eiffelStatus) {
        this.eiffelStatus = this.eiffelStatus.combine(eiffelStatus);
    }

    public Result getJenkinsStatus() {
        return jenkinsStatus;
    }

    /**
     * Set, but do not overwrite the worse one.
     *
     * @param jenkinsStatus Jenkins build status
     */
    public void setJenkinsStatus(Result jenkinsStatus) {
        this.jenkinsStatus = this.jenkinsStatus.combine(jenkinsStatus);
    }

    public StatusType getLeoStatus() {
        return leoStatus;
    }

    public void setLeoStatus(StatusType leoStatus) {
        this.leoStatus = leoStatus;
    }

    /**
     * Check if the Jenkins build status equals {@link Result#SUCCESS}.
     *
     * @return true or false
     */
    public boolean isJenkinsStatusSuccess() {
        return jenkinsStatus.equals(Result.SUCCESS);
    }

    /**
     * Set the most adequate statuses for {@link Result#ABORTED}.
     */
    public void setAbortedStatuses() {
        set(PhaseStatus.ERROR, Result.ABORTED, StatusType.ABORTED);
    }

    /**
     * Set all statuses based on the {@link Result}.
     * If the {@link Result#SUCCESS} occurred, set Eiffel status as
     * {@link PhaseStatus#PROGRESS}.
     *
     * @param result Jenkins build result
     */
    public void setStatusesAfterBuild(Result result) {
        setAfterBuild(result, false);
    }

    /**
     * Set all statuses based on the {@link Result}.
     * If the {@link Result#SUCCESS} occurred, set Eiffel status as
     * {@link PhaseStatus#SUCCESS}.
     *
     * @param result Jenkins build result
     */
    public void setFinalStatusesAfterBuild(Result result) {
        setAfterBuild(result, true);
    }

}
