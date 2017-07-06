package com.ericsson.becrux.base.common.data;

import com.ericsson.becrux.base.common.core.ComponentFactory;
import com.ericsson.becrux.base.common.data.impl.BaseComponentFactory;
import com.ericsson.becrux.base.common.data.impl.BaseComponentImpl;
import com.ericsson.becrux.base.common.data.impl.DummyNodeFactory;
import com.ericsson.becrux.base.common.data.impl.DummyNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link ComponentFactory}
 */
public class NodeFactoryTests {

    ComponentFactory factory;

    @Before
    public void setUp(){
        factory = new BaseComponentFactory();
    }


    @Test(expected = Exception.class)
    public void testCreateNodeWithNullInput() throws Exception {
        assertNull(factory.create(null));
    }

    @Test(expected = Exception.class)
    public void testCreateNodeWithInvalidInput() throws Exception {
        assertNull(factory.create("UNKNOWN_TYPE"));
    }

    @Test
    public void testCreateNode() throws Exception {
        Component node = factory.create(BaseComponentImpl.class.getSimpleName());
        node.setJobId(1L);

        String json = factory.toJson(node);
        assertTrue(json != null);
        node = factory.fromJson(json);
        assertTrue(node != null);
        assertTrue(node instanceof BaseComponentImpl);
        assertTrue(node.getJobId() == 1L);
        assertTrue(BaseComponentImpl.class.getSimpleName().equals(node.getType()));
    }

    @Test
    public void testInheritDummy() throws Exception {
        ComponentFactory inheritFactory = new DummyNodeFactory();

        Component node = inheritFactory.create(DummyNode.class.getSimpleName());
        node.setJobId(1L);

        String json = inheritFactory.toJson(node);
        assertTrue(json != null);
        node = inheritFactory.fromJson(json);
        assertTrue(node != null);
        assertTrue(node instanceof DummyNode);
        assertTrue(node.getJobId() == 1L);
        assertTrue(DummyNode.class.getSimpleName().equals(node.getType()));
    }

}
