// ===================== CONFIGURAÇÃO =====================

// URL base da API REST (Javalin), rodando localmente na porta 7000.
// Todas as chamadas fetch() usam essa constante como prefixo.
const API_URL = "http://localhost:7000";


// ===================== CARREGAR E EXIBIR LIVROS =====================

/**
 * Busca a lista de livros na API (GET /livros) e chama renderizarLivros()
 * para exibi-los na tela. Executada automaticamente ao carregar a página,
 * e também depois de qualquer cadastro/alteração, para manter a lista atualizada.
 */
async function carregarLivros() {
    try {
        const resposta = await fetch(`${API_URL}/livros`);
        const livros = await resposta.json();
        renderizarLivros(livros);
    } catch (erro) {
        console.error("Erro ao carregar livros:", erro);
        document.getElementById("lista-livros").innerHTML =
            "<p>Erro ao conectar com a API. Verifique se o servidor está rodando.</p>";
    }
}

/**
 * Recebe a lista de livros e monta o HTML como uma grade de cards.
 * Cada card recebe uma classe CSS diferente dependendo do status
 * (lido = destaque verde, quero_ler = neutro), reforçando visualmente
 * a sensação de "conquista" nos livros já lidos.
 */
function renderizarLivros(livros) {
    const container = document.getElementById("lista-livros");

    if (livros.length === 0) {
        container.innerHTML = "<p>Nenhum livro cadastrado ainda.</p>";
        return;
    }

    const html = livros.map(livro => {
        const classeStatus = livro.status === "Lido" ? "livro-lido" : "livro-pendente";

        const selo = livro.status === "Lido"
            ? '<span class="selo-lido">&#10003;</span>'
            : "";

        const statusTexto = livro.status === "Lido" ? "lido" : "quero ler";

        return `
            <div class="livro-card ${classeStatus}">
                ${selo}
                <div class="livro-titulo">${livro.titulo}</div>
                <div class="livro-info">${livro.autor} · ${livro.genero}</div>
                <span class="livro-badge">${statusTexto}</span>
                <div class="livro-acoes">
                    <button class="btn-marcar-lido" data-id="${livro.id}">Marcar como lido</button>
                    <button class="btn-remover" data-id="${livro.id}">Remover</button>
                </div>
            </div>
        `;
    }).join("");

    container.innerHTML = html;
    conectarBotoesDeAcao();
}


// ===================== CADASTRO DE NOVO LIVRO =====================

/**
 * Captura o envio do formulário de cadastro (#form-cadastro).
 * Monta o objeto com os dados digitados, envia via POST /livros,
 * e recarrega a lista de livros para refletir o novo cadastro.
 */
document.getElementById("form-cadastro").addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const novoLivro = {
        titulo: document.getElementById("input-titulo").value,
        autor: document.getElementById("input-autor").value,
        genero: document.getElementById("input-genero").value
    };

    try {
        const resposta = await fetch(`${API_URL}/livros`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(novoLivro)
        });

        if (!resposta.ok) {
            throw new Error("Falha ao cadastrar livro");
        }

        document.getElementById("form-cadastro").reset();
        carregarLivros();
        carregarEstatisticas();

    } catch (erro) {
        console.error("Erro ao cadastrar livro:", erro);
        alert("Não foi possível cadastrar o livro. Tente novamente.");
    }
});


// ===================== AÇÕES NOS LIVROS (MARCAR COMO LIDO / REMOVER) =====================

/**
 * Adiciona os event listeners nos botões de "marcar como lido" e "remover"
 * de cada livro da listagem. Precisa ser chamada toda vez que a lista é
 * re-renderizada, já que os botões antigos são substituídos por novos elementos.
 */
function conectarBotoesDeAcao() {

    document.querySelectorAll(".btn-marcar-lido").forEach(botao => {
        botao.addEventListener("click", async () => {
            const id = botao.dataset.id;
            await marcarComoLido(id);
        });
    });

    document.querySelectorAll(".btn-remover").forEach(botao => {
        botao.addEventListener("click", async () => {
            const id = botao.dataset.id;
            await removerLivro(id);
        });
    });
}

/**
 * Envia uma requisição PUT para /livros/{id}/status, atualizando
 * o status do livro para "Lido". Depois, recarrega a lista e as estatísticas.
 */
async function marcarComoLido(id) {
    try {
        const resposta = await fetch(`${API_URL}/livros/${id}/status`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status: "Lido" })
        });

        if (!resposta.ok) {
            throw new Error("Falha ao atualizar status");
        }

        carregarLivros();
        carregarEstatisticas();

    } catch (erro) {
        console.error("Erro ao marcar como lido:", erro);
        alert("Não foi possível atualizar o status do livro.");
    }
}

/**
 * Envia uma requisição DELETE para /livros/{id}, removendo o livro.
 * Depois, recarrega a lista e as estatísticas.
 */
async function removerLivro(id) {
    const confirmar = confirm("Tem certeza que deseja remover este livro?");
    if (!confirmar) return;

    try {
        const resposta = await fetch(`${API_URL}/livros/${id}`, {
            method: "DELETE"
        });

        if (!resposta.ok) {
            throw new Error("Falha ao remover livro");
        }

        carregarLivros();
        carregarEstatisticas();

    } catch (erro) {
        console.error("Erro ao remover livro:", erro);
        alert("Não foi possível remover o livro.");
    }
}


// ===================== ESTATÍSTICAS =====================

/**
 * Busca as estatísticas de leitura na API (GET /estatisticas) e
 * exibe o resumo no painel #painel-estatisticas.
 */
async function carregarEstatisticas() {
    try {
        const resposta = await fetch(`${API_URL}/estatisticas`);
        const dados = await resposta.json();
        renderizarEstatisticas(dados);
    } catch (erro) {
        console.error("Erro ao carregar estatísticas:", erro);
    }
}

/**
 * Recebe o objeto de estatísticas (totalLido, porGenero, porStatus)
 * e monta o HTML de exibição dentro do painel, no formato de
 * destaques numéricos (total lidos / na fila) seguido de detalhes por gênero.
 */
function renderizarEstatisticas(dados) {
    const painel = document.getElementById("painel-estatisticas");

    const naFila = dados.porStatus["Quero_ler"] || 0;

    const generosTexto = Object.entries(dados.porGenero)
        .map(([genero, qtd]) => `${genero}: ${qtd}`)
        .join(" · ") || "Nenhum";

    painel.innerHTML = `
        <h2>Minha estante</h2>
        <div class="stats-destaque">
            <div class="stat-item">
                <span class="stat-numero stat-lidos">${dados.totalLido}</span>
                <span class="stat-label">total lidos</span>
            </div>
            <div class="stat-item">
                <span class="stat-numero stat-fila">${naFila}</span>
                <span class="stat-label">na fila</span>
            </div>
        </div>
        <p class="stats-generos">${generosTexto}</p>
    `;
}


// ===================== INICIALIZAÇÃO =====================

// Executa a busca de livros e estatísticas assim que o script é carregado.
carregarLivros();
carregarEstatisticas();