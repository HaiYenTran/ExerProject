package com.ericsson.eiffel.becrux.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ericsson.eiffel.becrux.versions.Version;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

public abstract class Component implements Comparable<Object> {
		
	public static enum State {
		NEW_BUILD, SUCCESSFUL_BUILD, FAILED_BUILD, BASELINE_CANDIDATE, BASELINE_APPROVED, BASELINE_REJECTED
	}
	
	private static transient Map<Class<?>, String> ids;
	private static transient Gson serializer;
	private String type;
	
	private State state;
	private String artifact;
	private List<String> parameters;
	private String version;
	
	private static void createIdsMap() {
		ids = new HashMap<>();
		ids.put(Mtas.class, Mtas.class.getSimpleName().toUpperCase());
		ids.put(Cscf.class, Cscf.class.getSimpleName().toUpperCase());
		ids.put(Pcscf.class, Pcscf.class.getSimpleName().toUpperCase());
		ids.put(Ibcf.class, Ibcf.class.getSimpleName().toUpperCase());
		ids.put(Int.class, Int.class.getSimpleName().toUpperCase());
	}
	
	private static Gson getSerializer() {
		if (serializer == null) {
			RuntimeTypeAdapterFactory<Component> factory = RuntimeTypeAdapterFactory
					.of(Component.class)
					.registerSubtype(Mtas.class, getId(Mtas.class))
					.registerSubtype(Cscf.class, getId(Cscf.class))
					.registerSubtype(Pcscf.class, getId(Pcscf.class))
					.registerSubtype(Ibcf.class, getId(Ibcf.class))
					.registerSubtype(Int.class, getId(Int.class));
			serializer = new GsonBuilder().registerTypeAdapterFactory(factory).create();
		}
		return serializer;
	}
	
	protected static Map<Class<?>, String> getIdsMap() {
		if (ids == null)
			createIdsMap();
		return ids;
	}
	
	protected static String getId(Class<?> clazz) {
		return getIdsMap().get(clazz);
	}
	
	public Component(String type) {
		this.type = type;
	}
	
	public Component(String type, Version version) {
		this.type = type;
		this.version = version.getVersion();
	}

	public Component( String type,  Version version, String artifact, 
			List<String> parameters) {
		this(type, version);
		this.artifact = artifact;
		this.parameters = parameters;
	}

	public Component( String type,  Version version, State state) {
		this(type, version);
		this.state = state;
	}
	
	public static Component getInstance(String type) {
		if (type == null)
			return null;
		else if (type.equals(getId(Mtas.class)))
			return new Mtas();
		else if (type.equals(getId(Cscf.class)))
			return new Cscf();
		else if (type.equals(getId(Pcscf.class)))
			return new Pcscf();
		else if (type.equals(getId(Ibcf.class)))
			return new Ibcf();
		else if (type.equals(getId(Int.class)))
			return new Int();
		else
			return null;
	}
	
	public String getType() {
		return type;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public String getArtifact() {
		return artifact;
	}
	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}
	public List<String> getParameters() {
		return parameters;
	}
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}
	public Version getVersion() {
		return Version.create(version);
	}
	public void setVersion(Version version) {
		this.version = version.getVersion();
	}
	
	@Override
	public String toString() {
		return getSerializer().toJson(this);
	}
	
	public static Component getInstancefromJsonString(String string) {
		return getSerializer().fromJson(string, new TypeToken<Component>(){}.getType());
	}
	
	public static String listToString(List<Component> components) {
		JsonObject json = new JsonObject();
		if (components != null) {			
			for (Component component : components) {
				if (component != null)
					json.add(component.getType(), new JsonParser().parse(component.toString()));
			}
		}
		return json.toString();
	}
	
	public static List<Component> stringToList(String string) {
		
		List<Component> components = new ArrayList<>();
		JsonElement element = new JsonParser().parse(string);
		JsonObject json;
		
		if (element.isJsonObject())
			json = new JsonParser().parse(string).getAsJsonObject();
		else
			return null;
		
		for (String type : getComponents()) {
			JsonObject j = json.getAsJsonObject(type);
			if (j != null) {
				components.add(Component.getInstancefromJsonString(j.toString()));			
			}
		}
		return components;
	}
	
	public static List<String> getComponents() {
		return new ArrayList<String>(getIdsMap().values());
	}
	
	public static List<String> getNodes() {
		List<String> c = new ArrayList<>();
		for (Entry<Class<?>, String> entry : getIdsMap().entrySet())
			if (entry.getKey().getSuperclass().equals(Node.class))
				c.add(entry.getValue());
		return c;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifact == null) ? 0 : artifact.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Component other = (Component) obj;
		if (artifact == null) {
			if (other.artifact != null)
				return false;
		} else if (!artifact.equals(other.artifact))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (state != other.state)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public int compareTo(Object o) {
		if (o == null)
			return 1;
		
		Component that = (Component)o;
		if (this.type.equals(that.getType()))
			return Version.create(this.version).compareTo(that.getVersion());
		
		return 0;
	}

}
