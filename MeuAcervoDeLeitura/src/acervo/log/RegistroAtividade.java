package acervo.log;

import java.time.LocalDateTime;

public class RegistroAtividade {
    
    //Atributos
    private String descricao;
    private String tipoAcao;
    private LocalDateTime timestamp;


    //Construtor
    public RegistroAtividade(String descricao, String tipoAcao){
        this.descricao = descricao;
        this.tipoAcao = tipoAcao;
        this.timestamp = LocalDateTime.now();
    }

    //Getters
    public String getDescricao() {
        return descricao;
    }


    public String getTipoAcao() {
        return tipoAcao;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    //
    @Override
    public String toString() {
        return "RegistroAtividade{" +
                "descricao='" + descricao + '\'' +
                ", tipoAcao='" + tipoAcao + '\'' +
                ", DataHora=" + timestamp +
                '}';
    }
    












}
