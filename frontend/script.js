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
 * Recebe a lista de livros (já convertida de JSON para objetos JS) e monta
 * o HTML correspondente dentro da div #lista-livros. Separada de carregarLivros()
 * para manter "buscar dados" e "exibir dados" como responsabilidades distintas.
 */
function renderizarLivros(livros) {
    const container = document.getElementById("lista-livros");

    // Caso não haja nenhum livro cadastrado ainda
    if (livros.length === 0) {
        container.innerHTML = "<p>Nenhum livro cadastrado ainda.</p>";
        return;
    }

    // Para cada livro, monta um bloco HTML com suas informações.
    // .map() transforma cada objeto Livro em uma string HTML,
    // e .join("") junta todas as strings em um único bloco final.
    const html = livros.map(livro => `
        <div class="livro">
            <strong>${livro.titulo}</strong> (${livro.autor})<br>
            Gênero: ${livro.genero} | Status: ${livro.status}
        </div>
    `).join("");

    container.innerHTML = html;
}


// ===================== CADASTRO DE NOVO LIVRO =====================

/**
 * Captura o envio do formulário de cadastro (#form-cadastro).
 * Monta o objeto com os dados digitados, envia via POST /livros,
 * e recarrega a lista de livros para refletir o novo cadastro.
 */
document.getElementById("form-cadastro").addEventListener("submit", async (evento) => {
    evento.preventDefault(); // impede o recarregamento padrão da página ao enviar o form

    // Monta o objeto a ser enviado no corpo da requisição,
    // lendo os valores digitados/selecionados no formulário
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

        // Limpa os campos do formulário após o cadastro bem-sucedido
        document.getElementById("form-cadastro").reset();

        // Recarrega a lista para exibir o livro recém-cadastrado
        carregarLivros();

    } catch (erro) {
        console.error("Erro ao cadastrar livro:", erro);
        alert("Não foi possível cadastrar o livro. Tente novamente.");
    }
});


// ===================== INICIALIZAÇÃO =====================

// Executa a busca de livros assim que o script é carregado,
// preenchendo a listagem com os dados já existentes na API.
carregarLivros();