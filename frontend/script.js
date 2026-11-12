const API_URL = "http://localhost:7000";
const nomesMeses = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];
const coresGeneros = ["#5d921c", "#b78a5c", "#d8b94f", "#b9a7c9", "#7895a5", "#d48c72", "#c2a878"];
let livrosAtuais = [];
let livroParaConcluir = null;
let toastTimer;

function preencherMeses(select, incluirVazio = false) {
    if (incluirVazio) select.add(new Option("Ainda não terminei", ""));
    nomesMeses.forEach((mes, indice) => select.add(new Option(mes, String(indice + 1))));
}

async function formatarErro(resposta, fallback) {
    let dados = {};
    try { dados = await resposta.json(); } catch { /* Algumas respostas de erro não trazem JSON. */ }
    throw new Error(dados.erro || fallback);
}

function mostrarToast(mensagem, erro = false) {
    const toast = document.getElementById("toast");
    toast.textContent = mensagem;
    toast.classList.toggle("toast-error", erro);
    toast.classList.add("visible");
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => toast.classList.remove("visible"), 3600);
}

function trocarVisao(visao) {
    const jornada = visao === "jornada";
    document.getElementById("view-estante").hidden = jornada;
    document.getElementById("view-jornada").hidden = !jornada;
    document.querySelectorAll(".nav-link").forEach(botao => {
        const ativo = botao.dataset.view === visao;
        botao.classList.toggle("active", ativo);
        botao.setAttribute("aria-current", ativo ? "page" : "false");
    });
    if (jornada) renderizarJornada();
    window.scrollTo({ top: 0, behavior: "smooth" });
}

document.querySelectorAll("[data-view]").forEach(botao => botao.addEventListener("click", () => trocarVisao(botao.dataset.view)));
document.querySelector(".brand").addEventListener("click", evento => {
    evento.preventDefault();
    trocarVisao("estante");
});

async function carregarLivros() {
    const lista = document.getElementById("lista-livros");
    try {
        const resposta = await fetch(`${API_URL}/livros`);
        if (!resposta.ok) throw new Error("A API não conseguiu carregar os livros.");
        livrosAtuais = await resposta.json();
        renderizarLivros();
        renderizarJornada();
    } catch (erro) {
        console.error("Erro ao carregar livros:", erro);
        lista.replaceChildren(criarEstado("Não foi possível conectar à API. Confira se o servidor está ativo."));
        document.getElementById("contador-livros").textContent = "Indisponível";
        document.getElementById("estado-jornada").textContent = "Não foi possível carregar suas estatísticas. Verifique a conexão com a API.";
    }
}

function criarEstado(texto, classe = "empty-state") {
    const elemento = document.createElement("p");
    elemento.className = classe;
    elemento.textContent = texto;
    return elemento;
}

function renderizarLivros() {
    const container = document.getElementById("lista-livros");
    document.getElementById("contador-livros").textContent = `${livrosAtuais.length} ${livrosAtuais.length === 1 ? "livro" : "livros"}`;
    if (!livrosAtuais.length) {
        container.replaceChildren(criarEstado("Sua estante ainda está vazia. Cadastre um livro para começar."));
        return;
    }

    const cards = livrosAtuais.map(livro => {
        const lido = livro.status === "LIDO";
        const card = document.createElement("article");
        card.className = `book-card ${lido ? "book-read" : "book-pending"}`;
        const top = document.createElement("div");
        top.className = "book-card-top";
        const genero = document.createElement("span");
        genero.className = "book-genre";
        genero.textContent = String(livro.genero || "Sem gênero").replaceAll("_", " ");
        top.append(genero);
        if (lido) {
            const seal = document.createElement("span"); seal.className = "book-seal"; seal.textContent = "✓"; seal.setAttribute("aria-label", "Concluído"); top.append(seal);
        }
        const title = document.createElement("h3"); title.textContent = livro.titulo;
        const author = document.createElement("p"); author.className = "book-author"; author.textContent = livro.autor;
        const status = document.createElement("span"); status.className = "status-chip"; status.textContent = lido ? `Lido${formatarMesAno(mesAnoDoLivro(livro))}` : livro.status === "LENDO" ? "Lendo" : "Quero ler";
        const actions = document.createElement("div"); actions.className = "book-actions";
        if (!lido) {
            const finish = document.createElement("button"); finish.className = "button-secondary"; finish.type = "button"; finish.textContent = "Marcar como lido";
            finish.addEventListener("click", () => abrirDialogConclusao(livro)); actions.append(finish);
        }
        const remove = document.createElement("button"); remove.className = "button-quiet"; remove.type = "button"; remove.textContent = "Remover";
        remove.addEventListener("click", () => removerLivro(livro)); actions.append(remove);
        card.append(top, title, author, status, actions);
        return card;
    });
    container.replaceChildren(...cards);
}

