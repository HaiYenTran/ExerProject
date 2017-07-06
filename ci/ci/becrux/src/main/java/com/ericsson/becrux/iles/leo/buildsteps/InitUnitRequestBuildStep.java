package com.ericsson.becrux.iles.leo.buildsteps;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.iles.leo.LeoCommunicator;
import com.ericsson.becrux.iles.leo.domain.StatusType;
import com.ericsson.becrux.iles.leo.domain.UnitRequest;
import com.ericsson.becrux.iles.leo.domain.UnitType;
import com.ericsson.becrux.iles.leo.parameters.InitLeoParameterValue;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;


/**
 * Created by ebarkaw on 2016-12-02.
 */
public class InitUnitRequestBuildStep extends NwftBuildStep {

    //params
    private String identifyName;
    private String label;
    private String unitType;
    private String statusType;
    private boolean setStartTime;
    private boolean setStopTime;

    private String unitTypeList;
    private String statusTypeList;

    public String getIdentifyName() {
        return identifyName;
    }

    public void setIdentifyName(String identifyName) {
        this.identifyName = identifyName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    public boolean isSetStartTime() {
        return setStartTime;
    }

    public void setSetStartTime(boolean setStartTime) {
        this.setStartTime = setStartTime;
    }

    public boolean isSetStopTime() {
        return setStopTime;
    }

    public void setSetStopTime(boolean setStopTime) {
        this.setStopTime = setStopTime;
    }


    @DataBoundConstructor
    public InitUnitRequestBuildStep(@Nonnull String identifyName, String label,
                                    @Nonnull String unitType, @Nonnull String statusType, boolean setStartTime,
                                    boolean setStopTime) {
        this.identifyName = identifyName;
        this.label = label;
        this.unitType = unitType;
        this.statusType=statusType;
        this.setStartTime = setStartTime;
        this.setStopTime = setStopTime;
    }

    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) {
        listener.getLogger().println("[DEBUG] identifyName: " + this.identifyName);
        listener.getLogger().println("[DEBUG] label: " + this.label);
        listener.getLogger().println("[DEBUG] unitType: " + this.unitType);
        listener.getLogger().println("[DEBUG] statusType: " + this.statusType);
        listener.getLogger().println("[DEBUG] setStartTime: " + this.setStartTime);
        listener.getLogger().println("[DEBUG] setStopTime: " + this.setStopTime);

        List<InitLeoParameterValue> leoConfiguration = getAllNwftParametersOfType(build, InitLeoParameterValue.class);
        if(leoConfiguration.size()==1){
            InitLeoParameterValue leoConfig = leoConfiguration.get(leoConfiguration.size()-1);

            UnitRequest unitRequest = new UnitRequest();
            if(leoConfig.getJob()!=null){
                unitRequest.job = leoConfig.getJob();
                if(leoConfig.getUnitResponse()!=null){
                    unitRequest = new UnitRequest(leoConfig.getUnitResponse());
                    unitRequest.job = leoConfig.getJob();
                    if (!this.setStopTime) {
                        unitRequest.parent = leoConfig.getUnitResponse();
                    }
                }
                unitRequest.identityName = this.identifyName;
                if (this.setStartTime) {
                    unitRequest.startTime = LeoCommunicator.nicelyFormattedTimeNow();
                    unitRequest.endTime = null;
                }
                if (this.setStopTime) {
                    unitRequest.startTime = null;
                    unitRequest.endTime = LeoCommunicator.nicelyFormattedTimeNow();
                }
                unitRequest.label = this.label;
                try {
                    Map<String, Long> unitTypeMapListId = UnitType.generateMapListId();
                    Map<String, String> unitTypeMapListTypeName = UnitType.generateMapListTypeName();
                    long getUnitTypeId = unitTypeMapListId.get(this.unitType);
                    String getUnitTypeName = unitTypeMapListTypeName.get(this.unitType);
                    unitRequest.unitType = new UnitType(getUnitTypeId, getUnitTypeName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Map<String, Long> statusTypeMapListId = StatusType.generateMapListId();
                    Map<String, String> statusTypeMapListTypeName = StatusType.generateMapListTypeName();
                    Map<String, Integer> statusTypeMapListOrdinal = StatusType.generateMapListOrdinal();
                    long getStatusTypeId = statusTypeMapListId.get(this.statusType);
                    String getStatusTypeName = statusTypeMapListTypeName.get(this.statusType);
                    int getStatusTypeOrdinal = statusTypeMapListOrdinal.get(this.statusType);
                    unitRequest.statusType = new StatusType(getStatusTypeId, getStatusTypeName, getStatusTypeOrdinal);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                listener.getLogger().println("[DEBUG] unit: " + unitRequest);
                listener.getLogger().println("[DEBUG] unitType: " + unitRequest.unitType);
                listener.getLogger().println("[DEBUG] statusType: " + unitRequest.statusType);

                leoConfig.setUnitRequest(unitRequest);
                return true;
            }
            else{
                try {
                    build.setDescription("Leo Job doesn't exist");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
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

        public ListBoxModel doFillUnitTypeItems() {
            ListBoxModel items = new ListBoxModel();
            List<String> list = UnitType.generateStaticList();
            for (String item : list) {
                items.add(item.toString());
            }
            return items;
        }

        public ListBoxModel doFillStatusTypeItems() {
            ListBoxModel items = new ListBoxModel();
            List<String> list = StatusType.generateStaticList();
            for (String item : list) {
                items.add(item.toString());
            }
            return items;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "ILES: LEO: initialize Leo UnitRequest";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
