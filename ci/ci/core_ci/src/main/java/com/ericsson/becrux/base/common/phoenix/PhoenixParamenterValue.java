package com.ericsson.becrux.base.common.phoenix;

import com.ericsson.becrux.base.common.core.NwftParameterValue;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Build Parameter for Phoenix Build Steep.
 */
public class PhoenixParamenterValue extends NwftParameterValue {

    protected final static String PARAMETER_GRROUP = "Phoenix";
    protected final static String PARAMETER_NAME = "Phoenix Config Properties";
    protected final static String PARAMETER_DESCRIPTION = "This is configuration for Phoenix";

    protected Map<String, String> value;

    public PhoenixParamenterValue(@Nonnull Map<String, String> value) {
        super(PARAMETER_NAME, PARAMETER_DESCRIPTION, PARAMETER_GRROUP);
        this.value = value;
    }

    @Override
    public Map<String, String> getValue() {
        return value;
    }

    public void setValue(Map<String, String> value) {
        this.value = value;
    }


}
