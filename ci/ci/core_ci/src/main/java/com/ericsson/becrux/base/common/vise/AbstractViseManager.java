package com.ericsson.becrux.base.common.vise;

/**
 * Created by emiwaso on 2016-12-13.
 */
public abstract class AbstractViseManager implements ViseManager {

    //Allows all implementations to share a single lock object if they desire and need to do so
    protected static final Object _lock = new Object();
}
