package com.ericsson.becrux.base.common.eventhandler.strategies;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.eventhandler.EventHandler;
import hudson.model.Build;

/**
 * The build step used as a base for the specific {@link hudson.tasks.BuildStep Build Steps}
 * that register {@link EventHandlingStrategy Event Handling Strategies} for use
 * by the {@link EventHandler Event Handler}.
 */
public abstract class EventStrategyRegistrationBuildStep extends NwftBuildStep {

    /**
     * Creates new {@link hudson.model.ParametersAction} with a single {@link EventHandlingStrategyParameterValue} and adds it to the build.
     *
     * @param build the build to which the parameters will be added
     * @param strategy the {@link EventHandlingStrategy} to add as a parameter
     */
    protected void registerStrategy(Build<?, ?> build, EventHandlingStrategy strategy) {
        this.addNwftBuildParameter(build, new EventHandlingStrategyParameterValue(strategy.getName(), strategy, strategy.getDescription()));
    }
}
