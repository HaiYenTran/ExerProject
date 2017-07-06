package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;

/**
 * Utility class to populate event data from the IMS node.
 */
public class NodeEventConverter {
    private NodeEventConverter() {
    }

    /**
     * Converts IMS node to ITREvent.
     * @param node IMS node
     * @return ITREvent
     */
    public static ITREvent convertToITREvent(Component node) {
        ITREvent event = new ITREvent();
        return fillEventData(event, node);
    }

    /**
     * Populates ITREvent using IMS node object.
     * @param event ITREvent
     * @param node IMS node
     * @return ITREvent
     */
    public static ITREvent fillEventData(ITREvent event, Component node) {
        event.setArtifact(node.getArtifact());
        event.setBaseline(node.getVersion());
        //event.setParameters(node.getParameters());
        event.setProduct(node.getType());
        return event;
    }
}
