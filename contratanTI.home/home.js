// Array de objetos representando currículos cadastrados
const curriculos = [
    { nome: "João Silva", profissao: "Desenvolvedor Web" },
    { nome: "Maria Souza", profissao: "Designer Gráfico" },
    { nome: "Pedro Lima", profissao: "Engenheiro de Software" },
    // Adicione mais currículos conforme necessário
];


// Função para adicionar currículos ao menu de navegação e ao container principal
function carregarCurriculos() {
    const menu = document.getElementById("menuCurriculos");
    const container = document.getElementById("curriculoContainer");


    curriculos.forEach(curriculo => {
        // Criar item do menu
        const li = document.createElement("li");
        li.textContent = curriculo.nome;
        menu.appendChild(li);


        // Criar container de currículo
        const div = document.createElement("div");
        div.classList.add("curriculo");


        const h3 = document.createElement("h3");
        h3.textContent = curriculo.nome;
        div.appendChild(h3);


        const p = document.createElement("p");
        p.textContent = curriculo.profissao;
        div.appendChild(p);


        container.appendChild(div);
    });
    
}

// Chamar a função ao carregar a página
window.onload = carregarCurriculos;

/* // Função para verificar se o elemento está na viewport / não está funcionando 
function isInViewport(element) {
    const rect = element.getBoundingClientRect();
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
}

// Seleciona o elemento com a classe .curriculo
const animatedText = document.querySelector('.curriculo');

// Evento de rolagem
window.addEventListener('scroll', function() {
    if (isInViewport(animatedText)) {
        animatedText.classList.add('active');
    }
});
*/