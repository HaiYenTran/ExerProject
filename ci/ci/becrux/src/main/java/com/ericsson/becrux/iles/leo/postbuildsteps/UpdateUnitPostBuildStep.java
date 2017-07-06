package com.ericsson.becrux.iles.leo.postbuildsteps;

import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.iles.leo.LeoCommunicator;
import com.ericsson.becrux.iles.leo.domain.*;
import com.ericsson.becrux.iles.leo.parameters.InitLeoParameterValue;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.List;

/**
 * Created by ebarkaw on 2016-12-14.
 */
public class UpdateUnitPostBuildStep extends Notifier {

    @DataBoundConstructor
    public UpdateUnitPostBuildStep() {

    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }


    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher,final BuildListener listener)
            throws InterruptedException, IOException {

        BuildParametersExtractor extractor = new BuildParametersExtractor(build);
        List<InitLeoParameterValue> config = extractor.getAllNwftParametersOfType(InitLeoParameterValue.class);

        listener.getLogger().println("---POST ACTION UPADTE LEO---\nSIZE\t:\t"+config.size()+"\n"+build.getResult());

        if(config.size()>0){
            InitLeoParameterValue leoConfig = config.get(config.size()-1);
            LeoCommunicator com  = new LeoCommunicator(leoConfig.getLocation());
            com.ericsson.becrux.iles.leo.domain.Job mJob = leoConfig.getUnitRequest().job;
            if(mJob==null)mJob=leoConfig.getJob();
            UnitRequest request = new UnitRequest(leoConfig.getUnitResponse());
            request.startTime=null;
            request.endTime=LeoCommunicator.nicelyFormattedTimeNow();
            request.statusType = StatusType.convertFromResult(build.getResult());
            UnitResponse r = com.updateUnit(mJob,request);
            listener.getLogger().println("---Update Unit in LEO---\n"+r);
            leoConfig.setUnitResponse(r);
            return true;
        }
        else {
            try {
                build.setDescription("Create unit needs configuration job and seting more than 0 units");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Extension
    public static final class DescriptorImpl extends
            BuildStepDescriptor<Publisher> {

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(
                final Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "LEO UPDATE POST BUILDSTEP";
        }
    }
}
