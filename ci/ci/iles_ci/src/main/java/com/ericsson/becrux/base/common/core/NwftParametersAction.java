package com.ericsson.becrux.base.common.core;

import hudson.model.ParametersAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by emacmyc on 2016-11-29.
 */

/**
 * Records the parameter values used for NWFT.
 * <p>
 * Because {@link ParametersAction} is just allow to read parameter from it only, so in order to save all parameters for all build steps
 * we should create a new {@link ParametersAction} like NwftParametersAction.
 * NwftParametersAction allows to read/write one or more parameters from any build step.
 */
public class NwftParametersAction extends ParametersAction {

    protected List<NwftParametersGroup> groups;

    public NwftParametersAction(NwftParameterValue... values) {
        super();
        groups = Collections.synchronizedList(new ArrayList<>());
        Arrays.asList(values).forEach(this::addParam);
    }

    public List<NwftParametersGroup> getGroups() {
        return groups;
    }

    @Override
    public String getUrlName() {
        return super.getUrlName() + "NWFT";
    }

    @Override
    public String getDisplayName() {
        return super.getDisplayName() + " (NWFT)";
    }

    public void addParam(List<NwftParameterValue> params) {
        params.forEach(p -> addParam(p));
    }

    public void addParam(NwftParameterValue param) {
        NwftParametersGroup currentGroup = null;
        synchronized (groups) {
            for (NwftParametersGroup group : groups) {
                if (param.getGroup().equals(group.name)) {
                    currentGroup = group;
                    break;
                }
            }

            if (currentGroup == null) {
                currentGroup = new NwftParametersGroup(param.getGroup());
                groups.add(currentGroup);
                groups.sort((o1, o2) -> o1.name.compareTo(o2.name));
            }

            currentGroup.getValues().add(param);
        }
    }

    public NwftParametersGroup getGroup(String groupName) {
        synchronized(groups) {
            for(NwftParametersGroup group : groups)
                if(group.getName().equals(groupName))
                    return group;
        }
        return null;
    }
}
