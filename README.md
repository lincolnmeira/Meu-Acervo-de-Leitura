## 📚 Meu Acervo de Leitura

Sistema pessoal para cadastro e acompanhamento dos livros que já li, estou lendo ou pretendo ler. Gera estatísticas de leitura por gênero e status, além de um log de atividades a cada ação realizada. Possui API REST própria e uma interface web para gerenciar o acervo visualmente.

## 🎯 Objetivo

Diferente de um sistema de biblioteca tradicional (empréstimo/devolução para terceiros), este projeto é focado no **controle pessoal de leitura**, permitindo:

- Cadastrar livros com título, autor, gênero e status
- Marcar livros como lidos, atualizando estatísticas automaticamente
- Visualizar quantos livros já foram lidos, por gênero
- Gerar um log automático de todas as ações realizadas
- Gerenciar tudo por uma interface web simples, consumindo a API REST

## 🚀 Funcionalidades

- [x] Cadastro de livros
- [x] Classificação por gênero (Ficção, Romance, Política, etc.)
- [x] Controle de status (Lido / Quero Ler / Lendo)
- [x] Estatísticas de leitura
- [x] Log de atividades
- [x] Persistência de dados em JSON
- [x] Menu interativo via terminal
- [x] API REST (Javalin)
- [x] Front-end web (HTML/CSS/JS) consumindo a API

## 🛠️ Tecnologias

- **Java 21** (POO, estruturas de dados)
- **Maven** (gerenciamento de dependências e build)
- **Gson** (serialização/persistência em JSON)
- **Javalin** (API REST)
- **HTML, CSS e JavaScript puro** (front-end)

## 📁 Estrutura do Projeto

```
Meu-Acervo-de-Leitura/
├── docs/
│   ├── diagrama-uml.png
│   └── frontendmeuacervodeleitura.png
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── script.js
├── src/
│   ├── main/java/acervo/
│   │   ├── api/            # Endpoints da API REST (Javalin)
│   │   ├── log/             # Registro de atividades
│   │   ├── model/          # Classes de domínio (Livro, Genero, StatusLeitura)
│   │   ├── service/        # Regras de negócio e persistência
│   │   └── Main.java       # Menu interativo via terminal
│   └── test/java/
├── data/                   # Gerado em runtime (ignorado pelo git)
│   └── livros.json
├── .gitignore
├── pom.xml
└── README.md
```
## 📐 Diagrama UML

[Diagrama UML](docs/diagrama-uml.png)

## ▶️ Como executar

### Pré-requisitos
- Java 21+
- Maven

### Clonar o repositório
\`\`\`bash
git clone git@github.com:lincolnmeira/Meu-Acervo-de-Leitura.git
cd Meu-Acervo-de-Leitura
\`\`\`

### Opção 1: Menu interativo via terminal
\`\`\`bash
mvn clean compile
mvn exec:java -Dexec.mainClass="acervo.Main"
\`\`\`

### Opção 2: API REST + interface web

**1. Suba a API:**
\`\`\`bash
mvn clean compile
mvn exec:java -Dexec.mainClass="acervo.api.ApiServer"
\`\`\`
O servidor sobe em \`http://localhost:7000\`.

**2. Abra o front-end:**
Com a API rodando, abra o arquivo \`frontend/index.html\` diretamente no navegador (duplo clique, ou usando a extensão Live Server do VSCode).

## 🔌 Endpoints da API

| Método | Rota | Descrição |
|--------|------|-----------|
| GET    | \`/livros\` | Lista todos os livros cadastrados |
| POST   | \`/livros\` | Cadastra um novo livro |
| PUT    | \`/livros/{id}/status\` | Atualiza o status de leitura de um livro |
| DELETE | \`/livros/{id}\` | Remove um livro do acervo |
| GET    | \`/estatisticas\` | Retorna estatísticas de leitura (total lido, por gênero, por status) |
| GET    | \`/estatisticas/mes-ano?mes=8&ano=2026\` | Retorna totais lidos no mês e no ano informados |

### Exemplo de cadastro (POST /livros)
\`\`\`json
{
  "titulo": "Sapiens",
  "autor": "Yuval Noah Harari",
  "genero": "CIENCIAS_EXATAS"
}
\`\`\`

## 🖥️ Interface

A interface web permite cadastrar, visualizar, marcar como lido e remover livros, além de acompanhar estatísticas de leitura em tempo real. Livros lidos recebem destaque visual (selo verde), reforçando a sensação de progresso a cada leitura concluída.

[Tela principal](docs/frontendmeuacervodeleitura.png)

## 🔧 Melhorias futuras

- [ ] Persistir o histórico de atividades entre execuções
- [ ] Metas pessoais de leitura e gráficos anuais
- [ ] Ampliar os testes para serviços, persistência e endpoints da API
- [ ] Autenticação e deploy da API e do front-end

O cadastro valida título, autor e gênero e retorna `400 Bad Request` para dados inválidos. A atualização de status também valida o corpo e o valor do status. Os livros são persistidos em `data/livros.json`; o histórico de atividades permanece em memória e é reiniciado ao encerrar o programa.

Se a gravação dos livros falhar, as operações de alteração são desfeitas em memória e a API responde com `500 Internal Server Error`.
## 📝 Licença

Este projeto é de uso livre para fins de estudo e portfólio.
