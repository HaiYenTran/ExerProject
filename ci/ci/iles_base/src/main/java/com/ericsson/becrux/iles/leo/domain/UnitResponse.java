package com.ericsson.becrux.iles.leo.domain;

public class UnitResponse extends Unit {
    public Long parentId;
    public Long jobId;

    public UnitResponse(UnitRequest request){
        this.id=request.id;
    }

    public UnitResponse(){}

    @Override
    public String toString() {
        return "UnitResponse [parentId=" + parentId + ", jobId=" + jobId + ", id=" + id + ", identityName="
                + identityName + ", startTime=" + startTime + ", endTime=" + endTime + ", duration=" + duration
                + ", label=" + label + ", unitType=" + unitType + ", statusTypeName=" + statusTypeName
                + ", statusTypeDescription=" + statusTypeDescription + ", hasUnitData=" + hasUnitData + ", total="
                + total + ", executed=" + executed + ", succeeded=" + succeeded + ", failed=" + failed
                + ", successRate=" + successRate + ", unitDataErrors=" + unitDataErrors + ", failedUnitTestCases="
                + failedUnitTestCases + ", statusType=" + statusType + "]";
    }

    public void setUnit(Unit unit) {

        id = unit.id;
    }
}
