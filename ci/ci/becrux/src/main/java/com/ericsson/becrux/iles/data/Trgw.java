package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;

import javax.annotation.Nonnull;

/**
 * Present the TRGW Node product.
 */
public class Trgw extends Component implements Comparable<Object> {

    /**
     * Default constructor.
     */
    public Trgw() {
        super();
    }

    /**
     * Create new TRGW component with version.
     * @param version the version of TRGW product
     */
    public Trgw(@Nonnull String version) {
        super(version);
    }

    /**
     * Create new TRGW component with version and artifact.
     * @param version the version of TRGW product
     * @param artifact the path to TRGW component artifact
     */
    public Trgw(@Nonnull String version, String artifact) {
        super(version, artifact);
    }

    /**
     * Create new TRGW component with version and state.
     * @param version the version of TRGW product
     * @param state the current state of TRGW component.
     */
    public Trgw(@Nonnull String version, State state) {
        super(version, state);
    }

}
