document.addEventListener('DOMContentLoaded', () => {
    const curriculoList = document.getElementById('curriculo-list'); // Div onde os currículos serão adicionados
    const apiUrl = `http://localhost:8080/usuario`; // URL base para os currículos

    // Função para buscar os dados da API
    const fetchCurriculos = async () => {
        try {
            // Requisição GET para o endpoint
            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error(`Erro ao buscar currículos: ${response.status}`);
            }

            const usuarios = await response.json(); // Supondo que a resposta seja um array de objetos de usuários

            // Verifica se a resposta contém currículos
            if (!usuarios || usuarios.length === 0) {
                curriculoList.innerHTML = '<p>Nenhum currículo encontrado.</p>';
                return;
            }

            // Adiciona os currículos na página
            usuarios.forEach(usuario => {
                const { nome, email, telefone, descricao, cidade } = usuario;

                // Verifica se os dados do usuário existem
                if (!nome || !email || !telefone || !descricao || !cidade) {
                    console.warn('Dados do currículo incompletos', usuario);
                    return; // Ignora currículos incompletos
                }

                // Cria o elemento para cada currículo
                const curriculoItem = document.createElement('div');
                curriculoItem.classList.add('curriculo');

                // Adiciona os dados do usuário no HTML
                curriculoItem.innerHTML = `
                    <h4>${nome}</h4>
                    <p>Email: ${email}</p>
                    <p>Telefone: ${telefone}</p>
                    <p>Descrição: ${descricao}</p>
                    <p>Cidade: ${cidade}</p>
                `;

                // Adiciona o currículo à lista
                curriculoList.appendChild(curriculoItem);
            });
        } catch (error) {
            console.error('Erro ao carregar currículos:', error);
            curriculoList.innerHTML = '<p>Ocorreu um erro ao carregar os currículos.</p>';
        }
    };

    // Carrega os currículos assim que o DOM for completamente carregado
    fetchCurriculos();
});
s