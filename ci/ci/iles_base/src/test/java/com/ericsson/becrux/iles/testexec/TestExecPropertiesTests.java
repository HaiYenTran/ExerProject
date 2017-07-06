package com.ericsson.becrux.iles.testexec;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import hudson.FilePath;

public class TestExecPropertiesTests {

	private FilePath directory;
	
	@Before
	public void mkDir() throws IOException {
		directory = new FilePath( Files.createTempDirectory(null).toFile() );
	}
	
	@After
	public void rmDir() throws IOException, InterruptedException {
		directory.deleteRecursive();
	}
	/*
	@Test
	public void testSetProperties() throws IOException, InterruptedException {
		
		String param1Name = "FOO";
		String param2Name = "AAA";
		String param3Name = "BBB";
		
		String param1Value = "bar";
		String param2Value = "1";
		String param3Value = "100";
		
		Map<String,String> map = new HashMap<>();
		map.put(param2Name, param2Value);
		map.put(param3Name, param3Value);
		
		String cfgFile;
		
		Properties p;
		String actual1;
		String actual2;
		String actual3;
		try ( TestExecProperties tep = new TestExecProperties(directory) ) {
			cfgFile = tep.getCfgFilePath();
			tep.set(param1Name, param1Value);
			tep.setAll(map);
		}
		try ( InputStream in = new FileInputStream(cfgFile) ) {
			p = new Properties();
			p.load(in);
			actual1 = p.getProperty(param1Name);
			actual2 = p.getProperty(param2Name);
			actual3 = p.getProperty(param3Name);
		}
		
		assertEquals(param1Value, actual1);
		assertEquals(param2Value, actual2);
		assertEquals(param3Value, actual3);
	}

	@Test
	public void testSetNullValue() throws IOException, InterruptedException {
		String name = "NAME";
		
		String cfgFile;
		String result;

		try ( TestExecProperties tep = new TestExecProperties(directory) ) {
			cfgFile = tep.getCfgFilePath();
			tep.set(name, null);
		}

		try ( InputStream in = new FileInputStream(cfgFile) ) {
			Properties p = new Properties();
			p.load(in);
			result = p.getProperty(name);
		}
		
		assertEquals("", result);
	}
	*/
}
