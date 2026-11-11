# Meu Acervo de Leitura

Aplicação pessoal para organizar livros e acompanhar a leitura. O projeto combina um programa de terminal, uma API REST em Java e uma interface web em HTML, CSS e JavaScript.

## Objetivo

O acervo ajuda a registrar livros que você quer ler, está lendo ou já concluiu. A versão atual oferece cadastro, organização por gênero, atualização de status e estatísticas básicas. A próxima evolução planejada é uma página dedicada à jornada de leitura, com visualizações e conquistas.

## Estado atual do projeto

### Funcionalidades disponíveis

- Cadastro de livros com título, autor e gênero.
- Identificador único, status inicial `QUERO_LER` e data de cadastro gerados automaticamente.
- Prevenção de títulos duplicados, sem diferenciar maiúsculas de minúsculas.
- Listagem, remoção e atualização do status para `LIDO`, `LENDO` ou `QUERO_LER`.
- Registro automático da data de conclusão ao marcar um livro como lido.
- Estatísticas de total lido, contagem por gênero e status e percentual dos gêneros entre os livros lidos.
- Consulta de livros concluídos por mês e por ano.
- Persistência do acervo em `data/livros.json`.
- Registro de atividades durante a sessão atual.
- Menu interativo de terminal e interface web para cadastro e gerenciamento.
- Interface web com cards de livros, painel de estatísticas e consulta por mês/ano.
- Validação na API para cadastro, status e parâmetros da consulta mensal/anual.
- Gravação segura do arquivo JSON por arquivo temporário; em caso de falha, a alteração é revertida em memória e a API informa erro.
- Renderização dos dados dos livros como texto na interface, sem interpretar título e autor como HTML.

### Limitações conhecidas

- O histórico de atividades fica apenas em memória e recomeça quando o programa é reiniciado.
- A tela atual reúne cadastro, resumo de estatísticas e estante na mesma página.
- As estatísticas existentes não têm gráficos, metas anuais, conquistas ou uma visão detalhada da jornada.
- A aplicação não tem autenticação nem suporte a múltiplos usuários; foi feita para uso local e pessoal.
- Os testes automatizados existentes cobrem parte dos cálculos de estatísticas. Ainda falta cobertura para API, persistência e serviços do acervo.

## Próxima evolução: página “Minha jornada”

O mockup [mockup-minha-jornada.svg](mockup-minha-jornada.svg) apresenta a direção visual aprovada: manter a paleta creme, marrom e verde e separar a experiência em **Minha estante** e **Minha jornada**. O mockup é apenas uma proposta visual; nenhum dos componentes novos descritos abaixo está implementado ainda.

A página de jornada deverá tornar as estatísticas mais visuais e divertidas, incluindo:

- Resumo do ano com livros concluídos, progresso de meta e ritmo de leitura.
- Gráfico mensal de livros concluídos, com seleção de ano.
- Distribuição dos livros lidos por gênero em um gráfico visual.
- Conquistas por marcos de leitura, como concluir 25 livros ou explorar vários gêneros.
- Um destaque motivador ligado ao progresso do leitor.
- Registro do mês e do ano em que cada livro foi concluído, inclusive quando for cadastrado depois da leitura.

Páginas lidas e capas não fazem parte da primeira versão planejada: hoje esses dados não são armazenados no modelo `Livro`. Podemos decidir depois se vale adicioná-los.

## Plano de implementação

O trabalho será feito em etapas pequenas. Após cada etapa, o resultado poderá ser revisado antes de avançar.

### Etapa 1 — Separar a navegação e preparar a página

- Criar navegação entre **Minha estante** e **Minha jornada**.
- Manter cadastro e gerenciamento de livros na estante.
- Criar o esqueleto responsivo da jornada, seguindo o mockup e a identidade visual atual.
- Nesta etapa, deixar os espaços de estatísticas preparados; não inventar números para preencher os componentes.
- Estender o cadastro com mês e ano opcionais de conclusão. Se forem informados, o livro entra como lido; se ficarem vazios, continua na fila de leitura.

### Etapa 2 — Preparar os dados anuais

- Definir a resposta da API necessária para obter os totais de livros concluídos por mês em um ano.
- Aceitar mês/ano de conclusão informado no cadastro e também ao marcar um livro existente como lido.
- Registrar a precisão de mês e ano sem inventar um dia de conclusão; compatibilizar essa informação com a data completa já usada pelos livros existentes.
- Validar o ano solicitado e cobrir os casos sem livros concluídos.

### Etapa 3 — Construir os gráficos com dados reais

