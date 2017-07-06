package com.ericsson.eiffel.becrux.events;

import com.ericsson.duraci.eiffelmessage.messages.EiffelEvent;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import com.ericsson.eiffel.becrux.utils.EventValidationResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

public abstract class Event {

	protected static transient String productExtensionRegex = "^.*.((tar.gz)|(tgz)|(tar)|(ova)|(ova.gz))$";

	// Held internally to perform polymorphic deserialization from json
	private String type;

	// CIid for LEO
	private String buildId;

	private static transient Gson serializer;

	private transient EiffelMessage sourceEiffelMessage;

	private transient EiffelEvent sourceEiffelEvent;

	public static Gson getSerializer() {
		if(serializer == null) {
			RuntimeTypeAdapterFactory<Event> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
					.of(Event.class)
					.registerSubtype(NBPEvent.class, NBPEvent.class.getSimpleName())
					.registerSubtype(OPBEvent.class, OPBEvent.class.getSimpleName());

			serializer = new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).create();
		}
		return serializer;
	}

	public Event(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public EiffelMessage getSourceEiffelMessage() {
		return sourceEiffelMessage;
	}

	public void setSourceEiffelMessage(EiffelMessage sourceEiffelMessage) {
		this.sourceEiffelMessage = sourceEiffelMessage;
	}

	public EiffelEvent getSourceEiffelEvent() {
		if(sourceEiffelEvent == null && sourceEiffelMessage != null)
			return sourceEiffelMessage.getEvent();
		return sourceEiffelEvent;
	}

	public void setSourceEiffelEvent(EiffelEvent sourceEiffelEvent) {
		this.sourceEiffelEvent = sourceEiffelEvent;
	}

	public static Event fromJson(String json) {
		return getSerializer().fromJson(json, new TypeToken<Event>() {
		}.getType());
	}

	public String toJson() {
		return getSerializer().toJson(this);
	}

	public abstract EventValidationResult validate();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buildId == null) ? 0 : buildId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Event other = (Event) obj;
		if (buildId == null)
		{
			if (other.buildId != null)
				return false;
		} else if (!buildId.equals(other.buildId))
			return false;
		if (type == null)
		{
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return toJson();
	}

	public static String eventArrayToJson(Event[] arr) {
		return new Gson().toJson(arr);
	}

	public static Event getInstanceFromJsonString(String string) {
		return fromJson(string);
	}
}
