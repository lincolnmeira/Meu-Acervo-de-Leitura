package acervo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import acervo.log.RegistroAtividade;
import acervo.model.Genero;
import acervo.model.Livro;
import acervo.model.StatusLeitura;

public class AcervoService {

    private List<Livro> livros;
    private LogService logService;

    public AcervoService() {
        this.livros = new ArrayList<>();
        this.logService = new LogService();
    }

    public void adicionarLivro(Livro livro) {
        livros.add(livro);
        logService.registrarAcao(new RegistroAtividade(
                "ADICIONAR_LIVRO",
                "Livro adicionado: " + livro.getTitulo()
        ));
    }

    public void removerLivro(Livro livro) {
        livros.remove(livro);
        logService.registrarAcao(new RegistroAtividade(
                "REMOVER_LIVRO",
                "Livro removido: " + livro.getTitulo()
        ));
    }

    public void atualizarStatus(Livro livro, StatusLeitura status) {
        livro.setStatus(status);

        if (status == StatusLeitura.Lido) {
            livro.setDataConclusao(java.time.LocalDate.now());
        }

        logService.registrarAcao(new RegistroAtividade(
                "ATUALIZAR_STATUS",
                "Livro '" + livro.getTitulo() + "' -> status: " + status
        ));
    }

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