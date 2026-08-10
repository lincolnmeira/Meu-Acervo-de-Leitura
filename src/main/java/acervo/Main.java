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
 * Responsável por exibir o menu interativo no terminal e
 * direcionar as ações do usuário para os serviços correspondentes.
 */
public class Main {

    // ===================== ATRIBUTOS =====================

    private static Scanner scanner = new Scanner(System.in);
    private static AcervoService acervoService = new AcervoService();
    private static EstatisticasService estatisticasService = new EstatisticasService();


    // ===================== MÉTODO PRINCIPAL =====================

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
                case 7 -> verPorcentagemPorGenero(); // <<< ADICIONADO
                case 0 -> {
                    System.out.println("\nAté a próxima leitura!");
                    continuar = false;
                }
                default -> System.out.println("\n[!] Opção inválida, tente novamente.");
            }
        }

        scanner.close();
    }


    // ===================== MENU PADRÃO =====================

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
        System.out.println("7 - Ver porcentagem por gênero (dos livros lidos)"); // <<< ADICIONADO
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    // ===================== MÉTODO PARA CADASTRAR LIVRO =====================

    private static void cadastrarLivro() {
        System.out.println("\n--- Cadastrar novo livro ---");

        System.out.print("Título: ");
        String titulo = scanner.nextLine().trim();

        System.out.print("Autor: ");
        String autor = scanner.nextLine().trim();

        Genero genero = escolherGenero();
        if (genero == null) return;

        Livro livro = new Livro(titulo, autor, genero);
        acervoService.adicionarLivro(livro);
        System.out.println("[OK] Livro cadastrado com sucesso!");
    }

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


    // ===================== MÉTODO PARA LISTAR LIVROS =====================

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


    // ===================== MÉTODO PARA MARCAR COMO LIDO =====================

    private static void marcarComoLido() {
        List<Livro> livros = acervoService.getLivros();

        if (livros.isEmpty()) {
            System.out.println("\nNenhum livro cadastrado ainda.");
            return;
        }

        listarLivros();
        System.out.print("\nDigite o número do livro que deseja marcar como lido: ");

        Livro livro = selecionarLivro(livros);
        if (livro == null) return;

        acervoService.atualizarStatus(livro, StatusLeitura.Lido);
        System.out.println("[OK] Livro marcado como lido!");
    }


    // ===================== MÉTODO PARA REMOVER LIVRO =====================

    private static void removerLivro() {
        List<Livro> livros = acervoService.getLivros();

        if (livros.isEmpty()) {
            System.out.println("\nNenhum livro cadastrado ainda.");
            return;
        }

        listarLivros();
        System.out.print("\nDigite o número do livro que deseja remover: ");

        Livro livro = selecionarLivro(livros);
        if (livro == null) return;

        acervoService.removerLivro(livro);
        System.out.println("[OK] Livro removido!");
    }

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


    // ===================== MÉTODO PARA VER ESTATÍSTICAS =====================

    private static void verEstatisticas() {
        List<Livro> livros = acervoService.getLivros();

        System.out.println("\n--- Estatísticas ---");
        System.out.println("Total de livros lidos: " + estatisticasService.totalLido(livros));
        System.out.println("\nPor gênero:");
        System.out.println(estatisticasService.contarPorGenero(livros));
        System.out.println("\nPor status:");
        System.out.println(estatisticasService.contarPorStatus(livros));
    }


    // ===================== MÉTODO PARA VER PORCENTAGEM POR GÊNERO ===================== <<< ADICIONADO (seção inteira)

    /**
     * Exibe, para cada gênero, a porcentagem que ele representa
     * dentro do total de livros já lidos.
     */
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


    // ===================== MÉTODO PARA VER HISTÓRICO =====================

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
}