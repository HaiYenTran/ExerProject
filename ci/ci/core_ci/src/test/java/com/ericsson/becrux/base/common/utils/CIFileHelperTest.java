package com.ericsson.becrux.base.common.utils;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Test {@link CIFileHelperTest}
 * TODO: need more unit tests
 */
public class CIFileHelperTest {

    @Test(expected = IOException.class)
    public void testValidateFileWithFileNotFoundException() throws Exception {
        String path = "TestFolder/dummy.txt";

        assertTrue(CIFileHelper.validateFile(path, false, false, false, false, false));
    }

    @Test
    public void testValidateFileIsDirectory() throws Exception {
        File dir = Files.createTempDir();
        String path = dir.getAbsolutePath();
        assertTrue(CIFileHelper.validateFile(path, true, true, false, true, true));
        dir.delete();
    }

    @Test
    public void testValidateFileIsFile() throws Exception {
        File file = File.createTempFile("temp","");
        String path = file.getAbsolutePath();
        assertTrue(CIFileHelper.validateFile(path, true, true, false, true, false));
        file.delete();
    }
}
