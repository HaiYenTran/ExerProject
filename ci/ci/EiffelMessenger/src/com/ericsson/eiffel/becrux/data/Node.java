package com.ericsson.eiffel.becrux.data;

import java.util.List;

import com.ericsson.eiffel.becrux.versions.Version;

public abstract class Node extends Component {

	public Node(String type) {
		super(type);
	}

	public Node(String type, Version version) {
		super(type, version);
	}

	public Node(String type, Version version, String artifact, List<String> parameters) {
		super(type, version, artifact, parameters);
	}

	public Node(String type, Version version, Component.State state) {
		super(type, version, state);
	}

}