const API_URL = "http://localhost:7000";
const nomesMeses = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];
const coresGeneros = ["#5d921c", "#b78a5c", "#d8b94f", "#b9a7c9", "#7895a5", "#d48c72", "#c2a878"];
let livrosAtuais = [];
let livroParaConcluir = null;
let toastTimer;
let requisicaoResumo = 0;

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
        botao.setAttribute("aria-selected", String(ativo));
        botao.tabIndex = ativo ? 0 : -1;
    });
    if (jornada) renderizarJornada();
    window.scrollTo({ top: 0, behavior: "smooth" });
}

document.querySelectorAll("[data-view]").forEach(botao => botao.addEventListener("click", () => trocarVisao(botao.dataset.view)));
document.querySelectorAll(".nav-link").forEach((botao, indice, botoes) => {
    botao.addEventListener("keydown", evento => {
        let proximo = null;
        if (evento.key === "ArrowRight") proximo = (indice + 1) % botoes.length;
        if (evento.key === "ArrowLeft") proximo = (indice - 1 + botoes.length) % botoes.length;
        if (evento.key === "Home") proximo = 0;
        if (evento.key === "End") proximo = botoes.length - 1;
        if (proximo !== null) {
            evento.preventDefault();
            trocarVisao(botoes[proximo].dataset.view);
            botoes[proximo].focus();
        }
    });
});
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
    select.addEventListener("change", () => { renderizarJornada(); });
}

function chaveMetaAnual(ano) {
    return `meu-acervo.meta-anual.${ano}`;
}

function lerMetaAnual(ano) {
    try {
        const valor = Number(localStorage.getItem(chaveMetaAnual(ano)));
        return Number.isInteger(valor) && valor > 0 && valor <= 5000 ? valor : null;
    } catch (erro) {
        console.error("Não foi possível acessar a meta anual salva:", erro);
        return null;
    }
}

function renderizarMetaAnual(ano, totalLido) {
    const meta = lerMetaAnual(ano);
    const semMeta = document.getElementById("meta-sem-configurar");
    const configurada = document.getElementById("meta-configurada");
    const formulario = document.getElementById("form-meta");
    document.getElementById("meta-ano-label").textContent = String(ano);
    semMeta.hidden = meta !== null;
    configurada.hidden = meta === null;
    formulario.hidden = true;

    if (meta === null) return;
    const contagem = document.getElementById("meta-contagem");
    const percentualEl = document.getElementById("meta-percentual");
    const progressoEl = document.getElementById("meta-progresso");
    const mensagem = document.getElementById("meta-mensagem");
    contagem.textContent = totalLido === null ? `— / ${meta}` : `${totalLido} / ${meta}`;
    if (totalLido === null) {
        percentualEl.textContent = "—";
        progressoEl.hidden = true;
        mensagem.textContent = "Não foi possível atualizar seu progresso agora.";
        return;
    }

    const percentual = Math.round(totalLido / meta * 100);
    percentualEl.textContent = `${percentual}%`;
    progressoEl.hidden = false;
    progressoEl.value = Math.min(percentual, 100);
    progressoEl.setAttribute("aria-valuetext", `${totalLido} de ${meta} livros, ${percentual}%`);
    if (totalLido >= meta) {
        mensagem.textContent = totalLido === meta
            ? "Meta concluída! Você atingiu sua meta deste ano."
            : "Meta concluída! Você superou sua meta deste ano.";
    } else {
        const faltam = meta - totalLido;
        mensagem.textContent = `Faltam ${faltam} ${faltam === 1 ? "livro" : "livros"} para sua meta.`;
    }
}

function abrirEditorMeta() {
    const ano = Number(document.getElementById("select-ano-jornada").value);
    const metaAtual = lerMetaAnual(ano);
    document.getElementById("input-meta").value = metaAtual || "";
    document.getElementById("meta-sem-configurar").hidden = true;
    document.getElementById("meta-configurada").hidden = true;
    document.getElementById("form-meta").hidden = false;
    document.getElementById("input-meta").focus();
}

document.getElementById("abrir-editor-meta").addEventListener("click", abrirEditorMeta);
document.getElementById("editar-meta").addEventListener("click", abrirEditorMeta);
document.getElementById("cancelar-meta").addEventListener("click", () => {
    const ano = Number(document.getElementById("select-ano-jornada").value);
    const valorAtual = Number(document.getElementById("total-lidos-ano").textContent);
    renderizarMetaAnual(ano, Number.isFinite(valorAtual) ? valorAtual : null);
});
document.getElementById("form-meta").addEventListener("submit", evento => {
    evento.preventDefault();
    const ano = Number(document.getElementById("select-ano-jornada").value);
    const valor = Number(document.getElementById("input-meta").value);
    if (!Number.isInteger(valor) || valor < 1 || valor > 5000) {
        mostrarToast("Escolha uma meta entre 1 e 5.000 livros.", true);
        return;
    }
    try {
        localStorage.setItem(chaveMetaAnual(ano), String(valor));
        const textoTotal = document.getElementById("total-lidos-ano").textContent;
        const total = /^\d+$/.test(textoTotal) ? Number(textoTotal) : null;
        renderizarMetaAnual(ano, total);
        mostrarToast(`Meta de ${valor} ${valor === 1 ? "livro" : "livros"} salva para ${ano}.`);
    } catch (erro) {
        console.error("Não foi possível salvar a meta anual:", erro);
        mostrarToast("Não foi possível salvar a meta neste navegador.", true);
    }
});

