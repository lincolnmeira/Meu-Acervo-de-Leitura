package acervo.api;

import acervo.model.StatusLeitura;

/**
 * DTO: dados recebidos no corpo de PUT /livros/{id}/status.
 * Contém apenas o novo status desejado.
 */
public class AtualizarStatusRequest {
    public StatusLeitura status;
}