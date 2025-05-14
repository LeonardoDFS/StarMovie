package com.StarMovie.star.NotaFil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query; // Importar Query
import org.springframework.data.repository.query.Param; // Importar Param
import java.util.List; // Importar List

// Usa a Entidade NotaFil e a Classe da Chave Composta NotaFilId
public interface NotaFilRepository extends JpaRepository<NotaFil, NotaFilId> {

    // Método para encontrar uma nota específica pelo ID do usuário e do filme
    Optional<NotaFil> findByUsuarioIdAndFilmeId(Integer usuarioId, Integer filmeId);

    // --- NOVO MÉTODO ---
    // Busca todas as NotaFil de um usuário e já carrega (FETCH) o Filme associado
    // e os Gêneros desse Filme para evitar LazyInitializationException no template.
    @Query("SELECT nf FROM NotaFil nf JOIN FETCH nf.filme f LEFT JOIN FETCH f.generos WHERE nf.usuarioId = :usuarioId ORDER BY nf.filme.nome ASC")
    List<NotaFil> findByUsuarioIdWithFilmeAndGeneros(@Param("usuarioId") Integer usuarioId);

    // Alternativa mais simples se você não precisar dos gêneros imediatamente ou se o filme já os carrega EAGER:
    // List<NotaFil> findByUsuarioIdOrderByFilmeNomeAsc(Integer usuarioId);
    // (O Spring Data JPA pode gerar essa query automaticamente pelo nome do método)

    @Query("SELECT nf FROM NotaFil nf JOIN FETCH nf.filme f LEFT JOIN FETCH f.generos WHERE nf.usuarioId = :usuarioId")
    Page<NotaFil> findByUsuarioIdWithFilmeAndGenerosPaginated(@Param("usuarioId") Integer usuarioId, Pageable pageable);

}