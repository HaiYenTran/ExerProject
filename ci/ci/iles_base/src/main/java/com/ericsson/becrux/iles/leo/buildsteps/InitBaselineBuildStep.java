package com.ericsson.becrux.iles.leo.buildsteps;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.iles.leo.domain.*;
import com.ericsson.becrux.iles.leo.parameters.InitBaselineParameterValue;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thien.d.vu on 12/7/2016.
 */
public class InitBaselineBuildStep extends NwftBuildStep {

    // param
    public List<ProductVersionHelper> listBaseline;

    public List<ProductVersionHelper> getListBaseline() {
        return listBaseline;
    }

    public void setListBaseline(List<ProductVersionHelper> listBaseline) {
        this.listBaseline = listBaseline;
    }


    @DataBoundConstructor
    public InitBaselineBuildStep(List<ProductVersionHelper> listBaseline) {
        this.listBaseline = listBaseline;
    }

    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) {
        List<ProductVersion> productVersionsList = new ArrayList<>();
        if (listBaseline != null && !listBaseline.isEmpty()) {

            List<InitBaselineParameterValue> listBaselinie = getAllNwftParametersOfType(build,InitBaselineParameterValue.class);

            for (ProductVersionHelper baseline : listBaseline) {
                listener.getLogger().println("[DEBUG] Product Version: " + baseline.getProductVersion());
                listener.getLogger().println("[DEBUG] ReleaseId: " + baseline.getReleaseId());
                listener.getLogger().println("[DEBUG] Description: " + baseline.getDescription());
                listener.getLogger().println("[DEBUG] Product Name: " + baseline.getProductName());
                listener.getLogger().println("[DEBUG] Signum: " + baseline.getSignum());

                ProductVersion productVersions = new ProductVersion();
                Product product = new Product();
                ReleaseType releaseType = new ReleaseType();

                product.name = baseline.getProductName();
                product.description = baseline.getDescription();

                releaseType.id = baseline.getReleaseId();

                productVersions.name = baseline.getProductVersion();
                productVersions.product = product;
                productVersions.releaseType = releaseType;

                productVersionsList.add(productVersions);
            }

            if(listBaselinie.size()==0){
                InitBaselineParameterValue param = new InitBaselineParameterValue(productVersionsList);
                addNwftBuildParameter(build,param);
            }
            else {
                listBaselinie.get(0).setValue(productVersionsList);
            }
        }

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
            return "ILES: LEO: initialize Baseline";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
