package com.ericsson.becrux.iles.leo.buildsteps;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
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

/**
 * Created by ebarkaw on 2016-12-02.
 */
public class CreateLeoStructareWorkerBuildStep extends NwftBuildStep {

    @DataBoundConstructor
    public CreateLeoStructareWorkerBuildStep() {

    }

    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) {

        new InitLeoBuildStep("http://esekiws5503.rnd.ki.sw.ericsson.se:8084").perform(build, launcher, listener);
        new InitLeoJobBuildStep(null,"job","Legacy Loop","ILES 0.2","ILES","CI_development","ciStreamName",
                "Penguins","https://fem003-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/job/flow-sfl/644/",
                "baseline","prevReleaseBaseline","viewName",17,"sv",1,"Legacy Loop").perform(build, launcher, listener);
        new CreateJobBuildStep().perform(build, launcher, listener);
        new InitUnitRequestBuildStep("Installation","","INSTALLATION","STARTED",true,false).perform(build, launcher, listener);
        new CreateUnitBuildStep().perform(build, launcher, listener);
        new CreateAllUnitsForNodesBuildStep().perform(build, launcher, listener);
        return true;
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
            return "ILES: LEO: ILES: Worker init";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }

}
