package acervo.service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Service: ensina o Gson a converter LocalDate <-> JSON como texto simples
 * (ex: "2026-08-11"), no formato ISO (yyyy-MM-dd).
 *
 * Necessário porque, sem esse adapter, o Gson tenta usar reflexão para
 * acessar os campos internos e privados do LocalDate (uma classe do
 * próprio Java) e o Java moderno bloqueia esse acesso por segurança
 * (JsonIOException: Failed making field ... accessible).
 *
 * Usado tanto no ArquivoJsonService (persistência em arquivo) quanto
 * no ApiServer (respostas da API).
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ISO_LOCAL_DATE;

    // Java -> JSON: converte LocalDate em texto (ex: 2026-08-11)
    @Override
    public JsonElement serialize(LocalDate data, Type tipo, JsonSerializationContext contexto) {
        return new JsonPrimitive(data.format(FORMATO));
    }

    // JSON -> Java: converte texto de volta em LocalDate
    @Override
    public LocalDate deserialize(JsonElement json, Type tipo, JsonDeserializationContext contexto) {
        return LocalDate.parse(json.getAsString(), FORMATO);
    }
}