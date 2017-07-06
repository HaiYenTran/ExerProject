package com.ericsson.becrux.iles.data;


import com.ericsson.becrux.base.common.core.ComponentFactory;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.data.impl.BaseComponentFactory;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;

/**
 * The Iles Component Factory.
 */
public class IlesComponentFactory extends BaseComponentFactory implements ComponentFactory {

    /**
     * Get Singleton Instance.
     * @return
     */
    public static IlesComponentFactory getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Constructor.
     */
    private IlesComponentFactory() {
        super();
    }

    /**
     * Get {@link Component} form {@link ITREvent}.
     * @param event
     * @return
     */
    public Component getComponentFromEvent(ITREvent event) {
        Component nodeFromEvent = null;

        // validate
        if (event == null) { return null; }
        String p = event.getProduct();
        if (p == null || p.isEmpty()) { return null; }

        if (p.equalsIgnoreCase(Mtas.class.getSimpleName())) {
            nodeFromEvent = new Mtas(event.getBaseline(), event.getArtifact());
        } else if (p.equalsIgnoreCase(Cscf.class.getSimpleName())) {
            nodeFromEvent = new Cscf(event.getBaseline(), event.getArtifact());
        } else if (p.equalsIgnoreCase(Pcscf.class.getSimpleName())) {
            nodeFromEvent = new Pcscf(event.getBaseline(), event.getArtifact());
        } else if (p.equalsIgnoreCase(Ibcf.class.getSimpleName())) {
            nodeFromEvent = new Ibcf(event.getBaseline(), event.getArtifact());
        } else if (p.equalsIgnoreCase(Int.class.getSimpleName())) {
            //TODO We should convert from Map to List for paramerters, maybe we should do it in the Strategy
            //nodeFromEvent = new Int(event.getBaseline(), event.getArtifact(), list);
            nodeFromEvent = new Int(event.getBaseline(), event.getArtifact(), null);
        } else if (p.equalsIgnoreCase(Agw.class.getSimpleName())) {
            nodeFromEvent = new Agw(event.getBaseline(), event.getArtifact());
        } else if (p.equalsIgnoreCase(Trgw.class.getSimpleName())) {
            nodeFromEvent = new Trgw(event.getBaseline(), event.getArtifact());
        }

        if (nodeFromEvent != null) {
            nodeFromEvent.setJobId(event.getJobId());
        }

        /*
            When ITREvent.product didn't exit, this method will return null.
         */
        return nodeFromEvent;
    }

    /**
     * Registered child class.
     * @return list of child class
     */
    protected void initChildClasses() {
        registerSubtype(Cscf.class);
        registerSubtype(Ibcf.class);
        registerSubtype(Int.class);
        registerSubtype(Mtas.class);
        registerSubtype(Pcscf.class);
        registerSubtype(Agw.class);
        registerSubtype(Trgw.class);
    }

    /**
     * Inner class for lazy initialization Singleton.
     */
    private static class Holder {
        static final IlesComponentFactory INSTANCE = new IlesComponentFactory();
    }

}
