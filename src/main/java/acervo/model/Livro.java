package acervo.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Representa um livro dentro do acervo pessoal de leitura.
 * Guarda as informações principais (título, autor, gênero) e
 * o controle de status de leitura (quero ler, lendo, lido).
 */
public class Livro {

    // ===================== ATRIBUTOS =====================

    private String id;                  // identificador único do livro, gerado automaticamente
    private String titulo;
    private String autor;
    private Genero genero;
    private StatusLeitura status;
    private int nota;
    private LocalDate dataCadastro;
    private LocalDate dataConclusao;


    // ===================== CONSTRUTOR =====================

    /**
     * Cria um novo livro já com id gerado automaticamente,
     * status inicial "Quero_ler" e data de cadastro igual à data atual.
     */
    public Livro(String titulo, String autor, Genero genero ){
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.status = StatusLeitura.Quero_ler;
        this.dataCadastro = LocalDate.now();

    }

    // ===================== GETTERS E SETTERS =====================
    // Como todos os atributos são "private" (encapsulamento), o acesso de fora da classe
    // só é possível através desses métodos:
    // - GET  -> permite apenas LER o valor do atributo
    // - SET  -> permite ALTERAR o valor do atributo
    // Isso protege a classe de mudanças diretas e descontroladas nos seus dados.

    public String getId() {
        return id;
    }
    // Sem setId(): um identificador não deve ser alterado após a criação do livro

    public String getTitulo() {
        return titulo;
    }


    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    public String getAutor() {
        return autor;
    }


    public void setAutor(String autor) {
        this.autor = autor;
    }


    public Genero getGenero() {
        return genero;
    }


    public void setGenero(Genero genero) {
        this.genero = genero;
    }


    public StatusLeitura getStatus() {
        return status;
    }


    public void setStatus(StatusLeitura status) {
        this.status = status;
    }


    public int getNota() {
        return nota;
    }


    public void setNota(int nota) {
        this.nota = nota;
    }


    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public LocalDate getDataConclusao() {
        return dataConclusao;
    }


    public void setDataConclusao(LocalDate dataConclusao) {
        this.dataConclusao = dataConclusao;
    }


    // ===================== TOSTRING =====================

    /**
     * Representação textual do livro, útil para debug e para exibição
     * no histórico de atividades e na listagem do terminal.
     */
    @Override
    public String toString() {
        return "Livro{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", genero=" + genero +
                ", status=" + status +
                ", nota=" + nota +
                ", dataCadastro=" + dataCadastro +
                ", dataConclusao=" + dataConclusao +
                '}';
    
    }
    

}