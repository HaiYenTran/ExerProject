package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.data.Version;

import javax.annotation.Nonnull;
import java.util.List;

public class Pcscf extends Component implements Comparable<Object> {

    public Pcscf() {
        super();
    }

    public Pcscf(@Nonnull Version version) {
        super(version);
    }

    public Pcscf(@Nonnull Version version, String artifact) {
        super(version, artifact);
    }

    public Pcscf(@Nonnull Version version, State state) {
        super(version, state);
    }
}
