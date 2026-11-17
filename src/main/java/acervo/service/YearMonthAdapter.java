package acervo.service;

import java.lang.reflect.Type;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/** Serializa YearMonth como texto ISO (yyyy-MM). */
public class YearMonthAdapter implements JsonSerializer<YearMonth>, JsonDeserializer<YearMonth> {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("uuuu-MM");

    @Override
    public JsonElement serialize(YearMonth value, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(value.format(FORMAT));
    }

    @Override
    public YearMonth deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
        return YearMonth.parse(json.getAsString(), FORMAT);
    }
}
