package com.ericsson.becrux.iles.data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.iles.data.*;
import org.junit.Test;

public class IlesImsBaselineTests {

	@Test
	public void testImsBaselineEquality() {
		IlesImsBaseline imsBaseline1 = new IlesImsBaseline();
		imsBaseline1.putComponent(new Mtas("2.0"));
		imsBaseline1.putComponent(new Cscf("1.0"));
		imsBaseline1.putComponent(new Pcscf("1.0"));
		imsBaseline1.putComponent(new Ibcf("1.0"));
		imsBaseline1.putComponent(new Int("1.1"));
		
		IlesImsBaseline imsBaseline2 = new IlesImsBaseline();
		imsBaseline2.putComponent(new Ibcf("1.0"));
		imsBaseline2.putComponent(new Cscf("1.0"));
		imsBaseline2.putComponent(new Int("1.1"));
		imsBaseline2.putComponent(new Pcscf("1.0"));
		imsBaseline2.putComponent(new Mtas("2.0"));
		assertTrue(imsBaseline1.equals(imsBaseline2));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testImsBaselineValidation(){
		new IlesImsBaseline(new ArrayList<Component>(
				Arrays.asList(
						new Mtas("2.0"),
						new Cscf("1.0"),
						new Ibcf("1.0"))), null);
	}
	
	@Test
	public void testMissingNodesBaselineValidation(){
		IlesImsBaseline imsBaseline = new IlesImsBaseline();
		imsBaseline.putComponent(new Mtas("2.0"));
		imsBaseline.putComponent(new Cscf("1.0"));
		imsBaseline.putComponent(new Int("1.0"));
		assertFalse(imsBaseline.isCorrect());
	}
}
