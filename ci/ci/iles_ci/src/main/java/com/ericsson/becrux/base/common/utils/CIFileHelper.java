package com.ericsson.becrux.base.common.utils;

import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The file helper.
 *
 * @author DungB
 */
public class CIFileHelper {

    /**
     * Validate the File access.
     * @param path the file path
     * @param readable can be readable
     * @param executable can be executable
     * @param isAbsolutePath is absolute path
     * @param isDirectory is directory
     * @return
     * @throws IOException
     */
    public static boolean validateFile(String path, boolean readable, boolean writeable, boolean executable, boolean isAbsolutePath, boolean isDirectory) throws IOException {
        try {
            Path p = Paths.get(path);
            File f = p.toFile();
            if (isAbsolutePath && !f.isAbsolute())
                throw new Exception("The Path is not absolute path.");
            if (!f.exists())
                throw new FileNotFoundException(path + " doesn't exist.");
            if (isDirectory && !f.isDirectory())
                throw new Exception("No such directory " + path);
            if (!isDirectory && !f.isFile())
                throw new Exception("Not found file " + path);
            if (readable && !f.canRead())
                throw new IOException(path + " cannot be read.");
            if (writeable && !f.canWrite())
                throw new IOException(path + " cannot be write.");
            if (executable && !f.canExecute())
                throw new IOException(path + " cannot be executed.");

        } catch (Exception ex) {
            throw new IOException(ex);
        }
        return true;
    }

    public static String validateExecutableFile(String path, boolean executable, boolean required) throws IOException {
        if (path == null || path.isEmpty()) {
            if (required)
                throw new NullPointerException("Path not provided.");
            else
                return "";
        }

        try {
            Path p = Paths.get(path);
            File f = p.toFile();
            if (!f.exists())
                throw new FileNotFoundException(path + " doesn't exist");
            if (!f.canRead())
                throw new IOException(path + " cannot be read");
            if (executable && !f.canExecute())
                throw new IOException(path + " cannot be executed");
        } catch (Exception ex) {
            throw new IOException(ex);
        }
        return path;
    }

    /**
     * Synchronize between two folder.
     * If the file not exit from other folder > create
     * If 2 folder has same file and overrideNewestFile == true, the old file will be overriden
     * @param source the folder to synchronize
     * @param target the folder for synchronize
     * @param overrideNewestFile
     * @return
     */
    public static boolean oneWaySynchronizeFilesBetweenTwoFolder(@Nonnull String source, @Nonnull String target, boolean overrideNewestFile) throws Exception{
        File sourceDir = new File(source);
        File targetDir = new File(target);

        // sychronize from source to target.
        // if overrideNewestFile == true, files on source will override files on target
        List<String> sourceFiles = listFileNamesForFolder(sourceDir);
        List<String> targetFiles = listFileNamesForFolder(targetDir);

        for (String filePath : sourceFiles) {
            File fileForCopy = new File(filePath);
            File oldFile = new File(targetDir, fileForCopy.getName());

            if (!targetFiles.contains(filePath)
                    || (overrideNewestFile && (fileForCopy.lastModified() > oldFile.lastModified()))) {
                // copy if file not exit
                FileUtils.copyFile(fileForCopy, oldFile);
            }
        }

        return true;
    }

    /**
     * List all Files on a folder include sub folder.
     * @param folder
     * @return
     */
    public static List<File> listFilesForFolder(@Nonnull final File folder) {
        List<File> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                files.addAll(listFilesForFolder(fileEntry));
            } else {
                files.add(fileEntry);
            }
        }

        return files;
    }

    /**
     * List all File names on a folder include sub folder.
     * @param folder
     * @return
     */
    public static List<String> listFileNamesForFolder(@Nonnull final File folder) {
        return listFilesForFolder(folder).stream().map(f -> f.getName()).collect(Collectors.toList());
    }
}
