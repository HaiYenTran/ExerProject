package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;

import javax.annotation.Nonnull;

/**
 * Represents 'MTAS' IMS node.
 */
public class Mtas extends Component implements Comparable<Object> {

    public Mtas() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Mtas(@Nonnull String version) {
        super(version);
    }

    /**
     * {@inheritDoc}
     */
    public Mtas(@Nonnull String version, String artifact) {
        super(version, artifact);
    }

    /**
     * {@inheritDoc}
     */
    public Mtas(@Nonnull String version, State state) {
        super(version, state);
    }
}
