package acervo.service;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public JsonElement serialize(LocalDate data, Type tipo, JsonSerializationContext contexto) {
        return new JsonPrimitive(data.format(FORMATO));
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type tipo, JsonDeserializationContext contexto) {
        return LocalDate.parse(json.getAsString(), FORMATO);
    }
}