package com.ericsson.becrux.base.common.core;


/**
 * Created by dung.t.bui on 2/22/2017.
 */
public class CommonParamenterValue extends NwftParameterValue{
    protected final static String PARAMETER_GRROUP = "Common";
    protected final static String PARAMETER_DESCRIPTION = "This is common param for any purpose.";

    private Object value;
    private String valueType; // this is backup in case the value contain unknown type

    public CommonParamenterValue(String name, Object value) {
        super(name, PARAMETER_GRROUP);
        setValue(value);
    }

    @Override
    public Object getValue() {
        return value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValue(Object value) {
        this.value = value;
        this.valueType = value.getClass().getName();
    }

    public void setValue(Object value, String valueType) {
        this.value = value;
        this.valueType = valueType;
    }

    public void setValueType(String type) {
        this.valueType = type;
    }
}