function mesAnoDoLivro(livro) {
    if (livro.mesAnoConclusao) return livro.mesAnoConclusao;
    if (livro.dataConclusao) return livro.dataConclusao.slice(0, 7);
    return null;
}

function formatarMesAno(valor) {
    if (!valor) return "";
    const [ano, mes] = valor.split("-").map(Number);
    return ` · ${nomesMeses[mes - 1]} ${ano}`;
}

document.getElementById("form-cadastro").addEventListener("submit", async evento => {
    evento.preventDefault();
    const mes = document.getElementById("input-mes-conclusao").value;
    const ano = document.getElementById("input-ano-conclusao").value;
    if (Boolean(mes) !== Boolean(ano)) {
        mostrarToast("Para registrar como lido, informe mês e ano juntos.", true);
        return;
    }
    const dados = {
        titulo: document.getElementById("input-titulo").value.trim(),
        autor: document.getElementById("input-autor").value.trim(),
        genero: document.getElementById("input-genero").value
    };
    if (mes && ano) { dados.mesConclusao = Number(mes); dados.anoConclusao = Number(ano); }
    try {
        const resposta = await fetch(`${API_URL}/livros`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(dados) });
        if (!resposta.ok) return await formatarErro(resposta, "Não foi possível cadastrar o livro.");
        evento.target.reset();
        mostrarToast(mes ? "Livro cadastrado como lido. Boa leitura!" : "Livro adicionado à estante!");
        await carregarLivros();
    } catch (erro) { mostrarToast(erro.message, true); }
});

function abrirDialogConclusao(livro) {
    livroParaConcluir = livro;
    document.getElementById("dialog-mes").value = String(new Date().getMonth() + 1);
    document.getElementById("dialog-ano").value = new Date().getFullYear();
    document.getElementById("dialog-conclusao").showModal();
}

document.getElementById("cancelar-conclusao").addEventListener("click", () => document.getElementById("dialog-conclusao").close());
document.getElementById("form-conclusao").addEventListener("submit", async evento => {
    evento.preventDefault();
    if (!livroParaConcluir) return;
    const mesConclusao = Number(document.getElementById("dialog-mes").value);
    const anoConclusao = Number(document.getElementById("dialog-ano").value);
    try {
        const resposta = await fetch(`${API_URL}/livros/${livroParaConcluir.id}/status`, { method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ status: "LIDO", mesConclusao, anoConclusao }) });
        if (!resposta.ok) return await formatarErro(resposta, "Não foi possível atualizar o livro.");
        document.getElementById("dialog-conclusao").close();
        livroParaConcluir = null;
        mostrarToast("Leitura registrada. Mais uma história na sua jornada!");
        await carregarLivros();
    } catch (erro) { mostrarToast(erro.message, true); }
});

