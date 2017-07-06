package com.ericsson.becrux.iles.leo.domain;

import java.util.List;

public abstract class Unit {
    public Long id;

    public String identityName;
    public String startTime;
    public String endTime;
    public String duration;
    public String label;

    public UnitType unitType;
    public String statusTypeName;
    public String statusTypeDescription;

    /*
     * Read-only
     */
    public Boolean hasUnitData;
    public Integer total;
    public Integer executed;
    public Integer succeeded;
    public Integer failed;
    /*
     * Read-only
     */
    public Double successRate;
    public Integer unitDataErrors;
    public List failedUnitTestCases;
    public StatusType statusType;


}
