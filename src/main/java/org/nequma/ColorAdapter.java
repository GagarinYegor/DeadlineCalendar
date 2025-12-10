package org.nequma;
import com.google.gson.*;
import java.awt.*;
import java.lang.reflect.Type;

public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
    @Override
    public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("r", color.getRed());
        object.addProperty("g", color.getGreen());
        object.addProperty("b", color.getBlue());
        object.addProperty("alpha", color.getAlpha());
        return object;
    }

    @Override
    public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        int r = object.get("r").getAsInt();
        int g = object.get("g").getAsInt();
        int b = object.get("b").getAsInt();
        int alpha = object.get("alpha").getAsInt();
        return new Color(r, g, b, alpha);
    }
}