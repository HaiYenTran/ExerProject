package com.ericsson.becrux.iles.data;

import java.io.IOException;
import java.util.Arrays;

import java.util.List;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.data.Version;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import org.junit.Test;

import static org.junit.Assert.*;

public class ComponentTests {
	
	@Test
	public void getInstanceTest() throws Exception {
		assertNull(IlesComponentFactory.getInstance().fromJson(null));
		for (String type : IlesComponentFactory.getInstance().getRegisteredClassNames()) {
			assertNotNull(IlesComponentFactory.getInstance().create(type));
		}
	}
	
	@Test
	public void objectSerializationTest() {
		Component before = new Mtas(Version.createReleaseVersion("1.0"), Component.State.BASELINE_APPROVED);
		assertNotNull(before);
		Component after = IlesComponentFactory.getInstance().fromJson(IlesComponentFactory.getInstance().toJson(before));
		assertNotNull(after);
		assertTrue(after.equals(before));
	}
	
	@Test
	public void listSerializationTest() {
		List<Component> before = Arrays.asList( 
				new Cscf(Version.createReleaseVersion("1.1"), Component.State.NEW_BUILD),
				new Int(Version.createReleaseVersion("1.1"), Component.State.BASELINE_APPROVED));
		assertNotNull(before);
		List<Component> after = IlesComponentFactory.getInstance().fromJsonToList(IlesComponentFactory.getInstance().convertCollectionToJson(before));
		assertNotNull(after);
		assertTrue(before.size() == after.size());
		for (Component c : before) {
			assertTrue(after.contains(c));
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void getDeployParametersMissingArtifact() throws IllegalArgumentException, IOException {
		Component node = new Cscf(Version.createReleaseVersion("1.0.1"));
		node.getDeployParameters();
	}

	@Test
	public void testCreationFromITREvent() {
		ITREvent i = new ITREvent();
		i.setProduct("CSCF");
		i.setBaseline(Version.createReleaseVersion("1.0.1"));
		i.setArtifact("file:///proj/ims_lu/cba_cde/int_ci/cfg/vnf_file");
		Component nodeFromEvent = IlesComponentFactory.getInstance().getComponentFromEvent(i);
		Component nodeFromScratch = new Cscf(Version.createReleaseVersion("1.0.1"));
		nodeFromScratch.setArtifact(i.getArtifact());
		assertTrue(nodeFromEvent.equals(nodeFromScratch));
	}

	@Test
	public void testCompareBetweenDifferentComponent() throws Exception {
		Cscf cscf = (Cscf) IlesComponentFactory.getInstance().create(Cscf.class.getSimpleName(), Version.createReleaseVersion("1.0"));
		Ibcf ibcf = (Ibcf) IlesComponentFactory.getInstance().create(Ibcf.class.getSimpleName(), Version.createReleaseVersion("2.0"));

		assertTrue(!cscf.equals(ibcf));
		assertTrue(!ibcf.equals(cscf));

		// compareTo method only compare version
		assertTrue(ibcf.compareTo(cscf) == 0); // different type
		assertTrue(cscf.compareTo(ibcf) == 0); // different type
		assertTrue(cscf.compareTo(cscf) == 0);
		assertTrue(ibcf.compareTo(ibcf) == 0);
		assertTrue(cscf.compareTo(null) == 1);
		assertTrue(ibcf.compareTo(null) == 1);
	}

}
