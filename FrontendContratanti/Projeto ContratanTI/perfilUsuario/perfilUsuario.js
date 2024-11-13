// Seleciona os itens clicados
var menuItem = document.querySelectorAll(".item-menu");

function selectLink() {
  menuItem.forEach((item) => item.classList.remove("ativo"));
  this.classList.add("ativo");
}

menuItem.forEach((item) => item.addEventListener("click", selectLink));

// Expandir o menu
var btnExp = document.querySelector("#btn-exp");
var menuSide = document.querySelector(".menu-lateral");

btnExp.addEventListener("click", function () {
  menuSide.classList.toggle("expandir");
});

// Função para editar os dados (redireciona para uma página de edição)
function editarDados() {
  const novoNome = prompt("Digite seu novo nome:");
  const novoEmail = prompt("Digite seu novo email:");
  const novaSenha = prompt("Digite sua nova senha:");

  if (novoNome && novoEmail && novaSenha) {
    document.getElementById("nome").innerText = novoNome; // Certifique-se de que o HTML tenha o ID correto
    document.getElementById("email").innerText = novoEmail;
    document.getElementById("senha").innerText = novaSenha.replace(/./g, "*");
    alert("Dados atualizados com sucesso!");
  } else {
    alert("Por favor, preencha todos os campos.");
  }
}

// Função para editar o currículo
function editarCurriculo() {
  document.getElementById("file-upload").click(); // Clica no input para abrir o seletor de arquivos
  document
    .getElementById("file-upload")
    .addEventListener("change", function () {
      const fileName = this.files[0].name;
      document.getElementById("curriculo").innerText = fileName; // Atualiza o nome do currículo exibido
      alert("Currículo atualizado com sucesso!");
    });
}
