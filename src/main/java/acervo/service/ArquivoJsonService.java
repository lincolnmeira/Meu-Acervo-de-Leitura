package acervo.service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import acervo.model.Livro;

/**
 * Service: persiste a lista de livros em data/livros.json usando Gson.
 * Chamado pelo AcervoService a cada cadastro/remoção/atualização,
 * e ao iniciar o programa (para carregar os dados salvos).
 */
public class ArquivoJsonService {

    private static final String CAMINHO_ARQUIVO = "data/livros.json";
    private final Gson gson;

    public ArquivoJsonService() {
        // LocalDateAdapter é obrigatório: sem ele, o Gson quebra ao tentar
        // serializar LocalDate via reflexão (ver bug documentado no LocalDateAdapter)
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(YearMonth.class, new YearMonthAdapter())
                .setPrettyPrinting()
                .create();
    }

    // Salva a lista completa de livros no arquivo, sobrescrevendo o conteúdo anterior.
    // Cria a pasta "data/" automaticamente se ainda não existir.
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

    // Carrega a lista de livros do arquivo. Retorna lista vazia (não null)
    // se o arquivo ainda não existir ou se ocorrer erro de leitura —
    // assim quem chama nunca precisa checar null.
    public List<Livro> carregar() {
        Path arquivo = Path.of(CAMINHO_ARQUIVO);
        if (!Files.exists(arquivo)) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(CAMINHO_ARQUIVO)) {
            // TypeToken necessário aqui porque o Gson não consegue inferir
            // sozinho o tipo genérico List<Livro> em tempo de execução
            // (limitação do Java: apagamento de tipo genérico / type erasure)
            Type tipoLista = new TypeToken<List<Livro>>() {}.getType();
            List<Livro> livros = gson.fromJson(reader, tipoLista);
            return livros != null ? livros : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Erro ao carregar livros: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
