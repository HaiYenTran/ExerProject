package com.ericsson.becrux.iles.leo.domain;

import com.ericsson.becrux.base.common.loop.PhaseStatus;
import hudson.model.Result;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusType {
    public static final StatusType STARTED = new StatusType(2l, "started", 0);
    public static final StatusType FINISHED = new StatusType(1l, "finished", 2);
    public static final StatusType ERROR = new StatusType(3l, "error", 4);
    public static final StatusType FAILED = new StatusType(4l, "failed", 3);
    public static final StatusType WAITING = new StatusType(5l, "waiting", 1);
    public static final StatusType ABORTED = new StatusType(6l, "aborted", 5);
    public Long id;
    public String statusTypeName;
    public int ordinal;

    public StatusType(Long anId, String sTN, int ord) {
        id = anId;
        statusTypeName = sTN;
        ordinal = ord;
    }

    public static StatusType convertFromPhaseStatus(PhaseStatus statusBuild) {
        if (statusBuild == PhaseStatus.SUCCESS) {
            return FINISHED;
        } else if (statusBuild == PhaseStatus.FAILURE) {
            return FAILED;
        } else {
            return ERROR;
        }
    }

    public static StatusType convertFromResult(Result statusBuild) {
        if (statusBuild == Result.SUCCESS) {
            return FINISHED;
        } else if (statusBuild == Result.FAILURE) {
            return ERROR;
        } else {
            return FAILED;
        }
    }

    public
    @Nonnull
    StatusType combine(@Nonnull StatusType that) {
        if (this.ordinal < that.ordinal)
            return that;
        else
            return this;
    }

    public static List<String> generateStaticList() {
        List<String> list = new ArrayList<>();

        list.add("STARTED");
        list.add("FINISHED");
        list.add("ERROR");
        list.add("FAILED");
        list.add("WAITING");
        list.add("ABORTED");

        return list;
    }

    public static Map<String, String> generateMapListTypeName() throws Exception {
        Map<String, String> mapList = new HashMap<>();
        mapList.put("STARTED", "started");
        mapList.put("FINISHED", "finished");
        mapList.put("ERROR", "error");
        mapList.put("FAILED", "failed");
        mapList.put("WAITING", "waiting");
        mapList.put("ABORTED", "aborted");

        return mapList;
    }

    public static Map<String, Long> generateMapListId() throws Exception {
        Map<String, Long> mapList = new HashMap<>();
        mapList.put("STARTED", 2l);
        mapList.put("FINISHED", 1l);
        mapList.put("ERROR", 3l);
        mapList.put("FAILED", 4l);
        mapList.put("WAITING", 5l);
        mapList.put("ABORTED", 6l);

        return mapList;
    }

    public static Map<String, Integer> generateMapListOrdinal() throws Exception {
        Map<String, Integer> mapList = new HashMap<>();
        mapList.put("STARTED", 0);
        mapList.put("FINISHED", 2);
        mapList.put("ERROR", 4);
        mapList.put("FAILED", 3);
        mapList.put("WAITING", 1);
        mapList.put("ABORTED", 5);

        return mapList;
    }

    public boolean isWorseThan(@Nonnull StatusType that) {
        return this.ordinal > that.ordinal;
    }

    public boolean isWorseOrEqualTo(@Nonnull StatusType that) {
        return this.ordinal >= that.ordinal;
    }

    public boolean isBetterThan(@Nonnull StatusType that) {
        return this.ordinal < that.ordinal;
    }

    public boolean isBetterOrEqualTo(@Nonnull StatusType that) {
        return this.ordinal <= that.ordinal;
    }
}
