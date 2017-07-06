package com.ericsson.becrux.base.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class TempFilesCreator {
    public static File createTempFile(String prefix, String suffix) throws IOException {
        File result = File.createTempFile(prefix, suffix, Paths.get(System.getProperty("java.io.tmpdir")).toFile()); //Create file
        result.deleteOnExit(); //Mark file to be deleted on jvm exit
        return result;
    }

    public static File createTempFile(String prefix) throws IOException {
        return createTempFile(prefix, "");
    }

    public static File createTempDirectory(String prefix, String suffix) throws IOException {
        File result = createTempFile(prefix, suffix); //Create temporary file
        if (!result.delete()) //Delete it
            throw new IOException("Cannot delete temporary file");
        if (!result.mkdir()) //Make directory with same name
            throw new IOException("Cannot create temporary directory");
        result.deleteOnExit();
        return result;
    }

    public static File createTempDirectory(String prefix) throws IOException {
        return createTempDirectory(prefix, "");
    }
}
