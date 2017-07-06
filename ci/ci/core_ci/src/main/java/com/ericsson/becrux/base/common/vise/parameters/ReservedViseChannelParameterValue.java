package com.ericsson.becrux.base.common.vise.parameters;

import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.core.NwftParameterValue;

import javax.annotation.Nonnull;

/**
 * Created by emiwaso on 2016-12-02.
 */
public class ReservedViseChannelParameterValue extends NwftParameterValue {

    protected final static String PARAMETER_GROUP = "Reserved Vise channels";

    protected ViseChannel vise;

    public ReservedViseChannelParameterValue(String name, @Nonnull ViseChannel vise, String description) {
        super(name, description, PARAMETER_GROUP);
        this.vise = vise;
    }

    public ReservedViseChannelParameterValue(String name, @Nonnull ViseChannel vise) {
        super(name, PARAMETER_GROUP);
        this.vise = vise;
    }

    public ViseChannel getViseChannel() { return vise; }

    @Override
    public String getValue() { return vise.toString(); }
}