- Ligar o resumo anual e o gráfico mensal à API.
- Garantir que o mês e o ano informados no cadastro alimentem corretamente o gráfico e os totais anuais.
- Montar a distribuição por gênero usando os livros concluídos no período selecionado.
- Adicionar estados de carregamento, erro e ausência de dados.

### Etapa 4 — Metas e conquistas

- Definir como a meta anual será configurada e guardada.
- Calcular progresso e desbloquear conquistas a partir dos dados reais do acervo.
- Exibir mensagens motivadoras sem apresentar marcos que o leitor ainda não alcançou.

### Etapa 5 — Revisão final

- Ajustar a apresentação para telas menores e acessibilidade básica.
- Atualizar documentação e ampliar os testes dos cálculos e da API.

### Próximo passo

Começar pela **Etapa 1**, implementando a navegação e a estrutura visual das duas páginas. Depois de revisar essa etapa, decidiremos juntos se seguimos para os dados anuais ou se fazemos algum ajuste no layout.

## Tecnologias

- Java 21
- Maven
- Javalin 6
- Gson
- HTML, CSS e JavaScript sem framework
- JUnit 4

## Estrutura

```text
├── docs/                         # Diagrama UML e imagens do projeto
├── frontend/                     # Interface web
│   ├── index.html
│   ├── style.css
│   └── script.js
├── src/
│   ├── main/java/acervo/
│   │   ├── api/                   # API REST e DTOs
│   │   ├── log/                   # Registro de atividades
│   │   ├── model/                 # Livro, gênero e status
│   │   ├── service/               # Regras, estatísticas e JSON
│   │   └── Main.java              # Menu de terminal
│   └── test/java/                 # Testes automatizados
├── mockup-minha-jornada.svg       # Proposta visual da próxima evolução
├── data/                          # Criado em execução; ignorado pelo Git
├── pom.xml
└── README.md
```

## Como executar

### Pré-requisitos

- JDK 21 ou superior
- Maven

### Menu de terminal

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="acervo.Main"
```

### API e interface web

Inicie a API:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="acervo.api.ApiServer"
```

A API inicia em `http://localhost:7000`. Com ela ativa, abra `frontend/index.html` no navegador ou use uma extensão como Live Server.

## API disponível

| Método | Rota | Descrição |
<<<<<<< Updated upstream
|--------|------|-----------|
| GET    | \`/livros\` | Lista todos os livros cadastrados |
| POST   | \`/livros\` | Cadastra um novo livro |
| PUT    | \`/livros/{id}/status\` | Atualiza o status de leitura de um livro |
| DELETE | \`/livros/{id}\` | Remove um livro do acervo |
| GET    | \`/estatisticas\` | Retorna estatísticas de leitura (total lido, por gênero, por status) |
=======
|---|---|---|
| `GET` | `/livros` | Lista os livros cadastrados. |
| `POST` | `/livros` | Cadastra um livro; título, autor e gênero são obrigatórios. |
| `PUT` | `/livros/{id}/status` | Atualiza o status para um valor válido do enum. |
| `DELETE` | `/livros/{id}` | Remove um livro. |
| `GET` | `/estatisticas` | Retorna totais por status e gênero, total lido e percentual por gênero entre lidos. |
| `GET` | `/estatisticas/mes-ano?mes=8&ano=2026` | Retorna os totais concluídos no mês e no ano informados. |
>>>>>>> Stashed changes

Os livros são salvos em `data/livros.json`. O histórico de atividades não é persistido.

## Testes

Para executar os testes automatizados:

```bash
mvn test
```

<<<<<<< Updated upstream
## 🔧 Melhorias Futuras

- [ ] Validação de entrada mais robusta na API — atualmente, se o campo `genero` enviado no `POST /livros` não corresponder exatamente (case-sensitive) a uma constante do enum `Genero`, o campo fica silenciosamente `null` em vez de retornar um erro claro. No front-end isso já é mitigado com um `<select>` de opções fixas, mas a API em si ainda não valida. Próximo passo: validar a entrada e responder com `400 Bad Request` quando o gênero for inválido.
- [ ] Estatísticas por mês/ano, com metas pessoais de leitura
- [ ] Gráfico de leitura anual e percentual de livros lidos por gênero
- [ ] Testes automatizados (JUnit)
- [ ] Autenticação na API
- [ ] Deploy da API e do front-end (Render/Railway/Vercel)

## 📝 Licença

Este projeto é de uso livre para fins de estudo e portfólio.
=======
## Licença

Projeto de uso livre para fins de estudo e portfólio.
>>>>>>> Stashed changes
