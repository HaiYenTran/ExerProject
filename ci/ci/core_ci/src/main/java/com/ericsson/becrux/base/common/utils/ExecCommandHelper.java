package com.ericsson.becrux.base.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by TRONG.LE on 6/21/2017.
 */
public class ExecCommandHelper {

    /**
     * Execution the "readlink" command to get the symbolic link
     * @param : path of file
     * return soft link
     */
    public static String readSymbolicLink(String path)  throws Exception{
        String cmd = "readlink -f  " + path;

        System.out.println("Read symbolic link command : " + cmd);
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return bufferedReader.readLine();
    }
}
