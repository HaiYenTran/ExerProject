package com.ericsson.becrux.base.common.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link StringHelper}
 */
public class StringHelperTest {


    /**
     * Tests {@link StringHelper#handleString(Map, String)}
     * @throws Exception if anything fail
     */
    @Test
    public void testHandleString() throws Exception {
        String value1 = "hello ${name}.";
        String value2 = "hello ${${db}}.";
        String value3 = "hello ${${sub}-${name}}.";
        Map<String, String> params = new HashMap<>();
        params.put("name", "stranger");
        params.put("sub", "weird");
        params.put("db", "name");
        params.put("weird-stranger", "DUMMY");
        String result1 = StringHelper.handleString(params, value1);
        String result2 = StringHelper.handleString(params, value2);
        String result3 = StringHelper.handleString(params, value3);
        assertTrue("hello stranger.".equals(result1));
        assertTrue("hello stranger.".equals(result2));
        assertTrue("hello DUMMY.".equals(result3));
    }

    @Test
    public void testHandleJsonString() throws Exception {
        String value = "{PCSCF_INSTALLABLE:${PCSCF_INSTALLABLE},VERSION:${VERSION}}";
        Map<String, String> params = new HashMap<>();
        params.put("PCSCF_INSTALLABLE", "true");
        params.put("VERSION", "1.0");
        String result = StringHelper.handleString(params, value);

        assertTrue("{PCSCF_INSTALLABLE:true,VERSION:1.0}".equals(result));
    }

    /**
     * Tests {@link StringHelper#isParameterFormat(String)}.
     * @throws Exception if anything fail
     */
    @Test
    public void testIsParameterFormat() throws Exception {
        String x = "${asd}";
        assertTrue(StringHelper.isParameterFormat(x));
    }

    @Test
    public void testGenerateID() throws Exception {
        String id1 = StringHelper.generateID("");
        String id2 = StringHelper.generateID("");
        assertTrue(!id1.equals(id2));

        String id3 = StringHelper.generateID("TEST");
        assertTrue(id3.contains("TEST"));
    }

    @Test
    public void testConvertStringToList() throws Exception {
        String input = "TYPE1, TYPE2";
        List<String> expect = StringHelper.convertStringToList(input);

        assertTrue(expect != null);
        assertTrue(expect.size() == 2);
        assertTrue("TYPE1".equals(expect.get(0)));
        assertTrue("TYPE2".equals(expect.get(1)));
    }
}
