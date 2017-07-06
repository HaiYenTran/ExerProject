package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Tests {@link IlesComponentFactory}
 */
public class IlesComponentFactoryTest {

    private IlesComponentFactory factory = IlesComponentFactory.getInstance();

    @Test
    public void testCreateComponent() throws Exception {
        Component mtas = factory.create("Mtas");
        assertTrue(mtas.getType().equals(Mtas.class.getSimpleName()));
        Component cscf = factory.create("CSCF");
        assertTrue(cscf.getType().equals(Cscf.class.getSimpleName()));
        Component ibcf = factory.create("ibcf");
        assertTrue(ibcf.getType().equals(Ibcf.class.getSimpleName()));
        Component intt = factory.create("INt");
        assertTrue(intt.getType().equals(Int.class.getSimpleName()));
        Component pcscf = factory.create("pCScf");
        assertTrue(pcscf.getType().equals(Pcscf.class.getSimpleName()));
        Component agw = factory.create("agw");
        assertTrue(agw.getType().equals(Agw.class.getSimpleName()));
        Component trgw = factory.create("trgw");
        assertTrue(trgw.getType().equals(Trgw.class.getSimpleName()));
    }

    @Test
    public void testGetComponentFromITREvent() {
        ITREvent itr = new ITREvent();
        itr.setProduct("mTas");
        itr.setBaseline("R1A01");
        Component comp = IlesComponentFactory.getInstance().getComponentFromEvent(itr);
        assertTrue(comp != null);
        assertTrue(comp instanceof Mtas);

        itr.setProduct("CSCF");
        comp = IlesComponentFactory.getInstance().getComponentFromEvent(itr);
        assertTrue(comp != null);
        assertTrue(comp instanceof Cscf);

        itr.setProduct("ibcf");
        comp = IlesComponentFactory.getInstance().getComponentFromEvent(itr);
        assertTrue(comp != null);
        assertTrue(comp instanceof Ibcf);

        itr.setProduct("InT");
        comp = IlesComponentFactory.getInstance().getComponentFromEvent(itr);
        assertTrue(comp != null);
        assertTrue(comp instanceof Int);

        itr.setProduct("AGW");
        comp = IlesComponentFactory.getInstance().getComponentFromEvent(itr);
        assertTrue(comp != null);
        assertTrue(comp instanceof Agw);

        itr.setProduct("TRGW");
        comp = IlesComponentFactory.getInstance().getComponentFromEvent(itr);
        assertTrue(comp != null);
        assertTrue(comp instanceof Trgw);

        itr.setProduct("Pcscf");
        itr.setJobId(1L);
        comp = IlesComponentFactory.getInstance().getComponentFromEvent(itr);
        assertTrue(comp != null);
        assertTrue(comp instanceof Pcscf);
        assertTrue(comp.getJobId() == 1L);

        itr.setProduct("");
        comp = IlesComponentFactory.getInstance().getComponentFromEvent(itr);
        assertTrue(comp == null);

        itr.setProduct(null);
        comp = IlesComponentFactory.getInstance().getComponentFromEvent(itr);
        assertTrue(comp == null);

        comp = IlesComponentFactory.getInstance().getComponentFromEvent(null);
        assertTrue(comp == null);
    }
}
