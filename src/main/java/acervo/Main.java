package acervo;

import acervo.model.*;
import acervo.service.AcervoService;
import acervo.service.EstatisticasService;

public class Main {
    public static void main(String[] args) throws Exception {
        AcervoService service = new AcervoService();

        Livro livro1 = new Livro("Dom Casmurro", "Machado de Assis", Genero.ROMANCE);
        Livro livro2 = new Livro("O Poder do Hábito", "Charles Duhigg", Genero.AUTO_AJUDA);

        service.adicionarLivro(livro1);
        service.adicionarLivro(livro2);
        service.atualizarStatus(livro1, StatusLeitura.Lido);
        service.removerLivro(livro2);

        System.out.println("\nHistórico de atividades:");
        for (var registro : service.getHistoricoAtividades()) {
            System.out.println(registro);
        }

        EstatisticasService estatisticas = new EstatisticasService();

System.out.println("\nContagem por gênero:");
System.out.println(estatisticas.contarPorGenero(service.getLivros()));

System.out.println("\nContagem por status:");
System.out.println(estatisticas.contarPorStatus(service.getLivros()));

System.out.println("\nTotal de livros lidos: " + estatisticas.totalLido(service.getLivros()));
    }
}