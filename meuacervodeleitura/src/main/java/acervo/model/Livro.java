package acervo.model;

import java.time.LocalDate;

public class Livro {
    // Atributos

    private String titulo;
    private String autor;
    private Genero genero;
    private StatusLeitura status;
    private int nota;
    private LocalDate dataCadastro;
    private LocalDate dataConclusao;


    // Construtor
    public Livro(String titulo, String autor, Genero genero ){
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.status = StatusLeitura.Quero_ler;
        this.dataCadastro = LocalDate.now();

    }

 // Getters e Setters
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

@Override
    public String toString() {
        return "Livro{" +
                "titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", genero=" + genero +
                ", status=" + status +
                ", nota=" + nota +
                ", dataCadastro=" + dataCadastro +
                ", dataConclusao=" + dataConclusao +
                '}';
    
    }
    







}
