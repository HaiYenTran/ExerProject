package com.ericsson.becrux.base.common.testexec;

/**
 * Created by ematwie on 2016-12-20.
 */
public enum TestStatus {
    PASSED, SKIPPED, FAILED, TOTAL;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
