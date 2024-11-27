
document.addEventListener('DOMContentLoaded', () => {
    // Função para extrair o parâmetro 'uid' da URL
    const getQueryParam = (param) => {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    };

    // Captura o 'uid' da URL
    const userId = getQueryParam('uid');
    console.log("UID Capturado: ", userId);  // Verifique se o uid está correto
    // Verifica se o 'uid' foi passado
    if (!userId) {
        alert('Nenhum ID de usuário encontrado.');
        return;  // Encerra a função caso o ID não exista
    }

    const apiUrl = `http://localhost:8080/usuario/${userId}`;  // URL do endpoint para buscar os dados

    // Função para carregar os dados do usuário
    const carregarDadosUsuario = async () => {
        try {
            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error(`Erro ao buscar dados do usuário: ${response.status}`);
            }

            const userData = await response.json();  // Dados do usuário retornados pela API

            // Preencher os campos com os dados do usuário
            document.getElementById('nome').value = userData.nome || '';
            document.getElementById('email').value = userData.email || '';
            document.getElementById('telefone').value = userData.telefone || '';
            document.getElementById('cpf').value = userData.cpf || '';
        } catch (error) {
            console.error('Erro ao carregar os dados do usuário:', error);
        }
    };

    // Chama a função para carregar os dados do usuário assim que o DOM estiver carregado
    carregarDadosUsuario();

    // Evento de envio do formulário
    document.getElementById("dados-form").addEventListener("submit", function(event) {
        event.preventDefault(); // Impede o envio padrão do formulário

        // Obtém os dados dos campos
        const nome = document.getElementById("nome").value;
        const email = document.getElementById("email").value;
        const senha = document.getElementById("senha").value;
        const telefone = document.getElementById("telefone").value;
        const cpf = document.getElementById("cpf").value;

        // Cria o objeto com os dados
        const usuarioData = {
            nome: nome,
            email: email,
            senha: senha,
            telefone: telefone,
            cpf: cpf
        };

        // Substitua {uid} com o ID do usuário
        const uid = userId;  // Usando o 'uid' da URL
        const url = `http://localhost:8080/usuarios/${uid}`;

        // Envia a requisição PUT para o servidor
        fetch(url, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(usuarioData)
        })
        .then(response => {
            if (response.ok) {
                alert("Dados atualizados com sucesso!");
            } else {
                alert("Erro ao atualizar dados!");
            }
        })
        .catch(error => {
            console.error("Erro na requisição:", error);
            alert("Erro ao enviar os dados!");
        });
    });
});

