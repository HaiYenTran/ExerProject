package com.ericsson.becrux.base.common.core;

import com.ericsson.becrux.base.common.data.Component;

/**
 * Created by dung.t.bui on 1/12/2017.
 */
public interface ComponentFactory extends Factory<Component> {

    /**
     * Create Object by type
     * @param type
     * @return
     * @throws Exception if anything fail
     */
    Component create(String type) throws Exception;

    /**
     * Create Object by type
     * @param type
     * @param version
     * @return
     * @throws Exception
     */
    Component create(String type, String version) throws Exception;
}
