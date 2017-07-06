package com.ericsson.eiffel.becrux.data;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.eiffel.becrux.versions.Version;

public class Int extends Component implements Comparable<Object> {

	public static enum TestSuiteParameters {
		ITR_TGZ_LOCATION("intTgzLocation"),
		ITR_TGZ_NAME("intTgzName");
		
		private String paramName;
		
		TestSuiteParameters(String name) {
			this.paramName = name;
		}
		
		@Override
		public String toString() {
			return paramName;
		}
	}
	
	public Int() {
		super(getId(Int.class));
	}
	
	public Int(Version version) {
		super(getId(Int.class), version);
	}
	
	public Int(Version version, String artifact, List<String> parameters) {
		super(getId(Int.class), version, artifact, parameters);
	}
	
	public Int(Version version, Component.State state) {
		super(getId(Int.class), version, state);
	}
	
	public Map<String, String> getTestSuiteParameters() throws MalformedURLException {
		
		Map<String, String> map = new HashMap<>();
		
		if (getArtifact() == null || getArtifact().isEmpty())
			throw new IllegalArgumentException("Missing nodes artifact");
		if (getType() == null)
			throw new IllegalArgumentException("Missing nodes type");
		
		URL url = new URL(getArtifact());
		File file = new File(url.getPath());
		
		map.put(TestSuiteParameters.ITR_TGZ_LOCATION.toString(),file.getParent()+File.separatorChar);
		map.put(TestSuiteParameters.ITR_TGZ_NAME.toString(), file.getName());
		
		//TODO: add list of parameters: for (String param : parameters) { .... }
		
		return map;
	}
	
	@Override
	public int compareTo(Object o) {
		if (o == null)
			return 1;
		Int that = (Int) o;
		return this.getVersion().compareTo(that.getVersion());
	}

}
