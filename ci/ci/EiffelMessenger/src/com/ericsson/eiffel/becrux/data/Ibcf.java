package com.ericsson.eiffel.becrux.data;

import java.util.List;

import com.ericsson.eiffel.becrux.versions.Version;

public class Ibcf extends Node implements Comparable<Object> {
	
	public Ibcf() {
		super(getId(Ibcf.class));
	}
	
	public Ibcf(Version version) {
		super(getId(Ibcf.class), version);
	}

	public Ibcf(Version version, String artifact, List<String> parameters) {
		super(getId(Ibcf.class), version, artifact, parameters);
	}
	
	public Ibcf(Version version, Component.State state) {
		super(getId(Ibcf.class), version, state);
	}
	
	@Override
	public int compareTo(Object o) {
		if (o == null)
			return 1;
		Ibcf that = (Ibcf) o;
		return this.getVersion().compareTo(that.getVersion());
	}
	
}
