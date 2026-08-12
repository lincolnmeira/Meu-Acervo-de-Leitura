package acervo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import acervo.model.Genero;
import acervo.model.Livro;
import acervo.model.StatusLeitura;

/**
 * Testes automatizados para a classe EstatisticasService.
 * Cada método anotado com @Test representa um cenário isolado,
 * verificando se a lógica de cálculo produz o resultado esperado.
 */
public class EstatisticasServiceTest {

    // Instância do serviço que vamos testar, recriada antes de cada teste
    private EstatisticasService estatisticasService;

    /**
     * @Before marca um método que roda ANTES de cada teste individual.
     * Isso garante que cada teste comece com um EstatisticasService "limpo",
     * sem interferência de testes anteriores.
     */
    @Before
    public void setUp() {
        estatisticasService = new EstatisticasService();
    }

    /**
     * Método auxiliar para criar rapidamente um Livro de teste,
     * já com o status desejado definido.
     * Evita repetir a mesma lógica de criação em cada teste.
     */
    private Livro criarLivro(String titulo, Genero genero, StatusLeitura status) {
        Livro livro = new Livro(titulo, "Autor Teste", genero);
        livro.setStatus(status);
        return livro;
    }

    @Test
    public void totalLidoDeveContarApenasLivrosComStatusLido() {
        // Arrange: monta uma lista com 2 livros lidos e 1 "quero ler"
        List<Livro> livros = new ArrayList<>();
        livros.add(criarLivro("Livro A", Genero.FICCAO, StatusLeitura.LIDO));
        livros.add(criarLivro("Livro B", Genero.ROMANCE, StatusLeitura.LIDO));
        livros.add(criarLivro("Livro C", Genero.POLITICA, StatusLeitura.QUERO_LER));

        // Act: chama o método que estamos testando
        int resultado = estatisticasService.totalLido(livros);

        // Assert: confirma que o resultado bate com o esperado
        assertEquals(2, resultado);
    }

    @Test
    public void totalLidoComListaVaziaDeveRetornarZero() {
        List<Livro> livros = new ArrayList<>();

        int resultado = estatisticasService.totalLido(livros);

        assertEquals(0, resultado);
    }

    @Test
    public void contarPorGeneroDeveAgruparCorretamente() {
        List<Livro> livros = new ArrayList<>();
        livros.add(criarLivro("Livro A", Genero.ECONOMIA, StatusLeitura.LIDO));
        livros.add(criarLivro("Livro B", Genero.ECONOMIA, StatusLeitura.QUERO_LER));
        livros.add(criarLivro("Livro C", Genero.FICCAO, StatusLeitura.LIDO));

        Map<Genero, Integer> resultado = estatisticasService.contarPorGenero(livros);

        // Verifica cada gênero individualmente
        assertEquals(Integer.valueOf(2), resultado.get(Genero.ECONOMIA));
        assertEquals(Integer.valueOf(1), resultado.get(Genero.FICCAO));
    }

    @Test
    public void porcentagemGeneroEntreLidosDeveCalcularCorretamente() {

        List<Livro> livros = new ArrayList<>();
        livros.add(criarLivro("Livro A", Genero.ECONOMIA, StatusLeitura.LIDO));
        livros.add(criarLivro("Livro B", Genero.ECONOMIA, StatusLeitura.LIDO));
        livros.add(criarLivro("Livro C", Genero.FICCAO, StatusLeitura.LIDO));

        Map<Genero, Double> resultado = estatisticasService.porcentagemGeneroEntreLidos(livros);

        // assertEquals com "delta" (0.5) porque estamos comparando números
        // decimais (double), que podem ter pequenas imprecisões de arredondamento
        assertEquals(66.67, resultado.get(Genero.ECONOMIA), 0.5);
        assertEquals(33.33, resultado.get(Genero.FICCAO), 0.5);
    }

    @Test
    public void porcentagemGeneroEntreLidosComNenhumLivroLidoDeveRetornarMapaVazio() {
        List<Livro> livros = new ArrayList<>();
        livros.add(criarLivro("Livro A", Genero.ECONOMIA, StatusLeitura.QUERO_LER));

        Map<Genero, Double> resultado = estatisticasService.porcentagemGeneroEntreLidos(livros);

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void totalLidoNoMesDeveContarApenasLivrosDoMesEAnoCorretos() {
        Livro livroAgosto = criarLivro("Livro Agosto", Genero.FICCAO, StatusLeitura.LIDO);
        livroAgosto.setDataConclusao(LocalDate.of(2026, 8, 15));

        Livro livroSetembro = criarLivro("Livro Setembro", Genero.FICCAO, StatusLeitura.LIDO);
        livroSetembro.setDataConclusao(LocalDate.of(2026, 9, 1));

        List<Livro> livros = new ArrayList<>();
        livros.add(livroAgosto);
        livros.add(livroSetembro);

        int resultado = estatisticasService.totalLidoNoMes(livros, 8, 2026);

        // Só o "Livro Agosto" deve ser contado
        assertEquals(1, resultado);
    }
}