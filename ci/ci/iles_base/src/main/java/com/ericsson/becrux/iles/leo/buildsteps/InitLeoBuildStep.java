package com.ericsson.becrux.iles.leo.buildsteps;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
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

public class InitLeoBuildStep extends NwftBuildStep {

    String location ;

    @DataBoundConstructor
    public InitLeoBuildStep(String location) {
        this.location = location;
    }

    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) {
        List <InitLeoParameterValue> lastJobList = getAllNwftParametersOfType(build,InitLeoParameterValue.class);

        if(lastJobList.size()==0) {
            boolean leoConnection = location != null && !location.isEmpty();
            if(leoConnection) {
                InitLeoParameterValue param = new InitLeoParameterValue();
                param.setLocation(location);
                this.addNwftBuildParameter(build, param);
                listener.getLogger().println("---Initialized configuration LEO---\nLocation:\t" + location + "\n");
                return true;
            }
            else{
                try {
                    build.setDescription("Wrong leo URL");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        else if(lastJobList.size()>0){
            try {
                build.setDescription("Initialization LEO buildstep you can Buse only one time");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
            return "ILES: LEO: initialize LEO";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
