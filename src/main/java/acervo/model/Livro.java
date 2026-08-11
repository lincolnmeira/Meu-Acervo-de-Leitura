package acervo.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Model: representa um livro do acervo.
 * Guarda dados principais (título, autor, gênero) e status de leitura.
 */
public class Livro {

    // ===== ATRIBUTOS =====
    private String id;              // UUID gerado automaticamente no construtor
    private String titulo;
    private String autor;
    private Genero genero;
    private StatusLeitura status;   // Quero_ler / Lido
    private int nota;
    private LocalDate dataCadastro;
    private LocalDate dataConclusao; // preenchida só quando status vira Lido

    // ===== CONSTRUTOR =====
    // Gera id automático, status inicial fixo (Quero_ler) e data de cadastro = hoje
    public Livro(String titulo, String autor, Genero genero) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.status = StatusLeitura.QUERO_LER;
        this.dataCadastro = LocalDate.now();
    }

    // ===== GETTERS E SETTERS =====
    // get = ler atributo | set = alterar atributo (encapsulamento)

    public String getId() {
        return id;
    }
    // Sem setId(): id não muda após a criação

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
}