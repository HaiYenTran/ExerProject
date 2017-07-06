package com.ericsson.eiffel.becrux.data;

import java.util.List;

import com.ericsson.eiffel.becrux.versions.Version;

public class Cscf extends Node implements Comparable<Object> {
	
	public Cscf() {
		super(getId(Cscf.class));
	}
	
	public Cscf(Version version) {
		super(getId(Cscf.class), version);
	}

	public Cscf(Version version, String artifact, List<String> parameters) {
		super(getId(Cscf.class), version, artifact, parameters);
	}
	
	public Cscf(Version version, Component.State state) {
		super(getId(Cscf.class), version, state);
	}

	@Override
	public int compareTo(Object o) {
		if (o == null)
			return 1;
		Cscf that = (Cscf) o;
		return this.getVersion().compareTo(that.getVersion());
	}

}
