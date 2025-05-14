package com.StarMovie.star.Filme; // Confirme o pacote

import com.StarMovie.star.Filme.Filme; // Import da sua Entidade Filme
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; // IMPORTANTE: Import do JpaRepository
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//  --> VERIFIQUE SE ESTA LINHA ESTÁ EXATAMENTE ASSIM <--
public interface FilmeRepository extends JpaRepository<Filme, Integer> {
//                                   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    // Métodos que você definiu antes...
    List<Filme> findByOrderByPopularidadeDesc(Pageable pageable);

    @Query(value = "SELECT * FROM filme WHERE poster_path IS NOT NULL AND caminho_fundo IS NOT NULL ORDER BY RAND()", nativeQuery = true)
    List<Filme> findRandomFilmesForCarousel(Pageable pageable);

    // Query para buscar todos os não adultos (você já deve ter essa)
    @Query("SELECT f FROM Filme f WHERE f.isAdult = false")
    Page<Filme> findAllNonAdult(Pageable pageable);

    // NOVA QUERY para buscar por gênero E não adulto
// "g MEMBER OF f.generos" verifica se o gênero com ID :generoId está na coleção f.generos
    @Query("SELECT f FROM Filme f JOIN f.generos g WHERE f.isAdult = false AND g.id = :generoId")
    Page<Filme> findFiltradoPorGeneroEAdulto(@Param("generoId") Integer generoId,
                                             @Param("isAdult") boolean isAdult, // Parâmetro isAdult ainda necessário
                                             Pageable pageable);
    // Correção: O filtro de isAdult já está na query, não precisa do parâmetro boolean isAdult.
    // Ou podemos deixar o parâmetro isAdult para flexibilidade futura, mas garantir que é sempre false aqui.
    // Melhor simplificar:
    @Query("SELECT f FROM Filme f JOIN f.generos g WHERE f.isAdult = false AND g.id = :generoId")
    Page<Filme> findFiltradoPorGeneroEAdulto(@Param("generoId") Integer generoId, Pageable pageable);

    List<Filme> findByNomeContainingIgnoreCase(String termo, Pageable pageable);

}