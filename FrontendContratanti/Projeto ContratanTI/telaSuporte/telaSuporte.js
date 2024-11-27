document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('support-form');

    form.addEventListener('submit', async (event) => {
        event.preventDefault(); // Previne o comportamento padrão do formulário

        // Capturando os dados do formulário
        const nome = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const mensagem = document.getElementById('message').value;

        // Criando o objeto para enviar
        const suporteData = {
            nome: nome,
            email: email,
            mensagem: mensagem
        };

        try {
            // Enviando os dados para o servidor
            const response = await fetch('http://localhost:8080/usuario/suporte', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(suporteData)
            });

            if (response.ok) {
                alert('Mensagem enviada com sucesso!');
                form.reset(); // Limpa o formulário
            } else {
                alert('Erro ao enviar a mensagem. Por favor, tente novamente.');
            }
        } catch (error) {
            console.error('Erro:', error);
            alert('Ocorreu um erro inesperado. Verifique sua conexão e tente novamente.');
        }
    });
});
