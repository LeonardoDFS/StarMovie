const ratingStarsContainer = document.querySelector('.rating-stars');
const notaInputElement = document.getElementById('notaInput');
const ratingForm = document.getElementById('ratingForm');
// Pega a nota atual do usuário (que veio do model) para o estado inicial do mouseout
const currentRating = parseInt(notaInputElement.value || '0', 10); // Se não tiver input, usa a classe? Adapte

function highlightStars(rating) {
    if (!ratingStarsContainer) return;
    const stars = ratingStarsContainer.querySelectorAll('.fa-star');
    stars.forEach(star => {
        const starValue = parseInt(star.getAttribute('data-value'), 10);
        if (starValue <= rating) {
            star.classList.remove('far'); // Remove contorno
            star.classList.add('fas');    // Adiciona preenchido
            star.style.color = 'lightgoldenrodyellow'; // Cor de hover
        } else {
            star.classList.remove('fas');   // Remove preenchido
            star.classList.add('far');   // Adiciona contorno
            star.style.color = '#ccc';   // Cor padrão
        }
    });
}

function resetStars() {
    if (!ratingStarsContainer) return;
    const stars = ratingStarsContainer.querySelectorAll('.fa-star');
    // Volta a mostrar a nota que o usuário JÁ deu (ou 0)
    const savedRating = parseInt(ratingForm.querySelector('#notaInput').getAttribute('data-saved-rating') || '0', 10); // Pega a nota salva

    stars.forEach(star => {
        const starValue = parseInt(star.getAttribute('data-value'), 10);
        if (starValue <= savedRating) {
            star.classList.remove('far');
            star.classList.add('fas', 'active'); // Adiciona classe 'active' para cor dourada do CSS
            star.style.color = ''; // Deixa o CSS cuidar da cor 'active'
        } else {
            star.classList.remove('fas', 'active');
            star.classList.add('far');
            star.style.color = ''; // Deixa o CSS cuidar da cor padrão
        }
    });
}

function selecionarNota(rating) {
    if (!notaInputElement || !ratingForm) return;
    console.log("Nota selecionada:", rating);
    notaInputElement.value = rating; // Define o valor no input hidden

    // Adiciona um atributo para guardar a nota salva e poder resetar corretamente
    notaInputElement.setAttribute('data-saved-rating', rating);

    // Submete o formulário
    ratingForm.submit();
}

// Inicializa o estado visual das estrelas baseado na nota salva
document.addEventListener('DOMContentLoaded', () => {
    // Guarda a nota inicial no data attribute para o reset funcionar
    const initialRatingElement = ratingForm.querySelector('#notaInput');
    if (initialRatingElement) {
        const initialRatingValue = document.querySelector('.rating-stars .active') ?
            Array.from(document.querySelectorAll('.rating-stars .active')).pop().getAttribute('data-value') : '0';
        initialRatingElement.setAttribute('data-saved-rating', initialRatingValue);
        resetStars(); // Aplica o estado inicial visualmente
    }
});

// Garante que ao sair do container das estrelas, elas resetem
if(ratingStarsContainer) {
    ratingStarsContainer.addEventListener('mouseout', resetStars);
}
