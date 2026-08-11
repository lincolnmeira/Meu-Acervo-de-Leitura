package acervo.service;

import java.util.ArrayList;
import java.util.List;

import acervo.log.RegistroAtividade;

/**
 * Service: mantém o histórico de atividades (cadastros, remoções,
 * atualizações de status) em memória.
 *
 * OBS: os registros NÃO são persistidos em arquivo — existem apenas
 * durante a execução do programa. A cada reinício, o histórico reinicia.
 */
public class LogService {

    private List<RegistroAtividade> registros;

    // ===== CONSTRUTOR =====
    public LogService() {
        this.registros = new ArrayList<>();
    }

    // ===== MÉTODOS =====

    // Adiciona um novo registro ao histórico
    public void registrarAcao(RegistroAtividade registro) {
        registros.add(registro);
    }

    // Retorna todos os registros da sessão atual
    public List<RegistroAtividade> getRegistros() {
        return registros;
    }
}