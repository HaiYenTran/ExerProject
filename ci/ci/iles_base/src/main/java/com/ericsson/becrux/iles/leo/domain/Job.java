package com.ericsson.becrux.iles.leo.domain;

import java.util.List;

public class Job {
    public Long id;
    public String ciId;
    public String loop;
    public String productName;
    public String projectName;
    public String streamName;
    public String ciStreamName;
    public String teamName;
    public String buildLocation;
    public String baseline;
    public String prevReleaseBaseline;
    public String viewName;
    public String hardwareResources;
    public String environment;
    public Integer node;
    public String type;

    public String siteName;
    public String siteDescription;

    public List changeSets;

    public Boolean hasBaseline;
    public List units;
    public List postits;
    public Integer site;
    public String loopType;

    @Override
    public String toString() {
        return "Job [id=" + id + ", ciId=" + ciId + ", loop=" + loop + ", productName=" + productName + ", projectName="
                + projectName + ", streamName=" + streamName + ", ciStreamName=" + ciStreamName + ", teamName="
                + teamName + ", buildLocation=" + buildLocation + ", baseline=" + baseline + ", prevReleaseBaseline="
                + prevReleaseBaseline + ", viewName=" + viewName + ", hardwareResources=" + hardwareResources
                + ", environment=" + environment + ", node=" + node + ", type=" + type + ", siteName=" + siteName
                + ", siteDescription=" + siteDescription + ", changeSets=" + changeSets + ", hasBaseline=" + hasBaseline
                + ", units=" + units + ", postits=" + postits + "]";
    }

}
