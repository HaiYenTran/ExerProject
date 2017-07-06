package com.ericsson.becrux.iles.configuration;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IlesDirectoryTests {
	
	private File directory;
	
	@Before
	public void mkDir() throws IOException {
		directory = Files.createTempDirectory(null).toFile();
	}
	
	@After
	public void rmDir() throws IOException {
		if(directory != null)
			FileUtils.deleteDirectory(directory);
	}
	
	@Test
	public void validationTest() throws Exception {
		IlesDirectory iles = new IlesDirectory(directory.getPath());
		iles.createFolders();

		assertNotNull(iles.getBaseDir());
		assertNotNull(iles.getTestExecJar());
		assertNotNull(iles.getProvisioningScript());
	}
}
