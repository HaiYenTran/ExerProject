package com.ericsson.becrux.base.common.utils;

import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.base.common.core.NwftParametersAction;
import com.ericsson.becrux.base.common.core.NwftParametersGroup;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategy;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategyParameterValue;
import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by emacmyc on 2016-11-29.
 */
public class BuildParametersExtractor {

    AbstractBuild build;
    Collection<ParametersAction> actions;
    List<ParameterValue> values;
    NwftParametersAction nwftAction;

    public BuildParametersExtractor(AbstractBuild build) {
        this.build = build;

        actions = build.getActions(ParametersAction.class);
        values = new ArrayList<>();
        actions.forEach(action -> values.addAll(action.getParameters()));
        nwftAction = build.getAction(NwftParametersAction.class);
        if (nwftAction == null)
            buildNwftAction();
    }

    public List<ParameterValue> getAllParameters() {
        List<ParameterValue> result = values.stream().collect(Collectors.toList());
        nwftAction.getGroups().stream().forEach(g -> result.addAll(g.getValues()));
        return result;
    }

    public <T extends ParameterValue> List<T> getAllParametersOfType(Class<T> type) {
        List<T> result = nwftAction.getGroups().stream()
                .filter(value -> type.isAssignableFrom(value.getClass()))
                .map(value -> type.cast(value))
                .collect(Collectors.toList());

        values.stream()
                .filter(value -> type.isAssignableFrom(value.getClass()))
                .map(value -> type.cast(value))
                .forEach(value -> result.add(value));

        return result;
    }

    public List<NwftParameterValue> getAllNwftParameters() {
        List<NwftParameterValue> result = new ArrayList<>();
        nwftAction.getGroups().forEach(nwftParametersGroup -> result.addAll(nwftParametersGroup.getValues()));
        return result;
    }

    public <T extends NwftParameterValue> List<T> getAllNwftParametersOfType(Class<T> type) {
        return this.getAllNwftParameters() //List<NwftParameterValue>
                .stream()   // stream (List<NwftParameterValue>)
                .filter(value -> type.isAssignableFrom(value.getClass())) //  stream (List<NwftParameterValue>), only specific
                .map(nwftParameterValue -> type.cast(nwftParameterValue)) // stream (List<type>), only specific
                .collect(Collectors.toList());  // List<type>
    }

    public List<NwftParametersGroup> getAllNwftParametersGroups() {
        return nwftAction.getGroups();
    }

    public NwftParametersGroup getNwftParametersGroup(String groupName) {
        return nwftAction.getGroup(groupName);
    }

    public List<EventHandlingStrategy> getAllEventHandlingStrategies() {
        List<EventHandlingStrategyParameterValue> params = this.getAllParametersOfType(EventHandlingStrategyParameterValue.class);
        params.addAll(this.getAllNwftParametersOfType(EventHandlingStrategyParameterValue.class));
        List<EventHandlingStrategy> strategies = new ArrayList<>();

        params.forEach(parameterValue -> {
            EventHandlingStrategyParameterValue ehsParam = (EventHandlingStrategyParameterValue) parameterValue;
            if (ehsParam != null) {
                EventHandlingStrategy strategy = ehsParam.getStrategy();
                if (strategy != null)
                    strategies.add(strategy);
            }
        });

        return strategies;
    }

    public void addNwftParameters(NwftParameterValue... params) {
        for (NwftParameterValue param : params)
            nwftAction.addParam(param);
    }

    public void addNwftParameters(Collection<NwftParameterValue> params) {
        params.forEach(param -> nwftAction.addParam(param));
    }

    public void addNwftParameter(NwftParameterValue param) {
        nwftAction.addParam(param);
    }

    protected void buildNwftAction() {
        nwftAction = new NwftParametersAction();
        build.addAction(nwftAction);
        getAllNwftParametersOfType(NwftParameterValue.class).forEach(nwftParameterValue -> nwftAction.addParam(nwftParameterValue));
    }

    class myComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    }

}