async function removerLivro(livro) {
    if (!confirm(`Remover “${livro.titulo}” da estante?`)) return;
    try {
        const resposta = await fetch(`${API_URL}/livros/${livro.id}`, { method: "DELETE" });
        if (!resposta.ok) return await formatarErro(resposta, "Não foi possível remover o livro.");
        mostrarToast("Livro removido da estante.");
        await carregarLivros();
    } catch (erro) { mostrarToast(erro.message, true); }
}

function preencherAnos() {
    const select = document.getElementById("select-ano-jornada");
    const anoAtual = new Date().getFullYear();
    for (let ano = anoAtual; ano >= 1900; ano--) select.add(new Option(String(ano), String(ano)));
    select.value = String(anoAtual);
    select.addEventListener("change", renderizarJornada);
}

function renderizarJornada() {
    const ano = Number(document.getElementById("select-ano-jornada").value || new Date().getFullYear());
    const concluidos = livrosAtuais.filter(livro => livro.status === "LIDO" && mesAnoDoLivro(livro));
    const livrosAno = concluidos.filter(livro => Number(mesAnoDoLivro(livro).slice(0, 4)) === ano);
    const totaisMes = Array(12).fill(0);
    const generos = new Map();
    livrosAno.forEach(livro => {
        const mes = Number(mesAnoDoLivro(livro).slice(5, 7));
        if (mes >= 1 && mes <= 12) totaisMes[mes - 1]++;
        const genero = livro.genero || "OUTROS";
        generos.set(genero, (generos.get(genero) || 0) + 1);
    });
    document.getElementById("total-lidos-ano").textContent = String(livrosAno.length);
    document.getElementById("label-lidos-ano").textContent = livrosAno.length === 1 ? "livro neste ano" : "livros neste ano";
    document.getElementById("resumo-lidos-ano").textContent = livrosAno.length ? `Você concluiu ${livrosAno.length} ${livrosAno.length === 1 ? "história" : "histórias"} em ${ano}.` : `Nenhum livro concluído registrado em ${ano} ainda.`;
    const maior = Math.max(...totaisMes);
    if (maior) {
        const indice = totaisMes.indexOf(maior);
        document.getElementById("mes-destaque").textContent = nomesMeses[indice];
        document.getElementById("resumo-mes-destaque").textContent = `${maior} ${maior === 1 ? "livro concluído" : "livros concluídos"}`;
    } else {
        document.getElementById("mes-destaque").textContent = "Ainda por vir";
        document.getElementById("resumo-mes-destaque").textContent = "Sua próxima história pode começar agora.";
    }
    renderizarGraficoMensal(totaisMes);
    renderizarGeneros(generos, livrosAno.length);
    renderizarConquistas(concluidos, livrosAno, generos);
    const progresso = livrosAno.length ? `Este ano você já concluiu ${livrosAno.length} ${livrosAno.length === 1 ? "livro" : "livros"}.` : "Sua próxima história está esperando por você.";
    document.getElementById("mensagem-progresso").textContent = progresso;
    document.getElementById("mensagem-jornada").textContent = livrosAno.length >= 25 ? "Vinte e cinco histórias. Que jornada bonita!" : livrosAno.length ? "Cada livro abre uma nova janela." : "Toda grande jornada começa por uma página.";
    document.getElementById("estado-jornada").textContent = "";
}

function renderizarGraficoMensal(totais) {
    const chart = document.getElementById("grafico-mensal");
    chart.replaceChildren();
    const maximo = Math.max(1, ...totais);
    totais.forEach((total, indice) => {
        const grupo = document.createElement("div"); grupo.className = "month-column";
        const barraArea = document.createElement("div"); barraArea.className = "bar-area";
        const valor = document.createElement("span"); valor.className = "bar-value"; valor.textContent = total ? String(total) : "";
        const barra = document.createElement("div"); barra.className = `month-bar${total ? " has-books" : ""}${total === maximo && total > 0 ? " month-best" : ""}`;
        barra.style.height = `${Math.max(total ? 12 : 3, total / maximo * 100)}%`;
        barra.title = `${nomesMeses[indice]}: ${total} ${total === 1 ? "livro" : "livros"}`;
        barra.setAttribute("aria-label", barra.title);
        const label = document.createElement("span"); label.className = "month-label"; label.textContent = nomesMeses[indice].slice(0, 3);
        barraArea.append(valor, barra); grupo.append(barraArea, label); chart.append(grupo);
    });
}