async function renderizarJornada() {
    const ano = Number(document.getElementById("select-ano-jornada").value || new Date().getFullYear());
    const requestId = ++requisicaoResumo;
    const estado = document.getElementById("estado-jornada");
    estado.textContent = "Atualizando seu resumo...";

    let resumo;
    try {
        const resposta = await fetch(`${API_URL}/estatisticas/ano?ano=${ano}`);
        if (!resposta.ok) return await formatarErro(resposta, "Não foi possível carregar o resumo anual.");
        resumo = await resposta.json();
    } catch (erro) {
        if (requestId !== requisicaoResumo) return;
        renderizarMetaAnual(ano, null);
        document.getElementById("total-lidos-ano").textContent = "—";
        document.getElementById("resumo-lidos-ano").textContent = "Resumo anual indisponível.";
        document.getElementById("mes-destaque").textContent = "—";
        document.getElementById("resumo-mes-destaque").textContent = "Não foi possível carregar os dados.";
        document.getElementById("grafico-mensal").replaceChildren(criarEstado("Não foi possível carregar os dados deste ano."));
        document.getElementById("grafico-generos").replaceChildren(criarEstado("Distribuição indisponível."));
        document.getElementById("conquistas").replaceChildren(criarEstado("Não foi possível atualizar as conquistas."));
        document.getElementById("curiosidade-generos").textContent = "";
        estado.textContent = erro.message;
        return;
    }
    if (requestId !== requisicaoResumo) return;

    const totaisMes = resumo.porMes.map(item => item.total);
    const generos = new Map(Object.entries(resumo.porGenero).map(([genero, total]) => [genero, Number(total)]));
    const livrosAno = livrosAtuais.filter(livro => {
        const conclusao = mesAnoDoLivro(livro);
        return livro.status === "LIDO" && conclusao && Number(conclusao.slice(0, 4)) === ano;
    });
    const concluidos = livrosAtuais.filter(livro => livro.status === "LIDO");
    document.getElementById("total-lidos-ano").textContent = String(resumo.totalLido);
    document.getElementById("label-lidos-ano").textContent = resumo.totalLido === 1 ? "livro neste ano" : "livros neste ano";
    document.getElementById("resumo-lidos-ano").textContent = resumo.totalLido ? `Você concluiu ${resumo.totalLido} ${resumo.totalLido === 1 ? "história" : "histórias"} em ${ano}.` : `Nenhum livro concluído registrado em ${ano} ainda.`;
    renderizarMetaAnual(ano, resumo.totalLido);
    const maior = Math.max(0, ...totaisMes);
    if (maior) {
        const indice = totaisMes.indexOf(maior);
        document.getElementById("mes-destaque").textContent = nomesMeses[indice];
        document.getElementById("resumo-mes-destaque").textContent = `${maior} ${maior === 1 ? "livro concluído" : "livros concluídos"}`;
    } else {
        document.getElementById("mes-destaque").textContent = "Ainda por vir";
        document.getElementById("resumo-mes-destaque").textContent = "Sua próxima história pode começar agora.";
    }
    renderizarGraficoMensal(totaisMes);
    renderizarGeneros(generos, resumo.totalLido);
    renderizarConquistas(concluidos, livrosAno, generos);
    const progresso = resumo.totalLido ? `Este ano você já concluiu ${resumo.totalLido} ${resumo.totalLido === 1 ? "livro" : "livros"}.` : "Sua próxima história está esperando por você.";
    document.getElementById("mensagem-progresso").textContent = progresso;
    document.getElementById("mensagem-jornada").textContent = resumo.totalLido >= 25 ? "Vinte e cinco histórias. Que jornada bonita!" : resumo.totalLido ? "Cada livro abre uma nova janela." : "Toda grande jornada começa por uma página.";
    estado.textContent = "";
}

function renderizarGraficoMensal(totais) {
    const chart = document.getElementById("grafico-mensal");
    chart.replaceChildren();
    chart.setAttribute("aria-label", `Livros concluídos por mês: ${nomesMeses.map((mes, indice) => `${mes}: ${totais[indice] || 0}`).join("; ")}.`);
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
    donut.setAttribute("role", "img");
    donut.setAttribute("aria-label", `Distribuição dos ${total} livros lidos por gênero.`);
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
