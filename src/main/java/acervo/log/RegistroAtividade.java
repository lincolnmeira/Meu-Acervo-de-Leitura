package acervo.log;

import java.time.LocalDateTime;

/**
 * Log: registra uma ação realizada no acervo (cadastro, remoção,
 * atualização de status), usado no histórico de atividades.
 */
public class RegistroAtividade {

    // ===== ATRIBUTOS =====
    private String descricao;      // texto legível, ex: "Livro adicionado: Sapiens"
    private String tipoAcao;       // código da ação, ex: "ADICIONAR_LIVRO"
    private LocalDateTime timestamp;

    // ===== CONSTRUTOR =====
    public RegistroAtividade(String tipoAcao, String descricao) {
        this.tipoAcao = tipoAcao;
        this.descricao = descricao;
        this.timestamp = LocalDateTime.now();
    }

    // ===== GETTERS =====
    // Sem setters: um registro de log não deve ser alterado após criado
    public String getDescricao() {
        return descricao;
    }

    public String getTipoAcao() {
        return tipoAcao;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // ===== TOSTRING =====
    @Override
    public String toString() {
        return "RegistroAtividade{" +
                "descricao='" + descricao + '\'' +
                ", tipoAcao='" + tipoAcao + '\'' +
                ", DataHora=" + timestamp +
                '}';
    }
}