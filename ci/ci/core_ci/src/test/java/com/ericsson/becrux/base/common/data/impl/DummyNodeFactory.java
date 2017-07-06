package com.ericsson.becrux.base.common.data.impl;


import com.ericsson.becrux.base.common.core.ComponentFactory;

/**
 * Created by dung.t.bui on 12/30/2016.
 */
public class DummyNodeFactory extends BaseComponentFactory implements ComponentFactory {

    public DummyNodeFactory() {
        super();
        registerSubtype(DummyNode.class);
    }
}
