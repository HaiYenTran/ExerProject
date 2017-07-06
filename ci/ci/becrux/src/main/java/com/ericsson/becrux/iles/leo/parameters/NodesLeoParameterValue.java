package com.ericsson.becrux.iles.leo.parameters;

import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.iles.leo.domain.UnitResponse;

import java.util.HashMap;

/**
 * Created by thien.d.vu on 12/7/2016.
 */
public class NodesLeoParameterValue extends NwftParameterValue {
    protected final static String PARAMETER_GRROUP = "Leo ILES";
    protected final static String PARAMETER_NAME = "Leo ILES ";
    protected final static String PARAMETER_DESCRIPTION = "This is configuration UnitRequest in LEO";

    HashMap<String,UnitResponse> nodes = new HashMap<>();

    public NodesLeoParameterValue() {
        super(PARAMETER_NAME, PARAMETER_DESCRIPTION, PARAMETER_GRROUP);
    }

    public void addNode(String node, UnitResponse response){
        nodes.put(node, response);
    }

    public UnitResponse getResponse(String node){
        return nodes.get(node);
    }

    public HashMap<String,UnitResponse> getNodes(){
        return nodes;
    }

    @Override
    public Object getValue() {
        return nodes;
    }
}
