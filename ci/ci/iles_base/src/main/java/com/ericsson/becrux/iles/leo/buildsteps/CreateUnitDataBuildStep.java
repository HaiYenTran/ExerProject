package com.ericsson.becrux.iles.leo.buildsteps;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.iles.leo.LeoCommunicator;
import com.ericsson.becrux.iles.leo.parameters.InitLeoParameterValue;
import com.ericsson.becrux.iles.leo.parameters.InitUnitDataParameterValue;
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
public class CreateUnitDataBuildStep extends NwftBuildStep {

    @DataBoundConstructor
    public CreateUnitDataBuildStep() {

    }

    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) {
        List<InitUnitDataParameterValue> unitDataConfiguration = getAllNwftParametersOfType(build,InitUnitDataParameterValue.class);
        List<InitLeoParameterValue> leoConfiguration = getAllNwftParametersOfType(build, InitLeoParameterValue.class);

        if(unitDataConfiguration.size()>0&&leoConfiguration.size()>0){
            InitLeoParameterValue leoConfig = leoConfiguration.get(leoConfiguration.size()-1);

            boolean leoConnection = leoConfig.getLocation() != null && !leoConfig.getLocation().isEmpty();
            if(leoConnection){
                InitUnitDataParameterValue p = unitDataConfiguration.get(unitDataConfiguration.size()-1);
                LeoCommunicator com  = new LeoCommunicator(leoConfig.getLocation());
                com.createUnitData(p.getValue());

                listener.getLogger().println("---Create new UnitData in LEO---\n"+p.getValue());
                return true;
            }
            else{
                try {
                    build.setDescription("leoUrl in configuration Job doesn't set on Null or empty");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        else {
            try {
                build.setDescription("Create unit needs configuration job and UnitData");
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
            return "ILES: LEO: create value";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }

}
