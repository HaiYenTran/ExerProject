package com.ericsson.becrux.base.common.loop;

import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.base.common.testexec.TestStatus;

import java.util.Map;

/**
 * Created by xthengu on 2017-03-20.
 */
public class ResultParameterValue extends NwftParameterValue {

    public final static String group = "Processing Result";
    private PhaseStatus processStatus;
    private Map<String, String> details;
    private Map<TestStatus, Integer> testScore;

    public ResultParameterValue(String processType, PhaseStatus processStatus, Map<String, String> details, Map<TestStatus, Integer> testScore){
        super(processType, group);
        this.processStatus = processStatus;
        this.details = details;
        this.testScore = testScore;
    }

    public ResultParameterValue(String processType, PhaseStatus processStatus) {
        super(processType, group);
        this.processStatus = processStatus;
    }

    @Override
    public String getValue() {
        StringBuilder stringResult = new StringBuilder();
        stringResult.append("[Name:").append(this.name).append("] ");
        stringResult.append("[Status:").append(this.processStatus).append("] ");
        stringResult.append("[Detail: ");
        if (details != null) {
            details.forEach((k,v) -> stringResult.append("(").append(k).append(":").append(v).append(") "));
        }
        stringResult.append("] ");
        stringResult.append("[Result: ");
        if (testScore != null) {
            testScore.forEach((k,v) -> stringResult.append("(").append(k).append(":").append(v).append(") "));
        }
        stringResult.append("] ");

        return stringResult.toString();
    }

    public Map<TestStatus, Integer> getTestScore() {
        return testScore;
    }

    public void setTestScore(Map<TestStatus, Integer> testScore) {
        this.testScore = testScore;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public PhaseStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(PhaseStatus processStatus) {
        this.processStatus = processStatus;
    }
}
