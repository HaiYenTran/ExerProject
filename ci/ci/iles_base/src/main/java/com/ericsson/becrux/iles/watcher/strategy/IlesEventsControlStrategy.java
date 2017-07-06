package com.ericsson.becrux.iles.watcher.strategy;

import com.ericsson.becrux.base.common.core.NwftDownstreamJob;
import com.ericsson.becrux.base.common.dao.EventDao;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.watcher.strategy.ControllingStrategy;
import com.ericsson.becrux.base.common.watcher.strategy.impl.BaseControllingStrategy;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Run;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

/**
 * The Events Controlling Strategy.
 */
public class IlesEventsControlStrategy extends BaseControllingStrategy implements ControllingStrategy {

    private EventDao dao;
    private String eventQueueName;
    private String controllerJobName;
    private AbstractBuild build;

    public IlesEventsControlStrategy(String controllerJobName, String eventQueueName, @Nonnull PrintStream logger, @Nonnull EventDao dao, AbstractBuild build) throws IOException {
        super(null, logger);
        this.dao = dao;
        this.eventQueueName = eventQueueName;
        this.controllerJobName = controllerJobName;
        this.build = build;
    }

    @Override
    protected void initializeData() {
        // no need any specific.
    }

    @Override
    public void handle() {
        try {
            // get all event from queue
            Collection<Event> events = dao.loadEventQueue(eventQueueName);
            if(events.size() > 0) {
                // trigger controller jobs
                // TODO: find better CAUSE for trigger jenkins jobs
                NwftDownstreamJob job = new NwftDownstreamJob(this.controllerJobName, null, new Cause.UpstreamCause((Run<?,?>)build), 0, 0);
                job.schedule();
            }
        } catch (IOException ex) {
            ex.printStackTrace(getLogger());
        }
    }
}
