# 📚 Meu Acervo de Leitura

Sistema pessoal para cadastro e acompanhamento dos livros que já li, estou lendo ou pretendo ler. Gera estatísticas de leitura por gênero e status, além de um log de atividades a cada ação realizada.

## 🎯 Objetivo

Diferente de um sistema de biblioteca tradicional (empréstimo/devolução para terceiros), este projeto é focado no **controle pessoal de leitura**, permitindo:

- Cadastrar livros com título, autor, gênero e status
- Marcar livros como lidos, atualizando estatísticas automaticamente
- Visualizar quantos livros já foram lidos, por gênero
- Gerar um log automático de todas as ações realizadas
- Consumir os dados via API REST (JSON)

## 🚀 Funcionalidades

- [x] Cadastro de livros
- [x] Classificação por gênero (Ficção, Romance, Política, etc.)
- [x] Controle de status (Lido / Quero Ler / Lendo)
- [x] Estatísticas de leitura
- [x] Log de atividades
- [x] Persistência de dados em JSON
- [x] Menu interativo via terminal
- [x] API REST (Javalin)
- [ ] Dashboard front-end

## 🛠️ Tecnologias

- **Java 21** (POO, estruturas de dados)
- **Maven** (gerenciamento de dependências e build)
- **Gson** (serialização/persistência em JSON)
- **Javalin** (API REST)
- HTML, CSS e JavaScript (front-end — em desenvolvimento)

## 📁 Estrutura do Projeto

\`\`\`
Meu-Acervo-de-Leitura/
├── docs/
│   └── diagrama-uml.png
├── src/
│   ├── main/java/acervo/
│   │   ├── api/            # Endpoints da API REST (Javalin)
│   │   ├── log/            # Registro de atividades
│   │   ├── model/          # Classes de domínio (Livro, Genero, StatusLeitura)
│   │   ├── service/        # Regras de negócio e persistência
│   │   └── Main.java       # Menu interativo via terminal
│   └── test/java/
├── data/                   # Gerado em runtime (ignorado pelo git)
│   └── livros.json
├── .gitignore
├── pom.xml
└── README.md
\`\`\`

## 📐 Diagrama UML

![Diagrama UML](docs/diagrama-uml.png)

## ▶️ Como executar

### Pré-requisitos
- Java 21+
- Maven

### Clonar o repositório
\`\`\`bash
git clone git@github.com:lincolnmeira/Meu-Acervo-de-Leitura.git
cd Meu-Acervo-de-Leitura
\`\`\`

### Rodar o menu interativo (terminal)
\`\`\`bash
mvn clean compile
mvn exec:java -Dexec.mainClass="acervo.Main"
\`\`\`

### Rodar a API REST
\`\`\`bash
mvn clean compile
mvn exec:java -Dexec.mainClass="acervo.api.ApiServer"
\`\`\`
O servidor sobe em \`http://localhost:7000\`.

## 🔌 Endpoints da API

| Método | Rota | Descrição |
|--------|------|-----------|
| GET    | \`/livros\` | Lista todos os livros cadastrados |
| POST   | \`/livros\` | Cadastra um novo livro |
| PUT    | \`/livros/{id}/status\` | Atualiza o status de leitura de um livro |
| DELETE | \`/livros/{id}\` | Remove um livro do acervo |
| GET    | \`/estatisticas\` | Retorna estatísticas de leitura (total lido, por gênero, por status) |

### Exemplo de cadastro (POST /livros)
\`\`\`json
{
  "titulo": "Sapiens",
  "autor": "Yuval Noah Harari",
  "genero": "CIENCIAS_EXATAS"
}
\`\`\`

## 🔧 Melhorias Futuras

- [ ] Validação de entrada mais robusta na API — atualmente, se o campo `genero` enviado no `POST /livros` não corresponder exatamente (case-sensitive) a uma constante do enum `Genero`, o campo fica silenciosamente `null` em vez de retornar um erro claro. Próximo passo: validar a entrada e responder com `400 Bad Request` e mensagem explicativa quando o gênero for inválido.
- [ ] Testes automatizados (JUnit)
- [ ] Autenticação na API
- [ ] Dashboard front-end (HTML/CSS/JS) consumindo a API REST
- [ ] Deploy da API (Render/Railway)

## 📸 Prints

*(adicionar screenshots do dashboard aqui quando estiver pronto)*

## 📝 Licença

Este projeto é de uso livre para fins de estudo e portfólio.