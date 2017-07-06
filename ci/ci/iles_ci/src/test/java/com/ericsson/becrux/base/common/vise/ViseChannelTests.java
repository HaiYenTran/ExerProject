package com.ericsson.becrux.base.common.vise;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ViseChannelTests {
	
	@Test
	public void validChannelTest() {
		String s = "VISE0123";
		ViseChannel vise = new ViseChannel(s);
		assertEquals("123", vise.getShortName());
		assertEquals(s, vise.getFullName());
		assertEquals(123, vise.getNumber());
	}
	
	@Test
	public void validChannelShortNameTest() {
		String s = "103";
		ViseChannel vise = new ViseChannel(s);
		assertEquals(s, vise.getShortName());
		assertEquals("VISE0"+s, vise.getFullName());
		assertEquals(103, vise.getNumber());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invalidNameTest() {
		new ViseChannel("1234");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invalidNameTest2() {
		new ViseChannel("012");
	}
	
	@Test(expected=NullPointerException.class)
	public void nullNameTest() {
		new ViseChannel(null);
	}

	@Test
	public void getWholeNumber() {
		ViseChannel viseChannel = new ViseChannel("VISE0203");
		assertEquals("0203",viseChannel.getWholeNumber());
	}

}
