package acervo.api;

/**
 * DTO: formato padrão de erro da API. Toda resposta de erro (400, 404,
 * 409, etc.) usa esse formato, mantendo consistência com o restante
 * das respostas em JSON.
 */
public class ErroResponse {
    public String erro;

    public ErroResponse(String mensagem) {
        this.erro = mensagem;
    }
}