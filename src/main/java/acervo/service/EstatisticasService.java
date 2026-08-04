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
}