package com.ericsson.becrux.iles.leo.domain;

public class UnitRequest extends Unit {

    public Job job;
    public Unit parent;

    public UnitRequest() {
    }

    public UnitRequest(UnitResponse unitResponse) {
        this.id=unitResponse.id;
    }

    @Override
    public String toString() {
        return "UnitRequest [job=" + job + ", parent=" + parent + ", id=" + id + ", identityName=" + identityName
                + ", startTime=" + startTime + ", endTime=" + endTime + ", duration=" + duration + ", label=" + label
                + ", unitType=" + unitType + ", statusTypeName=" + statusTypeName + ", statusTypeDescription="
                + statusTypeDescription + ", hasUnitData=" + hasUnitData + ", total=" + total + ", executed=" + executed
                + ", succeeded=" + succeeded + ", failed=" + failed + ", successRate=" + successRate
                + ", unitDataErrors=" + unitDataErrors + ", failedUnitTestCases=" + failedUnitTestCases
                + ", statusType=" + statusType + "]";
    }

}
