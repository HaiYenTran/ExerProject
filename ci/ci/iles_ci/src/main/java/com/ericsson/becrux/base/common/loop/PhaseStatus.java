package com.ericsson.becrux.base.common.loop;

import javax.annotation.Nonnull;

public enum PhaseStatus {
    SUCCESS(2),
    PROGRESS(1),
    RETRY(3),
    FAILURE(4),
    ERROR(5),
    INCONCLUSIVE(6),
    SKIPPED(7),
    UNSTABLE(8),
    QUEUED(0);

    private int ordinal;

    PhaseStatus(int ordinal) {
        this.ordinal = ordinal;
    }

    public
    @Nonnull
    PhaseStatus combine(@Nonnull PhaseStatus that) {
        if (this.ordinal < that.ordinal)
            return that;
        else
            return this;
    }

    public boolean isWorseThan(@Nonnull PhaseStatus that) {
        return this.ordinal > that.ordinal;
    }

    public boolean isWorseOrEqualTo(@Nonnull PhaseStatus that) {
        return this.ordinal >= that.ordinal;
    }

    public boolean isBetterThan(@Nonnull PhaseStatus that) {
        return this.ordinal < that.ordinal;
    }

    public boolean isBetterOrEqualTo(@Nonnull PhaseStatus that) {
        return this.ordinal <= that.ordinal;
    }
}
