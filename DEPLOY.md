# Guia de deploy — Meu Acervo de Leitura

Este guia registra uma possibilidade futura. O projeto está concluído para uso local e pessoal; deploy não faz parte do escopo atual.

Este projeto tem duas partes que serão publicadas separadamente:

- **API Java/Javalin:** Render, como Web Service Docker.
- **Interface HTML/CSS/JavaScript:** Vercel, como site estático com raiz em `frontend/`.

## 1. Decida como os dados serão separados

Faça esta escolha antes de publicar o acervo com livros pessoais. Apagar o arquivo inicial não faz com que cada pessoa passe a ter sua própria estante.


### Opção B — Uma estante privada para cada pessoa (alternativa futura, ainda não implementada)

Antes de abrir o site para usuários, evolua o sistema para múltiplas contas:

1. Adicione cadastro, login e logout, usando um provedor de autenticação confiável ou autenticação implementada no backend.
2. Troque o arquivo JSON compartilhado por um banco PostgreSQL gerenciado.
3. Relacione cada livro a um usuário, por exemplo com `books.user_id` referenciando `users.id`.
4. Em **todas** as operações da API, identifique o usuário autenticado e limite as consultas e alterações aos livros dele.
5. Guarde também a meta anual associada ao usuário e ao ano. Hoje ela fica no `localStorage` do navegador, portanto não acompanha a conta entre dispositivos.
6. Restrinja CORS ao domínio de produção do Vercel e mantenha a autorização no backend. Restringir CORS, sozinho, não protege a API.
7. Migre os livros atuais para uma conta sua, se quiser mantê-los. Faça uma cópia de segurança antes de qualquer migração.

Esse caminho exige mudanças no código e no modelo de dados antes do deploy público. Não publique livros pessoais com a API atual sem autenticação.

## 2. Prepare o backend para o Render

### 2.1 Fazer a API usar a porta do Render

O Render informa a porta pela variável de ambiente `PORT`. O código atual inicia na porta fixa `7000`, então adapte `ApiServer` antes de publicar.

No método de inicialização, aceite o host e a porta:

```java
public void iniciar(int porta) {
    app.start("0.0.0.0", porta);
}
```

No `main`, use a variável `PORT` e mantenha `7000` como padrão local:

```java
public static void main(String[] args) {
    int porta = Integer.parseInt(System.getenv().getOrDefault("PORT", "7000"));
    ApiServer servidor = new ApiServer();
    servidor.iniciar(porta);
}
```

O Render precisa que o serviço escute em `0.0.0.0` e na porta indicada por `PORT`.

### 2.2 Adicionar um `Dockerfile` na raiz

O Render usa Docker para executar aplicações Java. Crie `Dockerfile` ao lado de `pom.xml`:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package dependency:copy-dependencies -DincludeScope=runtime

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/classes ./target/classes
COPY --from=build /app/target/dependency ./target/dependency
EXPOSE 10000
CMD ["sh", "-c", "java -cp 'target/classes:target/dependency/*' acervo.api.ApiServer"]
```

Antes de enviar, compile e rode os testes localmente:

```bash
mvn clean test
```

Se tiver Docker instalado, também pode conferir a imagem localmente:

```bash
docker build -t meu-acervo-api .
docker run --rm -p 7000:10000 -e PORT=10000 meu-acervo-api
```

Com o container em execução, a API local deve responder em `http://localhost:7000/livros`.

### 2.3 Escolher onde os livros serão persistidos

O projeto atual salva os livros em `data/livros.json`, relativo ao diretório de trabalho `/app` do Dockerfile acima. Portanto, um disco persistente no Render deve ser montado em `/app/data`.

- Para uma demonstração temporária com o JSON, configure um **Persistent Disk** no Render com mount path `/app/data`. Discos persistentes só estão disponíveis em serviços pagos; um serviço gratuito usa filesystem efêmero, que pode perder arquivos em reinícios, suspensão ou novo deploy.
- Para múltiplas contas, use PostgreSQL e não o JSON compartilhado. Configure a URL e credenciais do banco como variáveis privadas **somente no Render**; nunca as coloque no JavaScript do frontend nem no Git.

Não considere o sistema pronto para guardar livros pessoais até confirmar a persistência escolhida.

## 3. Publique a API no Render