function renderizarGeneros(generos, total) {
    const area = document.getElementById("grafico-generos"); area.replaceChildren();
    const nota = document.getElementById("curiosidade-generos");
    if (!total) { area.append(criarEstado("Quando concluir livros neste ano, seu mapa de gêneros aparecerá aqui.", "genre-empty")); nota.textContent = ""; return; }
    const entries = [...generos.entries()].sort((a, b) => b[1] - a[1]);
    let acumulado = 0;
    const stops = entries.map(([genero, qtd], indice) => {
        const inicio = acumulado; acumulado += qtd / total * 100;
        return `${coresGeneros[indice % coresGeneros.length]} ${inicio}% ${acumulado}%`;
    });
    const donut = document.createElement("div"); donut.className = "genre-donut"; donut.style.background = `conic-gradient(${stops.join(",")})`;
    const centro = document.createElement("div"); centro.className = "donut-center";
    const num = document.createElement("strong"); num.textContent = String(total); const caption = document.createElement("span"); caption.textContent = total === 1 ? "livro" : "livros"; centro.append(num, caption); donut.append(centro);
    const legend = document.createElement("div"); legend.className = "genre-legend";
    entries.forEach(([genero, qtd], indice) => {
        const item = document.createElement("div"); item.className = "genre-legend-item";
        const dot = document.createElement("i"); dot.style.background = coresGeneros[indice % coresGeneros.length];
        const name = document.createElement("span"); name.textContent = genero.replaceAll("_", " ").toLowerCase();
        const pct = document.createElement("b"); pct.textContent = `${Math.round(qtd / total * 100)}%`;
        item.append(dot, name, pct); legend.append(item);
    });
    area.append(donut, legend);
    const favorito = entries[0][0].replaceAll("_", " ").toLowerCase();
    nota.textContent = `Seu gênero mais lido neste ano: ${favorito}.`;
}

function renderizarConquistas(todosLidos, lidosAno, generos) {
    const area = document.getElementById("conquistas"); area.replaceChildren();
    const conquistas = [
        { icone: "📚", nome: "Clube dos 25", descricao: "Concluir 25 livros no total", desbloqueada: todosLidos.length >= 25 },
        { icone: "🌱", nome: "Novo capítulo", descricao: "Explorar 3 gêneros neste ano", desbloqueada: generos.size >= 3 },
        { icone: "✨", nome: "Ritmo de leitura", descricao: "Concluir um livro em 3 meses diferentes", desbloqueada: new Set(lidosAno.map(livro => mesAnoDoLivro(livro).slice(5, 7))).size >= 3 }
    ];
    conquistas.forEach(conquista => {
        const item = document.createElement("article"); item.className = `achievement${conquista.desbloqueada ? " unlocked" : " locked"}`;
        const icon = document.createElement("span"); icon.className = "achievement-icon"; icon.textContent = conquista.icone;
        const content = document.createElement("div"); const title = document.createElement("h3"); title.textContent = conquista.nome; const detail = document.createElement("p"); detail.textContent = conquista.descricao; content.append(title, detail);
        const flag = document.createElement("span"); flag.className = "achievement-flag"; flag.textContent = conquista.desbloqueada ? "Conquistada" : "Em andamento";
        item.append(icon, content, flag); area.append(item);
    });
}

preencherMeses(document.getElementById("input-mes-conclusao"), true);
preencherMeses(document.getElementById("dialog-mes"));
preencherAnos();
carregarLivros();
