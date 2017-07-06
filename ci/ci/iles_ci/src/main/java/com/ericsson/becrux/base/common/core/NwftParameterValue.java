package com.ericsson.becrux.base.common.core;

import hudson.model.ParameterValue;

/**
 * Created by emacmyc on 2016-11-29.
 */
public abstract class NwftParameterValue extends ParameterValue {

    String group;

    boolean used = false;

    protected NwftParameterValue(String name, String description, String group) {
        super(name, description);
        this.group = group;
    }

    protected NwftParameterValue(String name, String group) {
        super(name);
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    /**
     * Return if the parameter has been used.
     *
     * @return true if value has been used
     */
    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

}
