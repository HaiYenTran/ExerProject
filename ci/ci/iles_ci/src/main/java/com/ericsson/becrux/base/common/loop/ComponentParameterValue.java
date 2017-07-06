package com.ericsson.becrux.base.common.loop;

import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.base.common.data.Component;
import com.google.gson.Gson;

/**
 * Created by emacmyc on 2016-12-09.
 */
public class ComponentParameterValue extends NwftParameterValue {

    public final static String group = "Components used";

    private Component component;

    public ComponentParameterValue(String name, Component value, String description) {
        super(name, description, group);
        this.component = value;
    }

    public ComponentParameterValue(String name, Component value) {
        super(name, group);
        this.component = value;
    }

    @Override
    public String getValue() {
        return new Gson().toJson(component);
    }

    public Component getComponent() { return component; }

    public void setComponent(Component component) { this.component = component; }
}
