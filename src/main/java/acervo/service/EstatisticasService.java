package acervo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import acervo.model.Genero;
import acervo.model.Livro;
import acervo.model.StatusLeitura;

public class EstatisticasService {

    public Map<Genero, Integer> contarPorGenero(List<Livro> livros) {
        Map<Genero, Integer> contagem = new HashMap<>();

        for (Livro livro : livros) {
            Genero genero = livro.getGenero();
            contagem.put(genero, contagem.getOrDefault(genero, 0) + 1);
        }

        return contagem;
    }

    public Map<StatusLeitura, Integer> contarPorStatus(List<Livro> livros) {
        Map<StatusLeitura, Integer> contagem = new HashMap<>();

        for (Livro livro : livros) {
            StatusLeitura status = livro.getStatus();
            contagem.put(status, contagem.getOrDefault(status, 0) + 1);
        }

        return contagem;
    }

    public int totalLido(List<Livro> livros) {
        int total = 0;

        for (Livro livro : livros) {
            if (livro.getStatus() == StatusLeitura.Lido) {
                total++;
            }
        }

        return total;
    }

    /**
     * Calcula, para cada gênero, qual porcentagem ele representa dentro
     * do total de livros já lidos (não do total geral cadastrado).
     * Exemplo: se você leu 10 livros no total, e 3 são de Economia,
     * o resultado para Economia é 30.0 (30%).
     *
     * Essa métrica reflete o perfil real de leitura da pessoa, já que
     * o denominador é sempre o total de livros efetivamente lidos —
     * cadastrar livros sem ler não influencia o resultado.
     */
    public Map<Genero, Double> porcentagemGeneroEntreLidos(List<Livro> livros) {
        int totalLidos = totalLido(livros);

        // Conta quantos livros LIDOS existem, por gênero
        Map<Genero, Integer> lidosPorGenero = new HashMap<>();
        for (Livro livro : livros) {
            if (livro.getStatus() == StatusLeitura.Lido) {
                Genero genero = livro.getGenero();
                lidosPorGenero.put(genero, lidosPorGenero.getOrDefault(genero, 0) + 1);
            }
        }

        // Calcula a porcentagem de cada gênero sobre o total de lidos
        Map<Genero, Double> porcentagens = new HashMap<>();

        // Evita divisão por zero caso ainda não haja nenhum livro lido
        if (totalLidos == 0) {
            return porcentagens;
        }

        for (Genero genero : lidosPorGenero.keySet()) {
            int lidosDoGenero = lidosPorGenero.get(genero);
            double porcentagem = (lidosDoGenero * 100.0) / totalLidos;
            porcentagens.put(genero, porcentagem);
        }

        return porcentagens;
    }


/**
 * Conta quantos livros foram concluídos (status Lido) em um determinado
 * mês e ano, com base na dataConclusao de cada livro.
 * Exemplo: totalLidoNoMes(livros, 8, 2026) retorna quantos livros
 * foram lidos em agosto de 2026.
 */
public int totalLidoNoMes(List<Livro> livros, int mes, int ano) {
    int total = 0;

    for (Livro livro : livros) {
        if (livro.getStatus() == StatusLeitura.Lido && livro.getDataConclusao() != null) {
            if (livro.getDataConclusao().getMonthValue() == mes
                    && livro.getDataConclusao().getYear() == ano) {
                total++;
            }
        }
    }

    return total;
}

/**
 * Conta quantos livros foram concluídos (status Lido) em um determinado
 * ano, com base na dataConclusao de cada livro.
 */
public int totalLidoNoAno(List<Livro> livros, int ano) {
    int total = 0;

    for (Livro livro : livros) {
        if (livro.getStatus() == StatusLeitura.Lido && livro.getDataConclusao() != null) {
            if (livro.getDataConclusao().getYear() == ano) {
                total++;
            }
        }
    }

    return total;
}

}