package com.ericsson.becrux.base.common.data.impl;

import com.ericsson.becrux.base.common.core.AbstractFactory;
import com.ericsson.becrux.base.common.core.ComponentFactory;
import com.ericsson.becrux.base.common.data.Component;

/**
 * Created by dung.t.bui on 12/30/2016.
 */
public class BaseComponentFactory extends AbstractFactory<Component> implements ComponentFactory {

    /**
     * Constructor.
     */
    public BaseComponentFactory() {
        super(Component.class);
        initChildClasses();
    }

    /**
     * Registered child class.
     * @return list of child class
     */
    protected void initChildClasses() {
        registerSubtype(BaseComponentImpl.class);
    }

    /** {@inheritDoc} */
    @Override
    public Component create(String type) throws Exception {
        Class<? extends Component> clazz = getRegisteredClasses()
                .get(getRegisteredClassNames().stream().filter(cls -> cls.equalsIgnoreCase(type)).toArray()[0]);
        if (clazz == null) {
            throw new ClassNotFoundException("Class " + type + " is not registered." );
        }
        return clazz.newInstance();
    }

    /** {@inheritDoc} */
    @Override
    public Component create(String type, String version) throws Exception {
        Component node = create(type);
        node.setVersion(version);
        return node;
    }
}
