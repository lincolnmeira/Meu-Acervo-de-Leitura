package acervo.service;

import java.util.ArrayList;
import java.util.List;
import acervo.log.RegistroAtividade;

public class LogService {
    
    private List<RegistroAtividade> registros;

    //Construtor
    public LogService() {
        this.registros = new ArrayList<>();
    }

    //Método para adicionar um registro de atividade
    public void registrarAcao(RegistroAtividade registro){
        registros.add(registro);
    }

    public List<RegistroAtividade> getRegistros() {
        return registros;
    }

}
