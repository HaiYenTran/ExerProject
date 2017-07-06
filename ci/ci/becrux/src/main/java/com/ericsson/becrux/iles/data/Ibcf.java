package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Represents 'IBCF' IMS node
 */
public class Ibcf extends Component implements Comparable<Object> {

    public Ibcf() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Ibcf(@Nonnull String version) {
        super(version);
    }

    /**
     * {@inheritDoc}
     */
    public Ibcf(@Nonnull String version, String artifact) {
        super(version, artifact);
    }

    /**
     * {@inheritDoc}
     */
    public Ibcf(@Nonnull String version, State state) {
        super(version, state);
    }

}
