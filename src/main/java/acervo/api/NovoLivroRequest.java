package acervo.api;

import acervo.model.Genero;

/**
 * Representa os dados que chegam no corpo da requisição POST /livros.
 * Usada apenas para receber a entrada da API — o objeto Livro "de verdade"
 * é construído a partir daqui, passando pelo construtor da classe Livro
 * (garantindo id, status inicial e data de cadastro corretos).
 *
 * TODO: o Gson faz correspondência exata (case-sensitive) do texto recebido
 * com as constantes do enum Genero. Se o valor enviado não bater exatamente
 * (ex: "politica" em vez de "POLITICA"), o campo genero fica silenciosamente
 * null, sem erro. Melhorar: validar/normalizar a entrada e retornar 400
 * Bad Request com mensagem clara quando o gênero for inválido.
 */
public class NovoLivroRequest {
    public String titulo;
    public String autor;
    public Genero genero;
}