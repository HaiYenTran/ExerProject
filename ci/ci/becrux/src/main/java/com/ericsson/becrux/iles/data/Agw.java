package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;

import javax.annotation.Nonnull;

/**
 * Present the AGW Node product.
 */
public class Agw extends Component implements Comparable<Object> {

    /**
     * Default constructor.
     */
    public Agw() {
        super();
    }

    /**
     * Create new AGW component with version.
     * @param version the version of AGW product
     */
    public Agw(@Nonnull String version) {
        super(version);
    }

    /**
     * Create new AGW component with version and artifact.
     * @param version the version of AGW product
     * @param artifact the path to AGW component artifact
     */
    public Agw(@Nonnull String version, String artifact) {
        super(version, artifact);
    }

    /**
     * Create new AGW component with version and state.
     * @param version the version of AGW product
     * @param state the current state of AGW component.
     */
    public Agw(@Nonnull String version, State state) {
        super(version, state);
    }

}
