package acervo.service;

import acervo.model.Livro;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArquivoJsonService {

    private static final String CAMINHO_ARQUIVO = "data/livros.json";
    private final Gson gson;

    public ArquivoJsonService() {
        // Necessário para o Gson conseguir serializar/desserializar LocalDate corretamente
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    public void salvar(List<Livro> livros) {
        try {
            Path pasta = Path.of("data");
            if (!Files.exists(pasta)) {
                Files.createDirectories(pasta);
            }
            try (FileWriter writer = new FileWriter(CAMINHO_ARQUIVO)) {
                gson.toJson(livros, writer);
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar livros: " + e.getMessage());
        }
    }

    public List<Livro> carregar() {
        Path arquivo = Path.of(CAMINHO_ARQUIVO);
        if (!Files.exists(arquivo)) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(CAMINHO_ARQUIVO)) {
            Type tipoLista = new TypeToken<List<Livro>>() {}.getType();
            List<Livro> livros = gson.fromJson(reader, tipoLista);
            return livros != null ? livros : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Erro ao carregar livros: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}