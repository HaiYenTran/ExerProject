package com.ericsson.becrux.iles.data;

import com.ericsson.becrux.base.common.data.Component;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents 'INT' IMS node.
 */
public class Int extends Component implements Comparable<Object> {

    private List<String> parameters;

    public Int() {
        super();

    }

    public Int(@Nonnull String version) {
        super(version);
        setInstallable(false);
    }

    public Int(@Nonnull String version, String artifact, List<String> parameters) {
        super(version, artifact);
        this.parameters = parameters;

        setInstallable(false);
    }

    public Int(@Nonnull String version, State state) {
        super(version, state);
        setInstallable(false);
    }


    public Map<String, String> getTestSuiteParameters() throws MalformedURLException {

        Map<String, String> map = new HashMap<>();

        if (getArtifact() == null || getArtifact().isEmpty())
            throw new IllegalArgumentException("Missing nodes artifact");
        if (getType() == null)
            throw new IllegalArgumentException("Missing nodes type");

        URL url = new URL(getArtifact());
        File file = new File(url.getPath());

        map.put(TestSuiteParameters.INT_TGZ_LOCATION.toString(), file.getParent() + File.separatorChar);
        map.put(TestSuiteParameters.INT_TGZ_NAME.toString(), file.getName());

        //TODO: add list of parameters: for (String param : parameters) { .... }

        return map;
    }

    public enum TestSuiteParameters {
        INT_TGZ_LOCATION("intTgzLocation"),
        INT_TGZ_NAME("intTgzName");

        private String paramName;

        TestSuiteParameters(String name) {
            this.paramName = name;
        }

        @Override
        public String toString() {
            return paramName;
        }
    }

}
