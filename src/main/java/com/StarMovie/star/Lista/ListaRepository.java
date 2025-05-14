package com.StarMovie.star.Lista; // Mesmo pacote da entidade Lista

import com.StarMovie.star.Filme.Filme;
import com.StarMovie.star.User.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Para nomear parâmetros na query

import java.util.Optional;
import java.util.List; // Para buscar todas as listas se precisar

public interface ListaRepository extends JpaRepository<Lista, Integer> {

    // Encontra uma lista específica pelo nome E pelo objeto Usuario
    Optional<Lista> findByUsuarioAndNomeIgnoreCase(Usuario usuario, String nome);

    // Ou encontra pelo nome E pelo ID do usuário
    Optional<Lista> findByUsuarioIdAndNomeIgnoreCase(Integer usuarioId, String nome);

    // Encontra todas as listas de um usuário (útil para página de perfil)
    List<Lista> findByUsuarioId(Integer usuarioId);

    // Encontra a lista de favoritos E já carrega os filmes dela (JOIN FETCH)
    @Query("SELECT l FROM Lista l LEFT JOIN FETCH l.filmes WHERE l.usuario.id = :usuarioId AND lower(l.nome) = lower(:nome)")
    Optional<Lista> findByUsuarioIdAndNomeIgnoreCaseWithFilmes(Integer usuarioId, String nome);

    // NOVO MÉT0DO para buscar filmes de uma lista específica de um usuário com paginação
    // Esta query busca os Filmes que estão associados a uma Lista específica
    // A Lista, por sua vez, pertence a um Usuario e tem um Nome específico (ex: "Favoritos")
    @Query("SELECT f FROM Lista l JOIN l.filmes f WHERE l.usuario.id = :usuarioId AND lower(l.nome) = lower(:nomeLista)")
    Page<Filme> findFilmesByUsuarioIdAndListaNome(@Param("usuarioId") Integer usuarioId,
                                                  @Param("nomeLista") String nomeLista,
                                                  Pageable pageable);
}