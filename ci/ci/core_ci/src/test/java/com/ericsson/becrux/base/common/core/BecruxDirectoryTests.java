package com.ericsson.becrux.base.common.core;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.ericsson.becrux.base.common.exceptions.BecruxDirectoryException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BecruxDirectoryTests {
	
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
		BecruxDirectory becruxDirectory = new BecruxDirectory(directory.toPath().toString());
        becruxDirectory.createFolders();

        assertNotNull(becruxDirectory.getBaseDir());
		assertNotNull(becruxDirectory.getTestExecJar());
		assertNotNull(becruxDirectory.getProvisioningScript());

		assertTrue(becruxDirectory.validate());
	}

	@Test
	public void testMissingAllDirectories() {
		try {
			BecruxDirectory becruxDirectory = new BecruxDirectory(directory.toPath().toString());
			becruxDirectory.validate();
			fail("Directories not created, expect exception.");
		} catch (Exception e) {
			assertNotNull(e);
		}
	}

	@Test
	public void testMissingOneDirectory() throws Exception {
		BecruxDirectory becruxDirectory = new BecruxDirectory(directory.toPath().toString());

		becruxDirectory.createFolders();
        FileUtils.deleteDirectory(becruxDirectory.getPhoenixDir());
		validate(becruxDirectory);

		becruxDirectory.createFolders();
		FileUtils.deleteDirectory(becruxDirectory.getProvisioningDir());
		validate(becruxDirectory);

	}

	public void validate(BecruxDirectory becruxDirectory) {
		try {
			becruxDirectory.validate();
			fail("Directory deleted, exception expected");
		} catch (BecruxDirectoryException e) {
            assertNotNull(e);
		}
	}
}
