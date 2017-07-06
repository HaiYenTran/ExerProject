package com.ericsson.eiffel.becrux.data;

import java.util.List;

import com.ericsson.eiffel.becrux.versions.Version;

public class Pcscf extends Node implements Comparable<Object> {
	
	public Pcscf() {
		super(getId(Pcscf.class));
	}
	
	public Pcscf(Version version) {
		super(getId(Pcscf.class), version);
	}

	public Pcscf(Version version, String artifact, List<String> parameters) {
		super(getId(Pcscf.class), version, artifact, parameters);
	}
	
	public Pcscf(Version version, Component.State state) {
		super(getId(Pcscf.class), version, state);
	}
	
	@Override
	public int compareTo(Object o) {
		if (o == null)
			return 1;
		Pcscf that = (Pcscf) o;
		return this.getVersion().compareTo(that.getVersion());
	}

}
