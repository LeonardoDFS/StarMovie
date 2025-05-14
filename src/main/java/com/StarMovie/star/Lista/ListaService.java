package com.StarMovie.star.Lista;

import com.StarMovie.star.Filme.Filme;
import com.StarMovie.star.Filme.FilmeRepository;
import com.StarMovie.star.User.Usuario;
import com.StarMovie.star.User.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // Importar Sort
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class ListaService {

    @Autowired private ListaRepository listaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private FilmeRepository filmeRepository;

    private static final String NOME_LISTA_FAVORITOS = "Favoritos";

    // Método para pegar ou criar a lista "Favoritos" de um usuário
    @Transactional
    public Lista getListaFavoritos(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para criar/buscar lista de favoritos"));
        Optional<Lista> listaOpt = listaRepository.findByUsuarioAndNomeIgnoreCase(usuario, NOME_LISTA_FAVORITOS);
        if (listaOpt.isPresent()) {
            return listaOpt.get();
        } else {
            Lista novaLista = new Lista(NOME_LISTA_FAVORITOS, usuario);
            return listaRepository.save(novaLista);
        }
    }

    // Adiciona um filme aos favoritos
    @Transactional
    public void addFilmeFavorito(Integer usuarioId, Integer filmeId) {
        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado: " + filmeId));
        Lista listaFavoritos = getListaFavoritos(usuarioId);
        listaFavoritos.addFilme(filme);
        listaRepository.save(listaFavoritos);
    }

    // Remove um filme dos favoritos
    @Transactional
    public void removeFilmeFavorito(Integer usuarioId, Integer filmeId) {
        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado: " + filmeId));
        Lista listaFavoritos = getListaFavoritos(usuarioId);
        listaFavoritos.removeFilme(filme);
        listaRepository.save(listaFavoritos);
    }

    // Verifica se um filme é favorito
    public boolean isFilmeFavorito(Integer usuarioId, Integer filmeId) {
        if (usuarioId == null) return false;
        Optional<Lista> listaOpt = listaRepository.findByUsuarioIdAndNomeIgnoreCaseWithFilmes(usuarioId, NOME_LISTA_FAVORITOS);
        if (listaOpt.isPresent()) {
            Lista lista = listaOpt.get();
            return lista.getFilmes().stream().anyMatch(filme -> filme.getId().equals(filmeId));
        }
        return false;
    }

    // Retorna o SET de filmes favoritos (para o carrossel da home, sem paginação)
    public Set<Filme> getFilmesFavoritos(Integer usuarioId) {
        if (usuarioId == null) return Collections.emptySet();
        Optional<Lista> listaOpt = listaRepository.findByUsuarioIdAndNomeIgnoreCaseWithFilmes(usuarioId, NOME_LISTA_FAVORITOS);
        return listaOpt.map(Lista::getFilmes).orElseGet(Collections::emptySet);
    }

    // **** ADICIONE ESTE NOVO MÉTODO ABAIXO ****
    /**
     * Busca os filmes favoritos de um usuário de forma paginada e ordenada.
     * @param usuarioId ID do usuário
     * @param pageNo Número da página solicitada (1-based)
     * @param pageSize Quantidade de itens por página
     * @param sortField Campo da entidade Filme para ordenação (ex: "nome", "ano")
     * @param sortDirection Direção da ordenação ("asc" ou "desc")
     * @return Um objeto Page<Filme> contendo os filmes favoritos da página e informações de paginação.
     */
    public Page<Filme> findFavoritosPaginated(Integer usuarioId, int pageNo, int pageSize, String sortField, String sortDirection) {
        if (usuarioId == null) {
            // Se não houver usuário logado, retorna uma página vazia
            return Page.empty();
        }

        // Cria o objeto Sort para ordenação
        // Atenção: 'sortField' deve ser um nome de atributo válido da entidade Filme
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        // Cria o objeto Pageable. Lembrar que PageRequest é 0-based para número de página.
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        // Chama o novo método do ListaRepository que busca os filmes paginados
        return listaRepository.findFilmesByUsuarioIdAndListaNome(usuarioId, NOME_LISTA_FAVORITOS, pageable);
    }
    // **** FIM DO NOVO MÉTODO ****
}