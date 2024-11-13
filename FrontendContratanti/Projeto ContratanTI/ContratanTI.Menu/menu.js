/* Caso queira mexer e implementar o carrossel

let currentSlide = 0; // Inicia sempre no primeiro slide (índice 0)

function showSlide(index) {
  const slides = document.querySelectorAll(".carousel-item");
  const indicators = document.querySelectorAll(".carousel-indicators button");

  // Esconde todos os slides
  slides.forEach((slide) => {
    slide.classList.remove("active");
  });

  // Remove o estado ativo dos indicadores
  indicators.forEach((indicator) => {
    indicator.classList.remove("active");
  });

  // Exibe o slide correspondente e define o indicador ativo
  slides[index].classList.add("active");
  indicators[index].classList.add("active");

  // Atualiza o slide atual
  currentSlide = index;
}

// Funções para navegação com os controles
function nextSlide() {
  const slides = document.querySelectorAll(".carousel-item");
  currentSlide = (currentSlide + 1) % slides.length; // Vai para o próximo slide
  showSlide(currentSlide);
}

function prevSlide() {
  const slides = document.querySelectorAll(".carousel-item");
  currentSlide = (currentSlide - 1 + slides.length) % slides.length; // Volta para o slide anterior
  showSlide(currentSlide);
}

// Inicializa o carrossel no primeiro slide
showSlide(0); // Garantindo que o primeiro slide (índice 0) esteja ativo ao carregar a página
*/
