# Meu Acervo de Leitura

Aplicação pessoal para organizar livros e acompanhar uma jornada de leitura. O projeto reúne uma API REST em Java, uma interface web e um menu de terminal, com os dados do acervo salvos localmente em JSON.

## Sobre o projeto

O Meu Acervo de Leitura nasceu como um projeto de estudo de Programação Orientada a Objetos. A ideia é tornar simples o registro de livros e transformar o acompanhamento das leituras em uma experiência visual e motivadora.

## Funcionalidades

- Cadastrar livros com título, autor e gênero.
- Registrar mês e ano de conclusão ao cadastrar um livro já lido ou ao atualizar seu status.
- Organizar livros nos status **Quero ler**, **Lendo** e **Lido**.
- Consultar estatísticas gerais e anuais, incluindo totais mensais e por gênero.
- Acompanhar a jornada com gráficos, mês em destaque, conquistas e meta anual.
- Usar a aplicação pela interface web ou pelo menu de terminal.
- Salvar o acervo localmente em `data/livros.json`.

## Tecnologias

- Java 21 e Maven
- Javalin 6 para a API REST
- Gson para JSON
- HTML, CSS e JavaScript sem framework
- JUnit 4 para testes automatizados

## Como executar

### Pré-requisitos

- JDK 21 ou superior
- Maven

### Interface web e API

Na raiz do projeto, inicie a API:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="acervo.api.ApiServer"
```

A API ficará disponível em `http://localhost:7000`. Com ela em execução, abra `frontend/index.html` no navegador ou use uma extensão como Live Server.

### Menu de terminal

```bash
mvn exec:java -Dexec.mainClass="acervo.Main"
```

### Testes

```bash
mvn test
```

## Rotas principais da API

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/livros` | Lista os livros do acervo. |
| `POST` | `/livros` | Cadastra um livro. |
| `PUT` | `/livros/{id}/status` | Atualiza o status e, quando aplicável, o mês/ano de conclusão. |
| `DELETE` | `/livros/{id}` | Remove um livro. |
| `GET` | `/estatisticas` | Retorna estatísticas gerais do acervo. |
| `GET` | `/estatisticas/mes-ano?mes=8&ano=2026` | Consulta leituras de um mês e ano. |
| `GET` | `/estatisticas/ano?ano=2026` | Retorna o resumo anual por mês e gênero. |

## Estrutura do projeto

```text
├── docs/                         # Documentação, mockup, imagens e diagrama UML
├── frontend/                     # Interface web (HTML, CSS e JavaScript)
├── src/main/java/acervo/
│   ├── api/                       # API REST e objetos de requisição/resposta
│   ├── log/                       # Registro de atividades da sessão
│   ├── model/                     # Livro, gênero e status de leitura
│   ├── service/                   # Regras de negócio, estatísticas e JSON
│   └── Main.java                  # Menu de terminal
├── src/test/java/                 # Testes automatizados
├── data/                          # Arquivo local do acervo (gerado em execução)
├── pom.xml                        # Dependências e configuração Maven
└── README.md
```

## Escopo atual

O projeto foi desenvolvido para uso local e pessoal. Ele não possui contas de usuário nem sincronização entre dispositivos. A meta anual fica no armazenamento local do navegador; os livros ficam em `data/livros.json`.

Para conhecer as etapas de implementação e o contexto do diagrama UML, consulte [Documentação da evolução](docs/EVOLUCAO.md).

O conceito visual inicial da página de jornada está em [docs/mockup-minha-jornada.svg](docs/mockup-minha-jornada.svg).

## Licença

Projeto de uso livre para fins de estudo e portfólio.
