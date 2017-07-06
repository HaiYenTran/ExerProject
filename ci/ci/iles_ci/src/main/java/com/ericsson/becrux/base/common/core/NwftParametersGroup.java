package com.ericsson.becrux.base.common.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by emacmyc on 2016-11-29.
 */
public class NwftParametersGroup {
    String name;
    List<NwftParameterValue> values = Collections.synchronizedList(new ArrayList<>());

    public NwftParametersGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<NwftParameterValue> getValues() {
        return values;
    }
}
