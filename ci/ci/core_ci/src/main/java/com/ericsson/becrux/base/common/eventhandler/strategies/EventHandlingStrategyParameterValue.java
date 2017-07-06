package com.ericsson.becrux.base.common.eventhandler.strategies;

import com.ericsson.becrux.base.common.core.NwftParameterValue;

/**
 * Created by emacmyc on 2016-11-28.
 */
public class EventHandlingStrategyParameterValue extends NwftParameterValue {

    protected final static String PARAMETER_GROUP = "Event Handling Strategies";

    protected EventHandlingStrategy strategy;

    public EventHandlingStrategyParameterValue(String name, EventHandlingStrategy strategy, String description) {
        super(name, description, PARAMETER_GROUP);
        this.strategy = strategy;
    }

    public EventHandlingStrategyParameterValue(String name, EventHandlingStrategy strategy) {
        super(name, PARAMETER_GROUP);
        this.strategy = strategy;
    }

    public EventHandlingStrategy getStrategy() { return strategy; }

    public void setStrategy(EventHandlingStrategy strategy) { this.strategy = strategy; }

    @Override
    public String getValue() {
        return strategy.getName();
    }
}
