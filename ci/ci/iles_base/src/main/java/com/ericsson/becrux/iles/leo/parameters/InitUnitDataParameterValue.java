package com.ericsson.becrux.iles.leo.parameters;

import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.iles.leo.domain.UnitData;

import javax.annotation.Nonnull;

/**
 * Created by ebarkaw on 2016-12-02.
 */
public class InitUnitDataParameterValue extends NwftParameterValue {

    protected final static String PARAMETER_GRROUP = "Leo UnitData";
    protected final static String PARAMETER_NAME = "Leo UnitData";
    protected final static String PARAMETER_DESCRIPTION = "This is configuration UnitData in LEO";

    protected UnitData value;

    public InitUnitDataParameterValue(@Nonnull UnitData value) {
        super(PARAMETER_NAME, PARAMETER_DESCRIPTION, PARAMETER_GRROUP);
        this.value = value;
    }

    @Override
    public UnitData getValue() {
        return value;
    }

    public void setValue(UnitData unitData) {
        this.value = unitData;
    }

}
