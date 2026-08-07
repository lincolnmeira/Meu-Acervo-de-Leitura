// URL base da API REST (Javalin), rodando localmente na porta 7000.
const API_URL = "http://localhost:7000";

// Busca a lista de livros na API assim que a página carrega,
// e chama renderizarLivros() para exibi-los na tela.
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

// Recebe a lista de livros (já convertida de JSON para objetos JS)
// e monta o HTML correspondente dentro da div #lista-livros.
function renderizarLivros(livros) {
    const container = document.getElementById("lista-livros");

    if (livros.length === 0) {
        container.innerHTML = "<p>Nenhum livro cadastrado ainda.</p>";
        return;
    }

    // Monta uma string HTML para cada livro e junta tudo em um único bloco
    const html = livros.map(livro => `
        <div class="livro">
            <strong>${livro.titulo}</strong> (${livro.autor})<br>
            Gênero: ${livro.genero} | Status: ${livro.status}
        </div>
    `).join("");

    container.innerHTML = html;
}

// Executa a busca assim que o script é carregado
carregarLivros();