package com.ericsson.becrux.iles.leo.postbuildsteps;

import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.iles.leo.LeoCommunicator;
import com.ericsson.becrux.iles.leo.domain.StatusType;
import com.ericsson.becrux.iles.leo.domain.UnitRequest;
import com.ericsson.becrux.iles.leo.domain.UnitType;
import com.ericsson.becrux.iles.leo.parameters.InitLeoParameterValue;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.List;

/**
 * Created by ebarkaw on 2016-12-14.
 */
public class UpdateProvisioningPostBuildStep extends Notifier {

    @DataBoundConstructor
    public UpdateProvisioningPostBuildStep() {

    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }


    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher,final BuildListener listener)
            throws InterruptedException, IOException {

        Result result = build.getResult();
        if(result!=Result.SUCCESS){
            BuildParametersExtractor extractor = new BuildParametersExtractor(build);
            List<InitLeoParameterValue> config = extractor.getAllNwftParametersOfType(InitLeoParameterValue.class);

            listener.getLogger().println("---POST ACTION UPADTE LEO---\nSIZE\t:\t"+config.size()+"\n"+build.getResult());

            if(config.size()>0){
                InitLeoParameterValue leoConfig = config.get(config.size()-1);
                LeoCommunicator com  = new LeoCommunicator(leoConfig.getLocation());
                UnitRequest request = new UnitRequest();
                request.job = leoConfig.getJob();
                request.endTime=LeoCommunicator.nicelyFormattedTimeNow();
                request.startTime=LeoCommunicator.nicelyFormattedTimeNow();
                request.statusType=StatusType.convertFromResult(result);
                request.unitType= UnitType.PROVISIONING;
                request.identityName="Provisioning";
                request.label=" ";
                request.parent=leoConfig.getUnit(UnitType.PROVISIONING);
                com.createUnit(request);
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
        return true;
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
            return "LEO PROVISIONING";
        }
    }
}
