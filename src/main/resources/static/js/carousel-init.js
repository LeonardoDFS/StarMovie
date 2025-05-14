document.addEventListener('DOMContentLoaded', function () {

    // Inicializa Carrossel Principal
    const mainSwiper = new Swiper('.main-carousel-container', {
        // Opções do Swiper
        loop: true, // Faz o carrossel ser infinito
        autoplay: {
            delay: 5000, // Muda slide a cada 5 segundos
            disableOnInteraction: false,
        },
        navigation: {
            nextEl: '.main-nav-next', // Seletor do botão 'next'
            prevEl: '.main-nav-prev', // Seletor do botão 'prev'
        },
        // Efeito de fade (opcional)
        effect: 'fade',
        fadeEffect: {
            crossFade: true
        },
    });

    // Configuração comum para carrosséis de filmes
    const movieCarouselOptions = {
        slidesPerView: 2, // Slides visíveis em telas pequenas
        spaceBetween: 15, // Espaço entre slides
        navigation: {
            // Botões serão específicos para cada carrossel
        },
        // Breakpoints para diferentes tamanhos de tela
        breakpoints: {
            // >= 640px
            640: {
                slidesPerView: 3,
                spaceBetween: 20,
            },
            // >= 768px
            768: {
                slidesPerView: 4,
                spaceBetween: 20,
            },
            // >= 1024px
            1024: {
                slidesPerView: 6, // Quantidade de posters visíveis
                spaceBetween: 20,
            },
            // >= 1200px
            1200: {
                slidesPerView: 7,
                spaceBetween: 20,
            },
        }
    };

    // Inicializa Carrossel Populares
    const popularSwiper = new Swiper('.popular-carousel-container', {
        ...movieCarouselOptions, // Usa as opções comuns
        navigation: {
            nextEl: '.popular-nav-next', // Botão específico
            prevEl: '.popular-nav-prev', // Botão específico
        },
    });

    // Inicializa Carrossel Favoritos (só se o container existir)
    const favoritesContainer = document.querySelector('.favorites-carousel-container');
    if (favoritesContainer) {
        const favoritesSwiper = new Swiper(favoritesContainer, {
            ...movieCarouselOptions, // Usa as opções comuns
            navigation: {
                nextEl: '.fav-nav-next', // Botão específico
                prevEl: '.fav-nav-prev', // Botão específico
            },
        });
    }

});