package com.ericsson.becrux.base.common.utils;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.*;

/**
 * Helper class for converting String.
 *
 * @author DungB
 */
public final class StringHelper {

    private final static SecureRandom randomGenerator = new SecureRandom();

    /**
     * Read a property file to Map.
     * File content format should be :
     *  key1=value1
     *  key2=value2
     *
     * @param absolutePath the path must be absolute path
     * @return new Map<key, value>
     * @throws Exception if anything fail
     */
    public static Map<String,String> readParamfiles(String absolutePath) throws Exception {
        Map<String, String> params = new HashMap<>();
        Properties properties = new Properties();

        // load file content into properties
        FileInputStream fileInputStream = new FileInputStream(absolutePath);
        properties.load(fileInputStream);

        // map properties to Map
        for(String key : properties.stringPropertyNames()) {
            params.put(key, properties.getProperty(key));
        }

        return params;
    }

    /**
     * Get build variables fill to the String.
     * The Params calling format: ${char}.
     * @param params params for mapping
     * @param x the string need to fill data
     * @return
     */
    public static String handleString(@Nonnull Map<String, String> params, @Nonnull String x) {
        StrSubstitutor substitutor = new StrSubstitutor(params);
        substitutor.setEnableSubstitutionInVariables(true);

        return substitutor.replace(x);
    }

    /**
     * Check if string has correct parameter format '${?}'.
     * @param x
     * @return
     */
    public static boolean isParameterFormat(String x) {
        return "${".equals(x.substring(0,2)) && "}".equals(x.substring(x.length() -1, x.length()));
    }
    
    /**
     * Parse the value of a json parameter
     * @param var - variable to look for
     * @param json - content of json
     * @return
     */
    public static String parseParameterJsonFormatValue(String var, Object json) throws Exception {
    	for (String val : ((String)json).split(",")) {
            String strip = val.replace("{", "").replace("}", "").trim();
            if (strip.contains(var))
                return strip.substring(strip.indexOf(":") + 1, strip.length());
        }

        throw new Exception("Value not found");
    }

    /**
     * Generate new ID will be {tag}_{time stamp}_{random Long number}.
     * Time stamp will have format 'MM-dd-yyyy-HH:mm:ss-z' and in UTC time
     * @param tag default prefix for ID
     * @return
     */
    public synchronized static String generateID(String tag) {
        StringBuilder id = new StringBuilder();

        String timeStamp = DateTime.now().withZone(DateTimeZone.UTC).toString("MM-dd-yyyy-HH:mm:ss-z");
        String randomNumber = String.valueOf(randomGenerator.nextLong());

        if(tag != null && !tag.isEmpty()) { id.append(tag).append("_"); }

        id.append("").append(timeStamp).append("_").append(randomNumber);

        return id.toString();
    }

    /**
     * Get list from string
     * The String will have format: 'TYPE1, TYPE2, ...'
     * @param value the String value to convert
     * @return List of String
     * */
    public static List<String> convertStringToList(String value) {
        value = value.replace(" ", "");

        List<String> types = Arrays.asList(value.split(","));

        return types;
    }
}
