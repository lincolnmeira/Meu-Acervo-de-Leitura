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

    // Scanner compartilhado para ler as entradas do usuário no terminal
    private static Scanner scanner = new Scanner(System.in);

    // Serviço responsável por gerenciar o acervo (adicionar, remover, listar livros)
    private static AcervoService acervoService = new AcervoService();

    // Serviço responsável por calcular estatísticas de leitura (contagens, totais)
    private static EstatisticasService estatisticasService = new EstatisticasService();


    // ===================== MÉTODO PRINCIPAL =====================

    /**
     * Ponto de entrada do programa.
     * Mantém o menu em loop até o usuário escolher sair (opção 0).
     */
    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            exibirMenu();
            int opcao = lerOpcao();

            // Direciona para o método correspondente à opção escolhida
            switch (opcao) {
                case 1 -> cadastrarLivro();
                case 2 -> listarLivros();
                case 3 -> marcarComoLido();
                case 4 -> removerLivro();
                case 5 -> verEstatisticas();
                case 6 -> verHistoricoAtividades();
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

    /**
     * Exibe as opções do menu principal no terminal.
     */
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
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    /**
     * Lê a opção digitada pelo usuário e converte para número.
     * Retorna -1 caso o usuário digite algo que não seja um número válido,
     * evitando que o programa quebre com uma exceção.
     */
    private static int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    // ===================== MÉTODO PARA CADASTRAR LIVRO =====================

    /**
     * Coleta os dados de um novo livro (título, autor, gênero) e
     * delega o cadastro ao AcervoService.
     */
    private static void cadastrarLivro() {
        System.out.println("\n--- Cadastrar novo livro ---");

        System.out.print("Título: ");
        String titulo = scanner.nextLine().trim();

        System.out.print("Autor: ");
        String autor = scanner.nextLine().trim();

        Genero genero = escolherGenero();
        if (genero == null) return; // cadastro cancelado por entrada inválida

        Livro livro = new Livro(titulo, autor, genero);
        acervoService.adicionarLivro(livro);
        System.out.println("[OK] Livro cadastrado com sucesso!");
    }

    /**
     * Exibe a lista de gêneros disponíveis (enum Genero) numerada,
     * e retorna o gênero escolhido pelo usuário.
     * Retorna null se a escolha for inválida.
     */
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

    /**
     * Lista todos os livros cadastrados, numerados, com título,
     * autor, gênero e status de leitura.
     */
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

    /**
     * Exibe a lista de livros, pede ao usuário qual deseja marcar como lido,
     * e atualiza o status através do AcervoService.
     */
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

    /**
     * Exibe a lista de livros, pede ao usuário qual deseja remover,
     * e delega a remoção ao AcervoService.
     */
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

    /**
     * Método auxiliar reutilizado por marcarComoLido() e removerLivro().
     * Lê o número digitado pelo usuário e retorna o Livro correspondente
     * na lista, ou null se a entrada for inválida.
     */
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

    /**
     * Exibe estatísticas de leitura: total de livros lidos,
     * contagem por gênero e contagem por status.
     */
    private static void verEstatisticas() {
        List<Livro> livros = acervoService.getLivros();

        System.out.println("\n--- Estatísticas ---");
        System.out.println("Total de livros lidos: " + estatisticasService.totalLido(livros));
        System.out.println("\nPor gênero:");
        System.out.println(estatisticasService.contarPorGenero(livros));
        System.out.println("\nPor status:");
        System.out.println(estatisticasService.contarPorStatus(livros));
    }


    // ===================== MÉTODO PARA VER HISTÓRICO =====================

    /**
     * Exibe o histórico de atividades registradas pelo LogService
     * (cadastros, remoções e atualizações de status).
     */
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