package com.ericsson.becrux.iles.leo.buildsteps;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.iles.leo.domain.Job;
import com.ericsson.becrux.iles.leo.parameters.InitLeoParameterValue;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.List;


/**
 * Created by ebarkaw on 2016-12-02.
 */
public class InitLeoJobBuildStep extends NwftBuildStep {

    private String id;
    private String ciId;
    private String loop;
    private String productName;
    private String projectName;
    private String streamName;
    private String ciStreamName;
    private String teamName;
    private String buildLocation;
    private String baseline;
    private String prevReleaseBaseline;
    private String viewName;
    private Integer node;
    private String type;
    private Integer site;
    private String loopType;

	@DataBoundConstructor
    public InitLeoJobBuildStep(String id, String ciId, String loop, String productName,
                               String projectName, String streamName, String ciStreamName, String teamName,
                               String buildLocation, String baseline, String prevReleaseBaseline, String viewName,
                               Integer node, String type, Integer site, String loopType) {
        this.id=id;
        this.ciId = ciId;
        this.loop = loop;
        this.productName = productName;
        this.projectName = projectName;
        this.streamName = streamName;
        this.ciStreamName = ciStreamName;
        this.teamName = teamName;
        this.buildLocation = buildLocation;
        this.baseline = baseline;
        this.prevReleaseBaseline = prevReleaseBaseline;
        this.viewName = viewName;
        this.node = node;
        this.type = type;
        this.site = site;
        this.loopType = loopType;
    }

    public String getId() {return id; }

    public void setId(String id) {this.id = id;}

    public String getCiId() { return ciId; }

    public void setCiId(String ciId) { this.ciId = ciId; }

    public String getLoop() { return loop; }

    public void setLoop(String loop) { this.loop = loop; }

    public String getProductName() { return productName; }

    public void setProductName(String productName) { this.productName = productName; }

    public String getProjectName() { return projectName; }

    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getStreamName() { return streamName; }

    public void setStreamName(String streamName) { this.streamName = streamName; }

    public String getCiStreamName() { return ciStreamName; }

    public void setCiStreamName(String ciStreamName) { this.ciStreamName = ciStreamName; }

    public String getTeamName() { return teamName; }

    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getBuildLocation() { return buildLocation; }

    public void setBuildLocation(String buildLocation) { this.buildLocation = buildLocation; }

    public String getBaseline() { return baseline; }

    public void setBaseline(String baseline) { this.baseline = baseline; }

    public String getPrevReleaseBaseline() { return prevReleaseBaseline; }

    public void setPrevReleaseBaseline(String prevReleaseBaseline) {
        this.prevReleaseBaseline = prevReleaseBaseline;
    }

    public String getViewName() { return viewName; }

    public void setViewName(String viewName) { this.viewName = viewName; }

    public Integer getNode() { return node; }

    public void setNode(Integer node) { this.node = node; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public Integer getSite() { return site; }

    public void setSite(Integer site) { this.site = site; }

    public String getLoopType() { return loopType; }

    public void setLoopType(String loopType) { this.loopType = loopType; }

    @Override
    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) {

        List<InitLeoParameterValue> leoConfiguration = getAllNwftParametersOfType(build,InitLeoParameterValue.class);
        if(leoConfiguration.size()==1) {
            InitLeoParameterValue leo = leoConfiguration.get(0);

                Job job = leo.getJob();
                if(job==null)job= new Job();
                if(this.id!=null && !this.id.isEmpty()) job.id=new Long(this.id);
                job.ciId=this.ciId;
                job.loop=this.loop;
                job.productName=this.productName;
                job.projectName=this.projectName;
                job.streamName=this.streamName;
                job.ciStreamName=this.ciStreamName;
                job.teamName=this.teamName;
                job.buildLocation=this.buildLocation;
                job.baseline=this.baseline;
                job.prevReleaseBaseline=this.prevReleaseBaseline;
                job.viewName=this.viewName;
                job.node=this.node;
                job.type=this.type;
                job.site=this.site;
                job.loopType=this.loopType;

                leo.setJob(job);
                listener.getLogger().println("---Initialized new JOB in LEO---\n"+job);

            return true;

        }
        else
        {
            try {
                build.setDescription("Leo configuration doesn't exist");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "ILES: LEO: Job initialization";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
