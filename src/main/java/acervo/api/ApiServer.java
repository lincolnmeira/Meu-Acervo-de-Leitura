package acervo.api;

import java.time.LocalDate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.javalin.Javalin;
import io.javalin.http.Context;

import acervo.model.Livro;
import acervo.service.AcervoService;
import acervo.service.LocalDateAdapter;

/**
 * Classe responsável por expor o AcervoService como uma API REST,
 * usando o framework Javalin.
 */
public class ApiServer {

    private final Javalin app;
    private final AcervoService acervoService;
    private final Gson gson;
    private final acervo.service.EstatisticasService estatisticasService;

    public ApiServer() {
        this.acervoService = new AcervoService();
        this.estatisticasService = new acervo.service.EstatisticasService();

        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        this.app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> it.anyHost());
            });
        });

        configurarRotas();
    }

    /**
     * Envia uma resposta de erro padronizada em JSON, usando o Gson
     * (em vez de ctx.json(), que exigiria a biblioteca Jackson, não usada
     * neste projeto). Centraliza o formato de erro em um único lugar,
     * evitando repetir a mesma lógica em cada rota.
     */
    private void enviarErro(Context ctx, int status, String mensagem) {
        ctx.status(status);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(new ErroResponse(mensagem)));
    }

    private void configurarRotas() {

        // ===== [GET] /livros -> lista todos os livros cadastrados =====
        app.get("/livros", ctx -> {
            var livros = acervoService.getLivros();
            ctx.contentType("application/json");
            ctx.result(gson.toJson(livros));
        });

        // ===== [GET] /estatisticas/mes-ano -> retorna total de livros lidos em um mês/ano específicos =====
        app.get("/estatisticas/mes-ano", ctx -> {
            var livros = acervoService.getLivros();

            String mesParam = ctx.queryParam("mes");
            String anoParam = ctx.queryParam("ano");

            if (mesParam == null || anoParam == null) {
                enviarErro(ctx, 400,
                    "Parâmetros 'mes' e 'ano' são obrigatórios. Exemplo: /estatisticas/mes-ano?mes=8&ano=2026");
                return;
            }

            try {
                int mes = Integer.parseInt(mesParam);
                int ano = Integer.parseInt(anoParam);

                var resposta = new java.util.HashMap<String, Object>();
                resposta.put("mes", mes);
                resposta.put("ano", ano);
                resposta.put("totalLidoNoMes", estatisticasService.totalLidoNoMes(livros, mes, ano));
                resposta.put("totalLidoNoAno", estatisticasService.totalLidoNoAno(livros, ano));

                ctx.contentType("application/json");
                ctx.result(gson.toJson(resposta));

            } catch (NumberFormatException e) {
                enviarErro(ctx, 400, "Os parâmetros 'mes' e 'ano' devem ser números.");
            }
        });

        // ===== [POST] /livros -> cadastra um novo livro =====
        app.post("/livros", ctx -> {
            NovoLivroRequest dados = gson.fromJson(ctx.body(), NovoLivroRequest.class);

            if (dados.titulo == null || dados.titulo.isBlank()) {
                enviarErro(ctx, 400, "O campo 'titulo' é obrigatório.");
                return;
            }

            if (dados.autor == null || dados.autor.isBlank()) {
                enviarErro(ctx, 400, "O campo 'autor' é obrigatório.");
                return;
            }

            if (dados.genero == null) {
                String generosValidos = java.util.Arrays.toString(acervo.model.Genero.values());
                enviarErro(ctx, 400, "Gênero inválido ou não informado. Valores aceitos: " + generosValidos);
                return;
            }

            Livro novoLivro = new Livro(dados.titulo, dados.autor, dados.genero);
            acervoService.adicionarLivro(novoLivro);

            ctx.contentType("application/json");
            ctx.status(201);
            ctx.result(gson.toJson(novoLivro));
        });

        // ===== [PUT] /livros/{id}/status -> atualiza o status de leitura de um livro =====
        app.put("/livros/{id}/status", ctx -> {
            String id = ctx.pathParam("id");

            Livro livro = acervoService.getLivros().stream()
                    .filter(l -> id.equals(l.getId()))
                    .findFirst()
                    .orElse(null);

            if (livro == null) {
                enviarErro(ctx, 404, "Livro não encontrado");
                return;
            }

            AtualizarStatusRequest dados = gson.fromJson(ctx.body(), AtualizarStatusRequest.class);
            acervoService.atualizarStatus(livro, dados.status);

            ctx.contentType("application/json");
            ctx.result(gson.toJson(livro));
        });

        // ===== [DELETE] /livros/{id} -> remove um livro do acervo =====
        app.delete("/livros/{id}", ctx -> {
            String id = ctx.pathParam("id");

            Livro livro = acervoService.getLivros().stream()
                    .filter(l -> id.equals(l.getId()))
                    .findFirst()
                    .orElse(null);

            if (livro == null) {
                enviarErro(ctx, 404, "Livro não encontrado");
                return;
            }

            acervoService.removerLivro(livro);
            ctx.status(204);
        });

        // ===== [GET] /estatisticas -> retorna estatísticas de leitura (por gênero, por status, total lido, % por gênero) =====
        app.get("/estatisticas", ctx -> {
            var livros = acervoService.getLivros();

            var resposta = new java.util.HashMap<String, Object>();
            resposta.put("totalLido", estatisticasService.totalLido(livros));
            resposta.put("porGenero", estatisticasService.contarPorGenero(livros));
            resposta.put("porStatus", estatisticasService.contarPorStatus(livros));
            resposta.put("porcentagemPorGenero", estatisticasService.porcentagemGeneroEntreLidos(livros));

            ctx.contentType("application/json");
            ctx.result(gson.toJson(resposta));
        });
    }

    public void iniciar(int porta) {
        app.start(porta);
    }

    public static void main(String[] args) {
        ApiServer servidor = new ApiServer();
        servidor.iniciar(7000);
    }
}