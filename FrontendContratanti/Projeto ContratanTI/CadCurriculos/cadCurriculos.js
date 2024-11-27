document.getElementById("file-upload").addEventListener("change", function (e) {
  const fileInput = e.target;
  const filePreview = document.getElementById("file-preview");

  if (fileInput.files.length > 0) {
    const file = fileInput.files[0];
    filePreview.textContent = `Arquivo selecionado: ${file.name}`;
  } else {
    filePreview.textContent = "";
  }
});

document
  .getElementById("curriculo-form")
  .addEventListener("submit", function (e) {
    e.preventDefault();

    const nome = document.getElementById("nome").value;
    const github = document.getElementById("github").value;
    const cargo = document.getElementById("cargo").value;
    const linkedin = document.getElementById("linkedin").value;
    const fileInput = document.getElementById("file-upload");
    const descricaoInput = document.querySelector('textarea[name="descricao"]');

    if (!fileInput.files.length) {
      alert("Por favor, envie seu currículo.");
      return;
    }

    // Aqui você pode enviar os dados para um backend usando AJAX ou Fetch API
    alert("Cadastro realizado com sucesso!");
  });
