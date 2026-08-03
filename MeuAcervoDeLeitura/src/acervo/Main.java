package acervo;

import acervo.model.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Livro livro = new Livro("Dom Casmurro", "Machado de Assis", Genero.ROMANCE);
        System.out.println(livro);
    }
}