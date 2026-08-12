package acervo.api;

import acervo.model.Genero;

/**
 * DTO: dados recebidos no corpo de POST /livros.
 * O objeto Livro "de verdade" é construído a partir daqui, passando
 * pelo construtor da classe (garantindo id, status inicial e data de
 * cadastro corretos).
 *
 * Validação: se o gênero enviado não corresponder a nenhuma constante
 * do enum (case-sensitive), o Gson deixa o campo silenciosamente null.
 * Essa possibilidade é tratada em ApiServer.configurarRotas() (POST /livros),
 * que retorna 400 Bad Request com a lista de gêneros válidos.
 */
public class NovoLivroRequest {
    public String titulo;
    public String autor;
    public Genero genero;
}