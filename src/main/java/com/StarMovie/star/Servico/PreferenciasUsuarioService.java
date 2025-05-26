package com.StarMovie.star.Servico;

import com.StarMovie.star.Filme.Filme;
import com.StarMovie.star.Lista.ListaService;
import com.StarMovie.star.NotaFil.NotaFil;
import com.StarMovie.star.NotaFil.NotaFilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PreferenciasUsuarioService {

    @Autowired
    private ListaService listaService; // Para buscar os filmes favoritos

    @Autowired
    private NotaFilService notaFilService; // Para buscar as avaliações do usuário

    // Constantes para definir o que consideramos "preferência"
    private static final int MIN_RATING_FOR_PREFERENCE = 4; // Nota mínima para considerar um gênero como preferido
    private static final int MAX_FAVORITE_GENRES_TO_CONSIDER = 3; // Quantos dos gêneros mais frequentes usaremos para sugestões
    private static final int WEIGHT_FAVORITE = 1; // Peso para um gênero de um filme favoritado
    private static final int WEIGHT_HIGH_RATING = 2; // Peso para um gênero de um filme bem avaliado (dando mais importância)

    /**
     * Identifica os IDs dos gêneros mais frequentes com base nos favoritos
     * e nas avaliações altas de um usuário.
     * @param usuarioId O ID do usuário.
     * @return Uma lista de IDs dos gêneros mais frequentes (até MAX_FAVORITE_GENRES_TO_CONSIDER).
     */
    public List<Integer> getIdGenerosMaisFrequentes(Integer usuarioId) {
        if (usuarioId == null) {
            return Collections.emptyList();
        }

        Map<Integer, Long> contagemGeneros = new HashMap<>();

        // 1. Processar gêneros dos filmes favoritos
        Set<Filme> filmesFavoritos = listaService.getFilmesFavoritos(usuarioId); // Método que já deve existir e retornar Set<Filme>
        filmesFavoritos.stream()
                .filter(filme -> filme != null && filme.getGeneros() != null)
                .flatMap(filme -> filme.getGeneros().stream())
                .filter(Objects::nonNull)
                .forEach(genero -> contagemGeneros.merge(genero.getId(), (long) WEIGHT_FAVORITE, Long::sum));

        // 2. Processar gêneros dos filmes bem avaliados
        List<NotaFil> notasDoUsuario = notaFilService.getNotasDoUsuario(usuarioId); // Método que já deve existir
        notasDoUsuario.stream()
                .filter(notaFil -> notaFil != null && notaFil.getNota() != null && notaFil.getNota() >= MIN_RATING_FOR_PREFERENCE)
                .map(NotaFil::getFilme) // Assume que NotaFil tem getFilme()
                .filter(filme -> filme != null && filme.getGeneros() != null)
                .flatMap(filme -> filme.getGeneros().stream())
                .filter(Objects::nonNull)
                .forEach(genero -> contagemGeneros.merge(genero.getId(), (long) WEIGHT_HIGH_RATING, Long::sum));

        if (contagemGeneros.isEmpty()) {
            System.out.println("Nenhuma preferência de gênero encontrada para o usuário ID: " + usuarioId);
            return Collections.emptyList();
        }

        // Ordena os gêneros pela contagem (frequência ponderada) e pega os top N
        List<Integer> generosPreferidos = contagemGeneros.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(MAX_FAVORITE_GENRES_TO_CONSIDER)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println("Gêneros preferidos para usuário ID " + usuarioId + ": " + generosPreferidos);
        return generosPreferidos;
    }
}