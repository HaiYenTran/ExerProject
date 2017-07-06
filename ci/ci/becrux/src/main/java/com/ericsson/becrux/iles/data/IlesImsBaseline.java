package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.data.ImsBaseline;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

/**
 * Define the ILES IMS Baseline
 */
public class IlesImsBaseline extends ImsBaseline {

    private boolean isLoopRunning = false;
    private ITREvent requestEvent;

    private Date loopStartTime;

    public IlesImsBaseline() {
        super();
    }

    public IlesImsBaseline(List<Component> components, ITREvent requestEvent) {
        super(components);
        this.requestEvent = requestEvent;
    }

    public Date getLoopStartTime() {
        return loopStartTime;
    }

    public boolean isLoopRunning() {
        return isLoopRunning;
    }

    public void setLoopRunning(boolean loopRunning) {
        this.isLoopRunning = loopRunning;
        if(loopRunning) { this.loopStartTime = DateTime.now().toDate(); }
    }

    public ITREvent getRequestEvent() {
        return requestEvent;
    }

    public void setRequestEvent(ITREvent requestEvent) {
        this.requestEvent = requestEvent;
    }

    public boolean isCorrect() {
        if (getBaseline() == null)
            return false;
        for (String t : IlesComponentFactory.getInstance().getRegisteredClassNames()) {
            if (!getBaseline().containsKey(t)) {
                return false;
            }
        }
        return true;
    }
}
