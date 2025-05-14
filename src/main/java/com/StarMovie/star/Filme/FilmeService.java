package com.StarMovie.star.Filme;

import com.StarMovie.star.Filme.Filme;
import com.StarMovie.star.Filme.FilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service

public class FilmeService {
    @Autowired
    private FilmeRepository filmeRepository;

    // Retorna N filmes mais populares
    public List<Filme> getFilmesPopulares(int quantidade) {
        Pageable limit = PageRequest.of(0, quantidade); // Página 0, 'quantidade' de itens
        try {
            return filmeRepository.findByOrderByPopularidadeDesc(limit);
        } catch (Exception e) {
            // Logar erro, etc.
            return Collections.emptyList();
        }
    }

    // Retorna N filmes aleatórios para o carrossel principal
    public List<Filme> getFilmesCarrosselPrincipal(int quantidade) {
        Pageable limit = PageRequest.of(0, quantidade);
        try {
            return filmeRepository.findRandomFilmesForCarousel(limit);
        } catch (Exception e) {
            // Logar erro, etc.
            return Collections.emptyList();
        }
    }

    // Retorna filmes favoritos do usuário (IMPLEMENTAÇÃO FUTURA)
    public List<Filme> getFilmesFavoritos(Integer usuarioId, int quantidade) {
        // **PLACEHOLDER:** Por enquanto, retorna populares como substituto
        // TODO: Implementar lógica real buscando das tabelas lista/listafil
        if (true) { // Remover este 'if' quando implementar favoritos
            System.out.println("AVISO: Usando filmes populares como placeholder para favoritos.");
            return getFilmesPopulares(quantidade);
        }
        // Lógica real virá aqui...
        // Pageable limit = PageRequest.of(0, quantidade);
        // return filmeRepository.findFavoritosByUsuarioId(usuarioId, limit); // Exemplo
        return Collections.emptyList();
    }

    // Busca filmes por nome
    public List<Filme> buscarFilmesPorNome(String termo, int quantidade) {
        Pageable limit = PageRequest.of(0, quantidade);
        try {
            return filmeRepository.findByNomeContainingIgnoreCase(termo, limit);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Page<Filme> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, Integer generoId) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        if (generoId != null) {
            // Chama um novo método no repositório que filtra por gênero
            return filmeRepository.findFiltradoPorGeneroEAdulto(generoId, false, pageable);
        } else {
            // Chama o método que só filtra por adulto (o antigo findAllNonAdult)
            return filmeRepository.findAllNonAdult(pageable);
        }
    }

    public Filme getFilmeById(Integer id) {
        // DECLARAÇÃO DA VARIÁVEL:
        Optional<Filme> optionalFilme = filmeRepository.findById(id);

        // USO DA VARIÁVEL:
        if (optionalFilme.isPresent()) {
            return optionalFilme.get();
        } else {
            return null;
        }
    }

    // --- PLACEHOLDERS para interações futuras
    public Integer getUsuarioRating(Integer filmeId, Integer usuarioId) {
        // TODO: Implementar busca da nota do usuário (tabela notafil)
        System.out.println("Aviso: Buscando nota do usuário (placeholder)");
        //Retorna 0 ou null por enquanto
        return 0;
    }

    public boolean isFilmeFavorito(Integer filmeId, Integer usuarioId) {
        // TODO: Implementar verificação se filme está na lista de usuário
        System.out.println("AVISO: VERIFICANDO FAVORITO (placeholder)");

        return false; //Retorna false por enquanto
    }

}
