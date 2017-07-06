package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.data.Version;

import javax.annotation.Nonnull;
import java.util.List;

public class Ibcf extends Component implements Comparable<Object> {

    public Ibcf() {
        super();
    }

    public Ibcf(@Nonnull Version version) {
        super(version);
    }

    public Ibcf(@Nonnull Version version, String artifact) {
        super(version, artifact);
    }

    public Ibcf(@Nonnull Version version, State state) {
        super(version, state);
    }

}
