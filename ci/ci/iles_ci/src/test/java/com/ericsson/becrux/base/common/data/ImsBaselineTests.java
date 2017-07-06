package com.ericsson.becrux.base.common.data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

//import com.ericsson.becrux.iles.data.*;
import org.junit.Test;

public class ImsBaselineTests {
//
//	@Test
//	public void testImsBaselineEquality() {
//		ImsBaseline imsBaseline1 = new ImsBaseline();
//		imsBaseline1.putComponent(new Mtas(Version.create("2.0")));
//		imsBaseline1.putComponent(new Cscf(Version.create("1.0")));
//		imsBaseline1.putComponent(new Pcscf(Version.create("1.0")));
//		imsBaseline1.putComponent(new Ibcf(Version.create("1.0")));
//		imsBaseline1.putComponent(new Int(Version.create("1.1")));
//
//		ImsBaseline imsBaseline2 = new ImsBaseline();
//		imsBaseline2.putComponent(new Ibcf(Version.create("1.0")));
//		imsBaseline2.putComponent(new Cscf(Version.create("1.0")));
//		imsBaseline2.putComponent(new Int(Version.create("1.1")));
//		imsBaseline2.putComponent(new Pcscf(Version.create("1.0")));
//		imsBaseline2.putComponent(new Mtas(Version.create("2.0")));
//		assertTrue(imsBaseline1.equals(imsBaseline2));
//	}
//
//	@Test(expected=IllegalArgumentException.class)
//	public void testImsBaselineValidation(){
//		new ImsBaseline(new ArrayList<Component>(
//				Arrays.asList(
//						new Mtas(Version.create("2.0")),
//						new Cscf(Version.create("1.0")),
//						new Ibcf(Version.create("1.0")))));
//	}
//
//	@Test
//	public void testMissingNodesBaselineValidation(){
//		ImsBaseline imsBaseline = new ImsBaseline();
//		imsBaseline.putComponent(new Mtas(Version.create("2.0")));
//		imsBaseline.putComponent(new Cscf(Version.create("1.0")));
//		imsBaseline.putComponent(new Int(Version.create("1.0")));
//		assertFalse(imsBaseline.isCorrect());
//	}
}
