package com.ericsson.becrux.base.common.data.impl;

import com.ericsson.becrux.base.common.data.Component;
import javax.annotation.Nonnull;

/**
 * Created by dung.t.bui on 12/30/2016.
 */
public class BaseComponentImpl extends Component {

    public BaseComponentImpl() {
        super();
    }

    public BaseComponentImpl(@Nonnull String version) {
        super(version);
    }

    public BaseComponentImpl(@Nonnull String version, String artifact) {
        super(version, artifact);
    }

    public BaseComponentImpl(@Nonnull String version, State state) {
        super(version, state);
    }
}
