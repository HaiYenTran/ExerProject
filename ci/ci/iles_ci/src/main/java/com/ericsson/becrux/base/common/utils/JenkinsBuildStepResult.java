package com.ericsson.becrux.base.common.utils;

import java.util.List;

/**
 * Contain the result of running {@link JenkinsBuildStepResult}.
 */
public class JenkinsBuildStepResult {

    private boolean isSuccess;
    private List<Exception> exceptions;

    /**
     * Constructor.
     * @param isSuccess
     * @param exceptions
     */
    public JenkinsBuildStepResult (boolean isSuccess, List<Exception> exceptions) {
        this.isSuccess = isSuccess;
        this.exceptions = exceptions;
    }

    /**
     * Get result status.
     * @return
     */
    public boolean isSuccess() {
        if(exceptions != null && exceptions.size() != 0) {
            isSuccess = false;
        }

        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }
}
