package acervo.api;

import acervo.model.StatusLeitura;

/**
 * Representa os dados que chegam no corpo da requisição
 * PUT /livros/{id}/status -> contém apenas o novo status desejado.
 */
public class AtualizarStatusRequest {
    
    public StatusLeitura status;
}