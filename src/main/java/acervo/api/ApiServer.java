package acervo.api;

import java.time.LocalDate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.javalin.Javalin;

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

    public ApiServer() {
        this.acervoService = new AcervoService();
        // Registra o adapter de LocalDate para o Gson conseguir converter
        // essas datas para JSON sem travar por causa da reflexão do Java
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        this.app = Javalin.create();

        configurarRotas();
    }

    /**
     * Define todos os endpoints (rotas) da API.
     */
    private void configurarRotas() {

        // GET /livros -> retorna a lista completa de livros em formato JSON
        app.get("/livros", ctx -> {
            var livros = acervoService.getLivros();
            ctx.contentType("application/json");
            ctx.result(gson.toJson(livros));
        });

        // POST /livros -> cadastra um novo livro a partir do JSON enviado no corpo da requisição
        app.post("/livros", ctx -> {
            Livro novoLivro = gson.fromJson(ctx.body(), Livro.class);
            acervoService.adicionarLivro(novoLivro);

            ctx.contentType("application/json");
            ctx.status(201); // 201 = "Created", código HTTP padrão para criação bem-sucedida
            ctx.result(gson.toJson(novoLivro));
        });
    }

    /**
     * Inicia o servidor na porta informada.
     */
    public void iniciar(int porta) {
        app.start(porta);
    }

    public static void main(String[] args) {
        ApiServer servidor = new ApiServer();
        servidor.iniciar(7000);
    }
}