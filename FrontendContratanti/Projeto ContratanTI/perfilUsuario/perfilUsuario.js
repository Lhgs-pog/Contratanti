document.addEventListener('DOMContentLoaded', () => {
    // Função para extrair parâmetros da URL
    const getQueryParam = (param) => {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    };

    // Função de redirecionamento para editar dados
    function editarDados() {
        window.location.assign("../EditarPerfilUsuario/EditarUsuario.html");
    }

    // Adiciona o evento de clique ao botão
    const btnEditar = document.querySelector('.btn-editar');
    btnEditar.addEventListener('click', editarDados);

    // Captura o UID da URL
    const userId = getQueryParam('uid');
    
    // Verifica se há um ID de usuário
    if (!userId) {
        alert('Nenhum usuário selecionado.');
        return; // Não faz nada, mas exibe o alerta
    }

    const apiUrl = `http://localhost:8080/usuario/${userId}`;

    // Função para buscar os dados do usuário e atualizar as labels
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

            const userData = await response.json();

            // Atualiza os elementos da página com os dados recebidos
            document.getElementById('nome').textContent = userData.nome || 'N/A';
            document.getElementById('email').textContent = userData.email || 'N/A';
            document.getElementById('senha').textContent = userData.senha ? '********' : 'N/A';
            document.getElementById('curriculo').textContent = userData.curriculo || 'N/A';
            document.getElementById('telefone').textContent = userData.telefone || 'N/A';
            document.getElementById('cpf').textContent = userData.cpf || 'N/A';
        } catch (error) {
            console.error('Erro ao carregar os dados do usuário:', error);
        }
    };

    // Chama a função para carregar os dados assim que o DOM estiver carregado
    carregarDadosUsuario();
});
