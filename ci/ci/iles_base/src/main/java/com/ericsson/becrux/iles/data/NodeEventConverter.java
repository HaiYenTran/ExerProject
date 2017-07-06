package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;

public class NodeEventConverter {
    private NodeEventConverter() {
    }

    public static ITREvent convertToITREvent(Component node) {
        ITREvent event = new ITREvent();
        return fillEventData(event, node);
    }

    public static ITREvent fillEventData(ITREvent event, Component node) {
        event.setArtifact(node.getArtifact());
        event.setBaseline(node.getVersion());
        //event.setParameters(node.getParameters());
        event.setProduct(node.getType());
        return event;
    }
}
