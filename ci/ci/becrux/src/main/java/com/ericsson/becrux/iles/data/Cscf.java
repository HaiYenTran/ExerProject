package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;

import javax.annotation.Nonnull;

/**
 * Represents "CSCF" IMS node.
 */
public class Cscf extends Component implements Comparable<Object> {

    public Cscf() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Cscf(@Nonnull String version) {
        super(version);
    }

    /**
     * {@inheritDoc}
     */
    public Cscf(@Nonnull String version, String artifact) {
        super(version, artifact);
    }

    public Cscf(@Nonnull String version, State state) {
        super(version, state);
    }

}
