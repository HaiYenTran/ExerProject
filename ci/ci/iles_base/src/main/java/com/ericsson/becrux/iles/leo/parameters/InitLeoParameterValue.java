package com.ericsson.becrux.iles.leo.parameters;

import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.iles.leo.domain.Job;
import com.ericsson.becrux.iles.leo.domain.UnitRequest;
import com.ericsson.becrux.iles.leo.domain.UnitResponse;
import com.ericsson.becrux.iles.leo.domain.UnitType;

import java.util.HashMap;

/**
 * Created by thien.d.vu on 12/7/2016.
 */
public class InitLeoParameterValue extends NwftParameterValue implements Cloneable{
    protected final static String PARAMETER_GRROUP = "Leo";
    protected final static String PARAMETER_NAME = "Leo";
    protected final static String PARAMETER_DESCRIPTION = "This is configuration LEO";

    protected String location;
    protected UnitRequest unitRequest;
    protected UnitResponse unitResponse;
    protected Job job;
    protected HashMap<String,UnitResponse> units = new HashMap<>();

    public InitLeoParameterValue() {
        super(PARAMETER_NAME, PARAMETER_DESCRIPTION, PARAMETER_GRROUP);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UnitRequest getUnitRequest() {
        return unitRequest;
    }

    public void setUnitRequest(UnitRequest unitRequest) {
        this.unitRequest = unitRequest;
    }

    public UnitResponse getUnitResponse() {
        return unitResponse;
    }

    public Job getJob() {
        return job;
    }

    public void setUnitResponse(UnitResponse unitResponse) {
        this.unitResponse = unitResponse;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setUnit(UnitResponse unitResponse, UnitType type) {
        UnitResponse inStorage = getUnit(type);
        if(inStorage!=null){
            units.replace(type.unitTypeName,unitResponse);
        }else{
            units.put(type.unitTypeName,unitResponse);
        }
    }

    public UnitResponse getUnit(UnitType unitType){
        UnitResponse r = units.get(unitType.unitTypeName);
        return r;
    }

    public HashMap<String, UnitResponse> getUnits() {
        return units;
    }

    public void setUnits(HashMap<String, UnitResponse> units) {
        this.units = units;
    }

    public InitLeoParameterValue clone(){
        try {
            return (InitLeoParameterValue) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
