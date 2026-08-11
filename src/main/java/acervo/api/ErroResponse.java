package acervo.api;

/**
 * Representa o formato padrão de resposta de erro da API.
 * Usada em todos os endpoints para manter consistência: toda resposta
 * de erro (400, 404, etc.) retorna um JSON com esse formato, em vez
 * de texto puro.
 */
public class ErroResponse {
    public String erro;

    public ErroResponse(String mensagem) {
        this.erro = mensagem;
    }
}