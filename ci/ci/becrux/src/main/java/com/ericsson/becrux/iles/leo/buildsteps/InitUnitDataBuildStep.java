package com.ericsson.becrux.iles.leo.buildsteps;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.iles.leo.domain.UnitData;
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

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;


/**
 * Created by ebarkaw on 2016-12-02.
 */
public class InitUnitDataBuildStep extends NwftBuildStep {

    private Integer total;
    private Integer executed;
    private Integer succeeded;
    private Integer failed;
    private Integer unitDataErrors;

	@DataBoundConstructor
    public InitUnitDataBuildStep(@Nonnull Integer total, @Nonnull Integer executed,
                                 @Nonnull Integer succeeded, @Nonnull Integer failed,
                                 @Nonnull Integer unitDataErrors) {
        this.total=total;
        this.executed=executed;
        this.succeeded=succeeded;
        this.failed=failed;
        this.unitDataErrors=unitDataErrors;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getExecuted() {
        return executed;
    }

    public void setExecuted(Integer executed) {
        this.executed = executed;
    }

    public Integer getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(Integer succeeded) {
        this.succeeded = succeeded;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getUnitDataErrors() {
        return unitDataErrors;
    }

    public void setUnitDataErrors(Integer unitDataErrors) {
        this.unitDataErrors = unitDataErrors;
    }

    @Override
    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) {
        List<InitLeoParameterValue> leoConfiguration = getAllNwftParametersOfType(build, InitLeoParameterValue.class);

        if(leoConfiguration.size()==1){
            InitLeoParameterValue leoConfig = leoConfiguration.get(leoConfiguration.size()-1);
            List <InitUnitDataParameterValue> lastUnitDataList = getAllNwftParametersOfType(build,InitUnitDataParameterValue.class);
            InitUnitDataParameterValue lastUnitData = null;
            if(lastUnitDataList.size()>0) lastUnitData = lastUnitDataList.get(lastUnitDataList.size()-1);

            if(lastUnitData==null){
                    UnitData unitData = new UnitData();
                    unitData.total=this.total;
                    unitData.executed=this.executed;
                    unitData.succeeded=this.succeeded;
                    unitData.failed=this.failed;
                    unitData.unitDataErrors=this.unitDataErrors;
                    unitData.unit=leoConfig.getUnitResponse();

                    InitUnitDataParameterValue param = new InitUnitDataParameterValue(unitData);
                    this.addNwftBuildParameter(build, param);

                    listener.getLogger().println("---Initialized new UnitData in LEO---\n"+unitData);
            }
            else
                {
                    lastUnitData.getValue().total=this.total;
                    lastUnitData.getValue().executed=this.executed;
                    lastUnitData.getValue().succeeded=this.succeeded;
                    lastUnitData.getValue().failed=this.failed;
                    lastUnitData.getValue().unitDataErrors=this.unitDataErrors;
                    lastUnitData.getValue().unit=leoConfig.getUnitResponse();

                    listener.getLogger().println("---Updated UnitData in LEO---\n"+lastUnitData.getValue());
                }

                return true;
        }
        else {
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
            return "ILES: LEO: UnitData initialization";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
