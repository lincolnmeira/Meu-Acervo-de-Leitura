package acervo.api;

import acervo.model.Genero;

/**
 * Representa os dados que chegam no corpo da requisição POST /livros.
 * Usada apenas para receber a entrada da API — o objeto Livro "de verdade"
 * é construído a partir daqui, passando pelo construtor da classe Livro
 * (garantindo id, status inicial e data de cadastro corretos).
 */
public class NovoLivroRequest {
    
    public String titulo;
    public String autor;
    public Genero genero;
}