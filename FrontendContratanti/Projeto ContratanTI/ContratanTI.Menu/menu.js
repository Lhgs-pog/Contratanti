document.addEventListener('DOMContentLoaded', () => {
    // URL do endpoint de usuários
    const apiUrl = 'http://localhost:8080/usuario';  // Ajuste o URL conforme necessário

    // Função para buscar os dados da API e exibir os currículos
    const fetchCurriculos = async () => {
        try {
            const response = await fetch(apiUrl);
            if (!response.ok) {
                throw new Error(`Erro ao buscar currículos: ${response.status}`);
            }

            const usuarios = await response.json(); // Resposta do backend, assumindo um array de usuários

            // Se não houver usuários, exibe mensagem
            if (!usuarios || usuarios.length === 0) {
                const container = document.querySelector('.grade-curriculos');
                container.innerHTML = '<p>Nenhum currículo encontrado.</p>';
                return;
            }

            // Preenche os currículos na página
            usuarios.forEach(usuario => {
                const { id, nome, email, telefone, descricao, cidade } = usuario;

                // Cria o HTML para exibir cada currículo
                const curriculoLink = document.createElement('a');
                curriculoLink.classList.add('curriculo');
                curriculoLink.href = `../perfilUsuario/perfilUsuario.html?id=${id}`; // Passando o ID do usuário na URL

                curriculoLink.innerHTML = `
                    <h4>${nome}</h4>
                    <p>Email: ${email}</p>
                    <p>Telefone: ${telefone}</p>
                    <p>Descrição: ${descricao}</p>
                    <p>Cidade: ${cidade}</p>
                `;

                // Adiciona o currículo à grade
                const curriculoPage = document.getElementById('curriculoPage1');
                curriculoPage.appendChild(curriculoLink);
            });
        } catch (error) {
            console.error('Erro ao carregar currículos:', error);
            const container = document.querySelector('.grade-curriculos');
            container.innerHTML = '<p>Ocorreu um erro ao carregar os currículos.</p>';
        }
    };

    // Chama a função para carregar os currículos ao carregar a página
    fetchCurriculos();
});
