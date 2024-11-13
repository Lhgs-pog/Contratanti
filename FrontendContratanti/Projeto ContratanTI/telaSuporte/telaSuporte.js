document
  .getElementById("support-form")
  .addEventListener("submit", function (event) {
    event.preventDefault();

    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const message = document.getElementById("message").value;

    if (name && email && message) {
      alert(
        "Sua mensagem foi enviada com sucesso! Entraremos em contato em breve."
      );
      // Aqui você pode adicionar o envio do formulário, por exemplo, via AJAX ou algum backend.
      document.getElementById("support-form").reset();
    } else {
      alert("Por favor, preencha todos os campos.");
    }
  });
