package acervo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.YearMonth;
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

    @Test
    public void resumoAnualDeveContarApenasLeiturasConcluidasNoAnoEPreencherOsDozeMeses() {
        Livro janeiro = criarLivro("Livro de janeiro", Genero.FICCAO, StatusLeitura.LIDO);
        janeiro.setMesAnoConclusao(YearMonth.of(2026, 1));
        Livro janeiroDois = criarLivro("Outro de janeiro", Genero.ROMANCE, StatusLeitura.LIDO);
        janeiroDois.setMesAnoConclusao(YearMonth.of(2026, 1));
        Livro fevereiro = criarLivro("Livro de fevereiro", Genero.FICCAO, StatusLeitura.LIDO);
        fevereiro.setMesAnoConclusao(YearMonth.of(2026, 2));
        Livro outroAno = criarLivro("Livro de outro ano", Genero.POLITICA, StatusLeitura.LIDO);
        outroAno.setMesAnoConclusao(YearMonth.of(2025, 1));
        Livro aindaLendo = criarLivro("Livro em andamento", Genero.ECONOMIA, StatusLeitura.LENDO);
        aindaLendo.setMesAnoConclusao(YearMonth.of(2026, 3));

        List<Livro> livros = List.of(janeiro, janeiroDois, fevereiro, outroAno, aindaLendo);

        Map<Integer, Integer> porMes = estatisticasService.contarLidosPorMesNoAno(livros, 2026);
        Map<Genero, Integer> porGenero = estatisticasService.contarLidosPorGeneroNoAno(livros, 2026);

        assertEquals(12, porMes.size());
        assertEquals(Integer.valueOf(2), porMes.get(1));
        assertEquals(Integer.valueOf(1), porMes.get(2));
        assertEquals(Integer.valueOf(0), porMes.get(3));
        assertEquals(3, estatisticasService.totalLidoNoAno(livros, 2026));
        assertEquals(Integer.valueOf(2), porGenero.get(Genero.FICCAO));
        assertEquals(Integer.valueOf(1), porGenero.get(Genero.ROMANCE));
        assertFalse(porGenero.containsKey(Genero.POLITICA));
        assertFalse(porGenero.containsKey(Genero.ECONOMIA));
    }

    @Test
    public void resumoAnualSemLeiturasDeveRetornarDozeMesesZeradosESemGeneros() {
        Map<Integer, Integer> porMes = estatisticasService.contarLidosPorMesNoAno(List.of(), 2026);
        Map<Genero, Integer> porGenero = estatisticasService.contarLidosPorGeneroNoAno(List.of(), 2026);

        assertEquals(12, porMes.size());
        assertTrue(porMes.values().stream().allMatch(total -> total == 0));
        assertTrue(porGenero.isEmpty());
    }
}
