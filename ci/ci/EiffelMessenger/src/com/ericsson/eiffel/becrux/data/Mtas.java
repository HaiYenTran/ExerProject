package com.ericsson.eiffel.becrux.data;

import java.util.List;

import com.ericsson.eiffel.becrux.versions.Version;

public class Mtas extends Node implements Comparable<Object> {
		
	public Mtas() {
		super(getId(Mtas.class));
	}
	
	public Mtas(Version version) {
		super(getId(Mtas.class), version);
	}

	public Mtas(Version version, String artifact, List<String> parameters) {
		super(getId(Mtas.class), version, artifact, parameters);
	}
	
	public Mtas(Version version, Component.State state) {
		super(getId(Mtas.class), version, state);
	}
	
	@Override
	public int compareTo(Object o) {
		if (o == null)
			return 1;
		Mtas that = (Mtas) o;
		return this.getVersion().compareTo(that.getVersion());
	}
	
}
