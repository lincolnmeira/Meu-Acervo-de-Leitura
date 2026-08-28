package acervo.model;

/**
 * IMPORTANTE: ao adicionar um gênero novo aqui, atualize também o
 * <select> do formulário em frontend/index.html. A API aceita
 * qualquer valor do enum automaticamente.
 */
public enum Genero {
    POLITICA,
    ECONOMIA,
    FINANCAS,        
    FICCAO,
    ROMANCE,
    AUTO_AJUDA,
    TECNOLOGIA,
    CIENCIAS_EXATAS,
    HISTORIA,
    BIOGRAFIA,
    FILOSOFIA,
    CLASSICO,
    POESIA,
    TERROR,
    FANTASIA,
    AUTOBIOGRAFIA,
    RELIGIOSO,
    MASCULINISTA
}