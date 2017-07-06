package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.data.Version;

import javax.annotation.Nonnull;
import java.util.List;

public class Cscf extends Component implements Comparable<Object> {

    public Cscf() {
        super();
    }

    public Cscf(@Nonnull Version version) {
        super(version);
    }

    public Cscf(@Nonnull Version version, String artifact) {
        super(version, artifact);
    }

    public Cscf(@Nonnull Version version, State state) {
        super(version, state);
    }

}
