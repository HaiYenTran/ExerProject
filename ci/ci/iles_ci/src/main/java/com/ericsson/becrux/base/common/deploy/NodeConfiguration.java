package com.ericsson.becrux.base.common.deploy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Review how to use, relationship
 */
public abstract class NodeConfiguration {
    public static final String VNF_NAME = "vnf_name";
    public static final String VNF_FILE = "vnf_file";

    public NodeConfiguration() {
    }

    public abstract void validateArguments(List<String> parameters) throws Exception;

    public void checkUrls(List<String> parameters) throws IOException {
        generateMap(parameters);
    }

    public Map<String, String> generateMap(List<String> parameters) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String param : parameters) {

            URL url = new URL(param);
            try (InputStream in = url.openStream()) {
                Properties properties = new Properties();
                properties.load(in);

                properties.keySet().stream().map(k -> (String) k).forEach(k -> map.put(k, properties.getProperty(k)));
            }
        }
        return map;
    }

    public Map<String, String> generatePartialParameters(Map<String, String> map) {
        Pattern pattern = Pattern.compile("^(" + map.get(VNF_NAME) + "_)(.*)$");
        Map<String, String> preservedMap = new HashMap<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            Matcher matcher = pattern.matcher(entry.getKey());
            while (matcher.find()) {
                String matched = (matcher.group(2) != null) ? matcher.group(2) : "";
                if (!matched.isEmpty()) {
                    preservedMap.put(matched, entry.getValue());
                }
            }
        }
        return preservedMap;
    }
}
