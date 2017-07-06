package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Represents 'PCSCF'IMS node.
 */
public class Pcscf extends Component implements Comparable<Object> {

    public Pcscf() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Pcscf(@Nonnull String version) {
        super(version);
    }

    /**
     * {@inheritDoc}
     */
    public Pcscf(@Nonnull String version, String artifact) {
        super(version, artifact);
    }

    /**
     * {@inheritDoc}
     */
    public Pcscf(@Nonnull String version, State state) {
        super(version, state);
    }
}
