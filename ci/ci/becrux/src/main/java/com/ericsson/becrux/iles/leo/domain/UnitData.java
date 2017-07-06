package com.ericsson.becrux.iles.leo.domain;

public class UnitData {

    public Integer total;
    public Integer executed;
    public Integer succeeded;
    public Integer failed;
    public Integer unitDataErrors;
    public Unit unit;

    @Override
    public String toString() {
        return "Total:"+total+"\tExecuted:"+executed+"\tSucceeded:"+succeeded+"\tFailed:"+failed+"\tUnitDataErrors:"+unitDataErrors;
    }
}
