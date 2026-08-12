package acervo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import acervo.log.RegistroAtividade;
import acervo.model.Genero;
import acervo.model.Livro;
import acervo.model.StatusLeitura;

/**
 * Service: orquestra o acervo de livros — adicionar, remover, atualizar
 * status e listar. Toda alteração é persistida via ArquivoJsonService
 * e registrada via LogService.
 */
public class AcervoService {

    private List<Livro> livros;
    private LogService logService;
    private ArquivoJsonService arquivoJsonService;

    // Carrega os livros salvos ao iniciar (lista vazia se não houver arquivo ainda)
    public AcervoService() {
        this.arquivoJsonService = new ArquivoJsonService();
        this.livros = arquivoJsonService.carregar();
        this.logService = new LogService();
    }

    /**
     * Adiciona um livro, mas apenas se não existir outro com o mesmo título
     * já cadastrado (comparação sem diferenciar maiúsculas/minúsculas).
     * Retorna true se o livro foi adicionado, false se era duplicata.
     */
    public boolean adicionarLivro(Livro livro) {
        boolean jaExiste = livros.stream()
                .anyMatch(l -> l.getTitulo().equalsIgnoreCase(livro.getTitulo()));

        if (jaExiste) {
            return false;
        }

        livros.add(livro);
        arquivoJsonService.salvar(livros);
        logService.registrarAcao(new RegistroAtividade(
                "ADICIONAR_LIVRO",
                "Livro adicionado: " + livro.getTitulo()));
        return true;
    }

    // Remove um livro, persiste e registra no log
    public void removerLivro(Livro livro) {
        livros.remove(livro);
        arquivoJsonService.salvar(livros);
        logService.registrarAcao(new RegistroAtividade(
                "REMOVER_LIVRO",
                "Livro removido: " + livro.getTitulo()));
    }

    // Atualiza o status; se virar LIDO, marca a dataConclusao automaticamente
    public void atualizarStatus(Livro livro, StatusLeitura status) {
        livro.setStatus(status);

        if (status == StatusLeitura.LIDO) {
            livro.setDataConclusao(LocalDate.now());
        }

        arquivoJsonService.salvar(livros);
        logService.registrarAcao(new RegistroAtividade(
                "ATUALIZAR_STATUS",
                "Livro '" + livro.getTitulo() + "' -> status: " + status));
    }

    // Filtra os livros de um gênero específico
    public List<Livro> listarPorGenero(Genero genero) {
        return livros.stream()
                .filter(l -> l.getGenero() == genero)
                .collect(Collectors.toList());
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public List<RegistroAtividade> getHistoricoAtividades() {
        return logService.getRegistros();
    }
}