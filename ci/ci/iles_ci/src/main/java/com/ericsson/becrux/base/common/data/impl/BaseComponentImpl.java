package com.ericsson.becrux.base.common.data.impl;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.data.Version;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by dung.t.bui on 12/30/2016.
 */
public class BaseComponentImpl extends Component {
//    private static final String TYPE = BaseComponentImpl.class.getSimpleName().toUpperCase();

//    public BaseComponentImpl() {
//        super(TYPE);
//    }
//
//    public BaseComponentImpl(@Nonnull Version version) {
//        super(TYPE, version);
//    }
//
//    public BaseComponentImpl(@Nonnull Version version, String artifact, List<String> parameters) {
//        super(TYPE, version, artifact, parameters);
//    }
//
//    public BaseComponentImpl(@Nonnull Version version, State state) {
//        super(TYPE, version, state);
//    }

    public BaseComponentImpl() {
        super();
    }

    public BaseComponentImpl(@Nonnull Version version) {
        super(version);
    }

    public BaseComponentImpl(@Nonnull Version version, String artifact) {
        super(version, artifact);
    }

    public BaseComponentImpl(@Nonnull Version version, State state) {
        super(version, state);
    }
}
