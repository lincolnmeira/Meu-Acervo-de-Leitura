package acervo.model;

/**
 * Model: status de leitura de um livro.
 * Definido no construtor de Livro como QUERO_LER, e atualizado
 * via PUT /livros/{id}/status.
 */

public enum StatusLeitura {
    LIDO,
    LENDO,
    QUERO_LER
}