1. Envie o `Dockerfile` e as adaptações de código para o branch que será usado em produção, normalmente `main`.
2. No Dashboard do Render, selecione **New → Web Service** e conecte o repositório GitHub.
3. Escolha o repositório e o branch de produção.
4. Selecione o runtime **Docker** e informe `./Dockerfile` como caminho do Dockerfile.
5. Escolha a instância adequada. Se mantiver temporariamente o JSON e precisar preservar dados, escolha um serviço pago e configure o disco persistente descrito acima.
6. Para a opção PostgreSQL, crie ou conecte o banco e adicione as variáveis necessárias na seção **Environment** do serviço. Não coloque segredos em arquivos versionados.
7. Inicie o deploy e acompanhe os logs até o serviço indicar que está disponível.
8. Copie a URL pública do serviço, no formato `https://nome-do-servico.onrender.com`.

> **Observação sobre o plano gratuito:** serviços gratuitos do Render podem dormir após um período sem tráfego e levar um tempo para responder à primeira solicitação. O filesystem é efêmero e não aceita persistent disk. Um PostgreSQL gratuito do Render tem prazo limitado; confira as condições atuais antes de usá-lo como armazenamento durável.

## 4. Configure o frontend para chamar a API publicada

O frontend atual usa `http://localhost:7000`. Antes de publicar, altere a constante `API_URL` em `frontend/script.js` para usar a API local no desenvolvimento e a URL do Render no site publicado:

```javascript
const hostLocal = ["localhost", "127.0.0.1", ""].includes(window.location.hostname);
const API_URL = hostLocal
    ? "http://localhost:7000"
    : "https://NOME-DO-SERVICO.onrender.com";
```

Substitua `NOME-DO-SERVICO` pela URL real copiada do Render, sem barra final. Essa URL é pública e não é um segredo. Como o frontend é estático e não tem etapa de build, uma variável configurada no painel do Vercel não substitui automaticamente a constante JavaScript.

Depois de conhecer o domínio do Vercel, restrinja a configuração CORS do Javalin à origem de produção, por exemplo:

```java
cors.addRule(it -> it.allowHost("https://SEU-PROJETO.vercel.app"));
```

Troque o domínio pelo endereço final do frontend. Se usar domínio personalizado, inclua também essa origem. Isso evita chamadas de navegador vindas de outras origens, mas **não substitui autenticação e autorização**.

## 5. Publique o frontend na Vercel

1. Envie a alteração de `API_URL` para o GitHub.
2. Na Vercel, selecione **Add New → Project** e importe o mesmo repositório.
3. Em **Root Directory**, escolha `frontend`.
4. Selecione o framework **Other**. Este frontend não precisa de compilação.
5. Deixe o Build Command vazio/sem build e use `.` como Output Directory, se o painel pedir um valor.
6. Defina `main` como Production Branch e faça o deploy.
7. Copie a URL gerada pela Vercel, como `https://seu-projeto.vercel.app`.
8. Se mudar CORS para aceitar apenas o domínio Vercel, atualize o domínio no backend e aguarde o Render fazer novo deploy.

## 6. Confira a implantação

1. Abra `https://NOME-DO-SERVICO.onrender.com/livros`. A resposta deve ser JSON.
2. Abra `https://NOME-DO-SERVICO.onrender.com/estatisticas/ano?ano=2026`. A resposta deve incluir o total anual, doze meses e os gêneros.
3. Abra o site da Vercel e confira se **Minha estante** e **Minha jornada** carregam.
4. Na ferramenta de desenvolvedor do navegador, confira a aba **Network**: as chamadas devem ir para `https://...onrender.com`, não para `localhost`.
5. Teste cadastro, conclusão e remoção com livros fictícios. Reinicie/reimplante a API e confirme se os dados continuam salvos no armazenamento escolhido.
6. Teste a navegação com teclado e em largura de celular.

## 7. Atualizações futuras

Com o GitHub conectado, novos commits no branch de produção podem disparar novos deploys no Render e na Vercel. Confira a página **Deployments** de cada serviço para ver o estado da publicação e os logs.

## Referências oficiais

- [Deploy Docker no Render](https://render.com/docs/docker)
- [Web Services e porta `PORT` no Render](https://render.com/docs/web-services)
- [Discos persistentes no Render](https://render.com/docs/disks)
- [Limitações de serviços gratuitos no Render](https://render.com/docs/free)
- [Configurar build e site estático na Vercel](https://vercel.com/docs/builds/configure-a-build)
- [Deploy a partir de repositórios Git na Vercel](https://vercel.com/docs/git)
- [Inicialização e configuração CORS do Javalin](https://javalin.io/documentation), [plugin CORS](https://javalin.io/plugins/cors)
