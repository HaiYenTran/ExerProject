package com.ericsson.becrux.iles.leo.buildsteps;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.iles.leo.LeoCommunicator;
import com.ericsson.becrux.iles.leo.domain.Job;
import com.ericsson.becrux.iles.leo.domain.UnitResponse;
import com.ericsson.becrux.iles.leo.domain.UnitType;
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
public class CreateJobBuildStep extends NwftBuildStep {

    @DataBoundConstructor
    public CreateJobBuildStep() {

    }

    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) {
        List<InitLeoParameterValue> leoConfiguration = getAllNwftParametersOfType(build, InitLeoParameterValue.class);

        if(leoConfiguration.size()>0){
            InitLeoParameterValue leoConfig = leoConfiguration.get(leoConfiguration.size()-1);

            LeoCommunicator com = new LeoCommunicator(leoConfig.getLocation());
            Job job = com.createJob(leoConfig.getJob());
            UnitResponse rootUnit = com.createRootUnit(job);
            leoConfig.setJob(job);

            leoConfig.setUnitResponse(rootUnit);
            leoConfig.setUnit(rootUnit, UnitType.CI_TOP);
            return true;
        }
        else {
            try {
                build.setDescription("Create job needs configuration job");
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
            return "ILES: LEO: create job";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }

}
