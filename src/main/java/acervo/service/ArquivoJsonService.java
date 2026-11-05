package acervo.service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.time.LocalDate;
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
                .setPrettyPrinting()
                .create();
    }

    // Salva a lista completa de livros no arquivo, sobrescrevendo o conteúdo anterior.
    // Cria a pasta "data/" automaticamente se ainda não existir.
    public void salvar(List<Livro> livros) {
        Path arquivo = Path.of(CAMINHO_ARQUIVO);
        Path temporario = arquivo.resolveSibling("livros.json.tmp");
        try {
            Files.createDirectories(arquivo.getParent());
            try (FileWriter writer = new FileWriter(temporario.toFile())) {
                gson.toJson(livros, writer);
            }
            try {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicMoveIndisponivel) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | RuntimeException e) {
            try {
                Files.deleteIfExists(temporario);
            } catch (IOException cleanupError) {
                e.addSuppressed(cleanupError);
            }
            if (e instanceof IllegalStateException falhaPersistencia) {
                throw falhaPersistencia;
            }
            throw new IllegalStateException("Não foi possível salvar os livros em " + arquivo + ".", e);
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
