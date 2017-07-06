package com.ericsson.becrux.iles.leo.buildsteps;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.iles.leo.LeoCommunicator;
import com.ericsson.becrux.iles.leo.domain.StatusType;
import com.ericsson.becrux.iles.leo.domain.UnitRequest;
import com.ericsson.becrux.iles.leo.domain.UnitType;
import com.ericsson.becrux.iles.leo.parameters.InitLeoParameterValue;
import com.ericsson.becrux.iles.data.IlesComponentFactory;
import com.ericsson.becrux.iles.leo.parameters.NodesLeoParameterValue;
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
public class CreateAllUnitsForNodesBuildStep extends NwftBuildStep {

    @DataBoundConstructor
    public CreateAllUnitsForNodesBuildStep() {
    }

    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) {
        List<InitLeoParameterValue> leoConfiguration = getAllNwftParametersOfType(build, InitLeoParameterValue.class);
        if(leoConfiguration.size()>0){
            InitLeoParameterValue leoConfig = leoConfiguration.get(leoConfiguration.size()-1);
                LeoCommunicator com  = new LeoCommunicator(leoConfig.getLocation());

                NodesLeoParameterValue param = new NodesLeoParameterValue();
                for(String n : IlesComponentFactory.getInstance().getRegisteredClassNames()){
                    UnitRequest r = new UnitRequest();
                    r.identityName = n;
                    r.label = "";
                    r.unitType = UnitType.NODE_INSTALLATION;
                    r.statusType = StatusType.STARTED;
                    r.startTime = LeoCommunicator.nicelyFormattedTimeNow();
                    r.endTime = null;
                    r.parent = leoConfig.getUnit(UnitType.INSTALLATION);
                    r.job = leoConfig.getJob();
                    param.addNode(n,com.createUnit(r));
                }

                addNwftBuildParameter(build,param);
                listener.getLogger().println("---Create new Nodes Unit in LEO---\n");
                return true;
        }
        else {
            try {
                build.setDescription("Create units needs storage and leo");
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
            return "ILES: LEO: ILES create all units for Nodes";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }

}
