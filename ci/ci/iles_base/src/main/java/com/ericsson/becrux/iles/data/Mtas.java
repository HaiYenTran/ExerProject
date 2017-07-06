package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.data.Version;

import javax.annotation.Nonnull;
import java.util.List;

public class Mtas extends Component implements Comparable<Object> {

    public Mtas() {
        super();
    }

    public Mtas(@Nonnull Version version) {
        super(version);
    }

    public Mtas(@Nonnull Version version, String artifact) {
        super(version, artifact);
    }

    public Mtas(@Nonnull Version version, State state) {
        super(version, state);
    }
}
