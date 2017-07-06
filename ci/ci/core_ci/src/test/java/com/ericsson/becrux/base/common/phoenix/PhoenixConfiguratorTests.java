package com.ericsson.becrux.base.common.phoenix;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.ericsson.becrux.base.common.phoenix.PhoenixConfigurator;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.common.io.Files;

import hudson.FilePath;

public class PhoenixConfiguratorTests {

	@Test
	public void checkParametersAddition() throws Exception {
		File path = null;
		try{
			path = File.createTempFile("configTests","");
			PhoenixConfigurator configuration = new PhoenixConfigurator();
			configuration.addParameter(path.getAbsolutePath(), "variable1", "value");
			configuration.addParameter(path.getAbsolutePath(), "variable2", "value2");
			configuration.addParameter(path.getAbsolutePath(), "variable3", "value3");
			configuration.addParameter(path.getAbsolutePath(), "variable3", "value4");
			configuration.removeParameter(path.getAbsolutePath(), "variable2");
			List<String> list = readFile(new FilePath (path));
			int lines = 0;
			for(String elem : list){
				if(!elem.isEmpty()){
					lines++;
				}
			}
			HashMap<String,String> values= configuration.getVariables(path.getAbsolutePath());
			assertTrue(values.containsKey("variable1"));
			assertFalse(values.containsKey("variable2"));
			assertTrue(values.containsKey("variable3"));
			assertEquals(lines,2);
		}
		finally {
			if(path != null)
				cleanFileOrDirectory(path);
		}
	}

	@Test
	public void checkCreateEmptyConfigFile() throws Exception {
		File path = null;
		String param_name="deploy_VNF.param";
		try{
			path = Files.createTempDir();
			PhoenixConfigurator configuration = new PhoenixConfigurator(new FilePath(path));
			FilePath file = new FilePath(configuration.getParamFilesDirectory(),param_name);
			configuration.addParameter(param_name, "variable1", "value");
			configuration.addParameter(param_name, "variable2", "value2");
			configuration.addParameter(param_name, "variable3", "value3");
			configuration.addParameter(param_name, "variable3", "value4");
			configuration.removeParameter(param_name, "variable2");
			List<String> list = readFile(file);
			int lines = 0;
			for(String elem : list){
				if(!elem.isEmpty()){
					lines++;
				}
			}
			HashMap<String,String> values= configuration.getVariables(param_name);
			assertTrue(values.containsKey("variable1"));
			assertFalse(values.containsKey("variable2"));
			assertTrue(values.containsKey("variable3"));
			assertEquals(lines,3);
		}
		finally {
			if(path != null){
				cleanFileOrDirectory(path);
			}
		}
	}

	private void cleanFileOrDirectory(File myTempDir) {
		if(myTempDir.isDirectory()){
			try {
				FileUtils.deleteDirectory(myTempDir);
			} catch (IOException e) {
				//ignore, quitting anyway
				e.printStackTrace();
			}
		}
		else{
			try{
				myTempDir.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	 private List<String> readFile(FilePath path) throws IOException {
    	try {
			return new ArrayList<>(Arrays.asList(path.readToString().split(System.lineSeparator())));
		} catch(InterruptedException ex) {
			throw new IOException(ex);
		}
    }
}
