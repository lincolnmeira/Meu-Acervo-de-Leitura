# Meu Acervo de Leitura

Aplicação pessoal para organizar livros e acompanhar a leitura. O projeto combina um programa de terminal, uma API REST em Java e uma interface web em HTML, CSS e JavaScript.

## Objetivo

O acervo ajuda a registrar livros que você quer ler, está lendo ou já concluiu. A versão atual oferece cadastro, organização por gênero, atualização de status e uma página dedicada à jornada de leitura, com visualizações e conquistas.

## Estado atual do projeto

### Funcionalidades disponíveis

- Cadastro de livros com título, autor e gênero.
- Identificador único, status inicial `QUERO_LER` e data de cadastro gerados automaticamente.
- Prevenção de títulos duplicados, sem diferenciar maiúsculas de minúsculas.
- Listagem, remoção e atualização do status para `LIDO`, `LENDO` ou `QUERO_LER`.
- Registro do mês/ano de conclusão sem exigir um dia, tanto no cadastro de livros já lidos quanto ao marcar um livro como lido.
- Estatísticas de total lido, contagem por gênero e status e percentual dos gêneros entre os livros lidos.
- Consulta de livros concluídos por mês e por ano.
- Resumo anual da API com total concluído, doze totais mensais e livros concluídos por gênero.
- Persistência do acervo em `data/livros.json`.
- Registro de atividades durante a sessão atual.
- Menu interativo de terminal e interface web para cadastro e gerenciamento.
- Interface web separada em **Minha estante** e **Minha jornada**, com cards de livros, formulário de cadastro e painel de jornada.
- Na jornada: resumo anual real, gráfico mensal, distribuição por gênero, conquistas calculadas a partir do acervo e mensagens de progresso.
- Meta anual configurável, com barra de progresso e armazenamento local separado por ano no navegador.
- Validação na API para cadastro, status e parâmetros da consulta mensal/anual.
- Gravação segura do arquivo JSON por arquivo temporário; em caso de falha, a alteração é revertida em memória e a API informa erro.
- Renderização dos dados dos livros como texto na interface, sem interpretar título e autor como HTML.

### Limitações conhecidas

- O histórico de atividades fica apenas em memória e recomeça quando o programa é reiniciado.
- A meta anual fica salva no armazenamento local do navegador; não é sincronizada entre navegadores ou dispositivos.
- As conquistas são calculadas no navegador a partir dos livros carregados.
- A aplicação não tem autenticação nem suporte a múltiplos usuários; foi feita para uso local e pessoal.
- Os testes cobrem parte dos cálculos de estatísticas e do resumo anual; ainda falta cobertura automatizada dos endpoints e da persistência JSON.

## Jornada de leitura: “Minha jornada”

O mockup [mockup-minha-jornada.svg](mockup-minha-jornada.svg) apresenta a direção visual aprovada: manter a paleta creme, marrom e verde e separar a experiência em **Minha estante** e **Minha jornada**. A primeira etapa já implementa essa separação e os componentes ligados aos dados disponíveis.

A página de jornada torna as estatísticas mais visuais e divertidas, incluindo:

- Resumo do ano com livros concluídos e mês mais produtivo.
- Gráfico mensal de livros concluídos, com seleção de ano.
- Distribuição dos livros lidos por gênero em um gráfico visual.
- Conquistas por marcos de leitura, como concluir 25 livros, explorar gêneros ou manter ritmo em meses diferentes.
- Um destaque motivador ligado ao progresso do leitor.
- Registro do mês e do ano em que cada livro foi concluído, inclusive quando for cadastrado depois da leitura.
- Meta anual configurável com acompanhamento de progresso.

Páginas lidas e capas não fazem parte da primeira versão planejada: hoje esses dados não são armazenados no modelo `Livro`. Podemos decidir depois se vale adicioná-los.

## Plano de implementação

O trabalho será feito em etapas pequenas. Após cada etapa, o resultado poderá ser revisado antes de avançar.

### Etapa 1 — Navegação, estante e primeira jornada (implementada)

- Criada navegação entre **Minha estante** e **Minha jornada**.
- Mantidos cadastro e gerenciamento de livros na estante.
- Criada a página responsiva da jornada, seguindo o mockup e a identidade visual atual.
- Resumo, gráfico mensal, gêneros e conquistas usam os dados existentes; quando não há dados, a página mostra estados vazios e valores zero reais.
- Cadastro aceita mês e ano opcionais de conclusão. Se forem informados, o livro entra como lido; sem eles, fica na fila.
- O mês/ano é salvo sem atribuir um dia inventado. Livros antigos com data completa continuam compatíveis.

### Etapa 2 — Consolidar os dados anuais na API (implementada)

- Criada a rota `GET /estatisticas/ano?ano=2026`, que retorna total anual, os doze meses e a distribuição por gênero.
- A rota valida o ano e retorna meses com total zero quando não há livros concluídos.
- Resumo, gráfico mensal e distribuição por gênero da jornada agora usam a resposta da API.

### Etapa 3 — Meta anual configurável (implementada)

- Permitir definir e ajustar uma meta por ano.
- Salvar a meta no navegador e restaurá-la ao abrir a jornada novamente.
- Mostrar quantidade concluída, percentual, barra de progresso, livros restantes ou mensagem de meta alcançada.

### Etapa 4 — Revisão e cobertura (implementada)

- Revisados os layouts para telas menores e a navegação por teclado entre as seções.
- Incluído atalho para o conteúdo principal e descrições acessíveis para os gráficos e atualizações do resumo.
- Adicionados testes para contagem anual por mês e gênero, incluindo meses vazios, status diferentes e anos distintos.
- Atualizada a documentação para registrar o estado final das quatro etapas.

### Próximo passo

As quatro etapas planejadas estão implementadas. O acervo está pronto para revisão e uso local; as limitações atuais são a ausência de autenticação, a sincronização da meta anual entre dispositivos e testes automatizados de API e persistência.

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
├── mockup-minha-jornada.svg       # Referência visual da jornada
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
|---|---|---|
| `GET` | `/livros` | Lista os livros cadastrados. |
| `POST` | `/livros` | Cadastra um livro; título, autor e gênero são obrigatórios. |
| `PUT` | `/livros/{id}/status` | Atualiza o status para um valor válido do enum. |
| `DELETE` | `/livros/{id}` | Remove um livro. |
| `GET` | `/estatisticas` | Retorna totais por status e gênero, total lido e percentual por gênero entre lidos. |
| `GET` | `/estatisticas/mes-ano?mes=8&ano=2026` | Retorna os totais concluídos no mês e no ano informados. |
| `GET` | `/estatisticas/ano?ano=2026` | Retorna o total concluído no ano, os totais por mês e por gênero. |

Os livros são salvos em `data/livros.json`. O histórico de atividades não é persistido.

## Testes

Para executar os testes automatizados:

```bash
mvn test
```

A suíte inclui testes de resumo anual por mês e gênero, com casos de ano diferente, status não concluído e ano sem leituras. Endpoints da API e persistência JSON ainda não têm testes automatizados.

## Licença

Projeto de uso livre para fins de estudo e portfólio.
