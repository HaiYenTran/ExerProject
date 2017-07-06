package com.ericsson.becrux.iles.leo.parameters;

import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.iles.leo.domain.ProductVersion;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by thien.d.vu on 12/7/2016.
 */
public class InitBaselineParameterValue extends NwftParameterValue {

    protected final static String PARAMETER_GRROUP = "Leo Baselinie";
    protected final static String PARAMETER_NAME = "Leo Baselinie";
    protected final static String PARAMETER_DESCRIPTION = "This is configuration Baseline in LEO";

    protected List<ProductVersion> value;

    public InitBaselineParameterValue(@Nonnull List<ProductVersion> value) {
        super(PARAMETER_NAME, PARAMETER_DESCRIPTION, PARAMETER_GRROUP);
        this.value = value;
    }

    @Override
    public List<ProductVersion> getValue() {
        return value;
    }

    public void setValue(List<ProductVersion> value) {
        this.value = value;
    }

}
