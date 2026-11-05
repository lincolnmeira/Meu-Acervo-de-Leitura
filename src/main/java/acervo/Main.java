package acervo;

import java.util.List;
import java.util.Scanner;

import acervo.model.Genero;
import acervo.model.Livro;
import acervo.model.StatusLeitura;
import acervo.service.AcervoService;
import acervo.service.EstatisticasService;

/**
 * Classe principal do sistema.
 */
public class Main {

    // ===== ATRIBUTOS =====
    private static Scanner scanner = new Scanner(System.in);
    private static AcervoService acervoService = new AcervoService();
    private static EstatisticasService estatisticasService = new EstatisticasService();

    // ===== MÉTODO PRINCIPAL =====
    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            exibirMenu();
            int opcao = lerOpcao();

            switch (opcao) {
                case 1 -> cadastrarLivro();
                case 2 -> listarLivros();
                case 3 -> marcarComoLido();
                case 4 -> removerLivro();
                case 5 -> verEstatisticas();
                case 6 -> verHistoricoAtividades();
                case 7 -> verPorcentagemPorGenero();
                case 8 -> verLidosPorMesAno();
                case 0 -> {
                    System.out.println("\nAté a próxima leitura!");
                    continuar = false;
                }
                default -> System.out.println("\n[!] Opção inválida, tente novamente.");
            }
        }

        scanner.close();
    }

    // ===== MENU PADRÃO =====
    private static void exibirMenu() {
        System.out.println("\n========================================");
        System.out.println("          MEU ACERVO DE LEITURA");
        System.out.println("========================================");
        System.out.println("1 - Cadastrar livro");
        System.out.println("2 - Listar livros");
        System.out.println("3 - Marcar livro como lido");
        System.out.println("4 - Remover livro");
        System.out.println("5 - Ver estatísticas");
        System.out.println("6 - Ver histórico de atividades");
        System.out.println("7 - Ver porcentagem por gênero (dos livros lidos)");
        System.out.println("8 - Ver livros lidos por mês/ano");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    // Retorna -1 se a entrada não for um número válido
    private static int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ===== CADASTRAR LIVRO =====
    private static void cadastrarLivro() {

        System.out.println("\n--- Cadastrar novo livro ---");

        System.out.print("Título: ");
        String titulo = scanner.nextLine().trim();

        System.out.print("Autor: ");
        String autor = scanner.nextLine().trim();

        Genero genero = escolherGenero();
        if (genero == null)
            return;

        Livro livro = new Livro(titulo, autor, genero);
        boolean adicionado;
        try {
            adicionado = acervoService.adicionarLivro(livro);
        } catch (IllegalStateException e) {
            System.out.println("[!] Não foi possível salvar o livro. Verifique o acesso à pasta data.");
            return;
        }

        if (adicionado) {
            System.out.println("[OK] Livro cadastrado com sucesso!");
        } else {
            System.out.println("[!] Livro já cadastrado: " + titulo);
        }
        // OBS: se o título já existir, AcervoService recusa silenciosamente
        // (só imprime aviso no console) — essa mensagem de sucesso aparece
        // mesmo assim. Ajustar quando adicionarLivro() passar a retornar status.
    }

    // Lista os gêneros numerados e retorna o escolhido (ou null se inválido)
    private static Genero escolherGenero() {
        Genero[] generos = Genero.values();

        System.out.println("\nEscolha o gênero:");
        for (int i = 0; i < generos.length; i++) {
            System.out.println((i + 1) + " - " + generos[i]);
        }
        System.out.print("Opção: ");

        try {
            int indice = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (indice < 0 || indice >= generos.length) {
                System.out.println("[!] Gênero inválido, cadastro cancelado.");
                return null;
            }
            return generos[indice];
        } catch (NumberFormatException e) {
            System.out.println("[!] Entrada inválida, cadastro cancelado.");
            return null;
        }
    }

    // ===== LISTAR LIVROS =====
    private static void listarLivros() {
        List<Livro> livros = acervoService.getLivros();

        System.out.println("\n--- Seus livros ---");
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado ainda.");
            return;
        }

        for (int i = 0; i < livros.size(); i++) {
            Livro l = livros.get(i);
            System.out.printf("%d - %s (%s) | Gênero: %s | Status: %s%n",
                    i + 1, l.getTitulo(), l.getAutor(), l.getGenero(), l.getStatus());
        }
    }

    // ===== MARCAR COMO LIDO =====
    private static void marcarComoLido() {
        List<Livro> livros = acervoService.getLivros();

        if (livros.isEmpty()) {
            System.out.println("\nNenhum livro cadastrado ainda.");
            return;
        }

        listarLivros();
        System.out.print("\nDigite o número do livro que deseja marcar como lido: ");

        Livro livro = selecionarLivro(livros);
        if (livro == null)
            return;

        try {
            acervoService.atualizarStatus(livro, StatusLeitura.LIDO);
        } catch (IllegalStateException e) {
            System.out.println("[!] Não foi possível salvar a alteração. Verifique o acesso à pasta data.");
            return;
        }
        System.out.println("[OK] Livro marcado como lido!");
    }

    // ===== REMOVER LIVRO =====
    private static void removerLivro() {
        List<Livro> livros = acervoService.getLivros();

        if (livros.isEmpty()) {
            System.out.println("\nNenhum livro cadastrado ainda.");
            return;
        }

        listarLivros();
        System.out.print("\nDigite o número do livro que deseja remover: ");

        Livro livro = selecionarLivro(livros);
        if (livro == null)
            return;

        try {
            acervoService.removerLivro(livro);
        } catch (IllegalStateException e) {
            System.out.println("[!] Não foi possível salvar a remoção. Verifique o acesso à pasta data.");
            return;
        }
        System.out.println("[OK] Livro removido!");
    }

    // Reaproveitado por marcarComoLido() e removerLivro()
    private static Livro selecionarLivro(List<Livro> livros) {
        try {
            int indice = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (indice < 0 || indice >= livros.size()) {
                System.out.println("[!] Número inválido.");
                return null;
            }
            return livros.get(indice);
        } catch (NumberFormatException e) {
            System.out.println("[!] Entrada inválida.");
            return null;
        }
    }

    // ===== ESTATÍSTICAS =====
    private static void verEstatisticas() {
        List<Livro> livros = acervoService.getLivros();

        System.out.println("\n--- Estatísticas ---");
        System.out.println("Total de livros lidos: " + estatisticasService.totalLido(livros));
        System.out.println("\nPor gênero:");
        System.out.println(estatisticasService.contarPorGenero(livros));
        System.out.println("\nPor status:");
        System.out.println(estatisticasService.contarPorStatus(livros));
    }

    // ===== PORCENTAGEM POR GÊNERO (ENTRE OS LIDOS) =====
    private static void verPorcentagemPorGenero() {
        List<Livro> livros = acervoService.getLivros();

        System.out.println("\n--- Porcentagem por gênero (entre os livros lidos) ---");

        var porcentagens = estatisticasService.porcentagemGeneroEntreLidos(livros);

        if (porcentagens.isEmpty()) {
            System.out.println("Nenhum livro lido ainda.");
            return;
        }

        for (var entrada : porcentagens.entrySet()) {
            System.out.printf("%s: %.1f%%%n", entrada.getKey(), entrada.getValue());
        }
    }

    // ===== HISTÓRICO DE ATIVIDADES =====
    private static void verHistoricoAtividades() {
        System.out.println("\n--- Histórico de atividades ---");
        List<acervo.log.RegistroAtividade> historico = acervoService.getHistoricoAtividades();

        if (historico.isEmpty()) {
            System.out.println("Nenhuma atividade registrada ainda.");
            return;
        }

        for (var registro : historico) {
            System.out.println(registro);
        }
    }

    // ===== LIDOS POR MÊS/ANO =====
    private static void verLidosPorMesAno() {
        List<Livro> livros = acervoService.getLivros();

        System.out.print("\nDigite o ano (ex: 2026): ");
        int ano = lerInteiro();
        if (ano == -1)
            return;

        System.out.print("Digite o mês (1-12): ");
        int mes = lerInteiro();
        if (mes == -1 || mes < 1 || mes > 12) {
            System.out.println("[!] Mês inválido.");
            return;
        }

        int totalNoMes = estatisticasService.totalLidoNoMes(livros, mes, ano);
        int totalNoAno = estatisticasService.totalLidoNoAno(livros, ano);

        System.out.println("\n--- Livros lidos ---");
        System.out.println("No mês " + mes + "/" + ano + ": " + totalNoMes);
        System.out.println("No ano " + ano + ": " + totalNoAno);
    }

    // Reaproveitado por verLidosPorMesAno()
    private static int lerInteiro() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] Entrada inválida.");
            return -1;
        }
    }
}
