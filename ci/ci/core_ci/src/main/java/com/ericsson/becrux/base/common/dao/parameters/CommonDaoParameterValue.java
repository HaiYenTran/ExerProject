package com.ericsson.becrux.base.common.dao.parameters;

import com.ericsson.becrux.base.common.dao.CommonDao;
import com.ericsson.becrux.base.common.core.NwftParameterValue;

import javax.annotation.Nonnull;

/**
 * Created by emiwaso on 2016-12-08.
 */
public class CommonDaoParameterValue extends NwftParameterValue {

    protected final static String PARAMETER_GROUP = "Database access objects";

    protected CommonDao value;

    public CommonDaoParameterValue(String name, @Nonnull CommonDao value, String description) {
        super(name, description, PARAMETER_GROUP);
        this.value = value;
    }

    public CommonDaoParameterValue(String name, @Nonnull CommonDao value) {
        super(name, PARAMETER_GROUP);
        this.value = value;
    }

    @Override
    public CommonDao getValue() { return value; }
}
