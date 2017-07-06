package com.ericsson.becrux.base.common.data;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by zguntom on 5/2/17.
 */
public class VersionSerializer implements JsonSerializer<Version>, JsonDeserializer<Version> {
    @Override
    public JsonElement serialize(Version src, Type typeOfSrc, JsonSerializationContext ctxt) {
        JsonObject result = new JsonObject();
        result.add("version", new JsonPrimitive(src.getVersion()));
        result.add("type", new JsonPrimitive(src.getVersionType().toString()));
        return result;
    }

    @Override
    public Version deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            return Version.createVersion(jsonObject.get("version").getAsString(),
                                         VersionType.get(jsonObject.get("type").getAsString()));
        }
        catch (Exception e) {
            System.out.println("Failed to deserialize Version: " + e.getMessage());
            return null;
        }
    }
}
