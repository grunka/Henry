package se.grunka.henry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class BasicTypesDeserializer implements JsonDeserializer<Object> {
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isString()) {
                return primitive.getAsString();
            } else if (primitive.isNumber()) {
                return primitive.getAsNumber();
            }
        } else if (json.isJsonObject()) {
            return context.deserialize(json, new TypeToken<Map<String, Object>>() {
            }.getType());
        } else if (json.isJsonArray()) {
            return context.deserialize(json, new TypeToken<List<Object>>() {
            }.getType());
        } else if (json.isJsonNull()) {
            return null;
        }
        throw new JsonParseException("Non basic type used");
    }
}
