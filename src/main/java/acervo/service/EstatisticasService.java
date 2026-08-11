package acervo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import acervo.model.Genero;
import acervo.model.Livro;
import acervo.model.StatusLeitura;

public class EstatisticasService {

    // Conta quantos livros existem por gênero (todos os status)
    public Map<Genero, Integer> contarPorGenero(List<Livro> livros) {
        Map<Genero, Integer> contagem = new HashMap<>();

        for (Livro livro : livros) {
            Genero genero = livro.getGenero();
            contagem.put(genero, contagem.getOrDefault(genero, 0) + 1);
        }

        return contagem;
    }

    // Conta quantos livros existem por status (LIDO /  QUERO_LER)
    public Map<StatusLeitura, Integer> contarPorStatus(List<Livro> livros) {
        Map<StatusLeitura, Integer> contagem = new HashMap<>();

        for (Livro livro : livros) {
            StatusLeitura status = livro.getStatus();
            contagem.put(status, contagem.getOrDefault(status, 0) + 1);
        }

        return contagem;
    }

    // Total de livros com status LIDO
    public int totalLido(List<Livro> livros) {
        int total = 0;

        for (Livro livro : livros) {
            if (livro.getStatus() == StatusLeitura.LIDO) {
                total++;
            }
        }

        return total;
    }

    /**
     * Para cada gênero, calcula sua porcentagem sobre o TOTAL DE LIVROS LIDOS
     * (não sobre o total cadastrado). Ex: 10 lidos, 3 de Economia -> 30%.
     * Denominador fixo em "lidos" evita que a métrica seja manipulável
     * só cadastrando livros sem ler.
     */
    public Map<Genero, Double> porcentagemGeneroEntreLidos(List<Livro> livros) {
        int totalLidos = totalLido(livros);

        // Conta lidos por gênero
        Map<Genero, Integer> lidosPorGenero = new HashMap<>();
        for (Livro livro : livros) {
            if (livro.getStatus() == StatusLeitura.LIDO) {
                Genero genero = livro.getGenero();
                lidosPorGenero.put(genero, lidosPorGenero.getOrDefault(genero, 0) + 1);
            }
        }

        Map<Genero, Double> porcentagens = new HashMap<>();

        // Evita divisão por zero se ainda não há nenhum livro lido
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

    // Total de livros lidos em um mês/ano específicos (usa dataConclusao)
    public int totalLidoNoMes(List<Livro> livros, int mes, int ano) {
        int total = 0;

        for (Livro livro : livros) {
            if (livro.getStatus() == StatusLeitura.LIDO && livro.getDataConclusao() != null) {
                if (livro.getDataConclusao().getMonthValue() == mes
                        && livro.getDataConclusao().getYear() == ano) {
                    total++;
                }
            }
        }

        return total;
    }

    // Total de livros lidos em um ano específico (usa dataConclusao)
    public int totalLidoNoAno(List<Livro> livros, int ano) {
        int total = 0;

        for (Livro livro : livros) {
            if (livro.getStatus() == StatusLeitura.LIDO && livro.getDataConclusao() != null) {
                if (livro.getDataConclusao().getYear() == ano) {
                    total++;
                }
            }
        }

        return total;
    }
}