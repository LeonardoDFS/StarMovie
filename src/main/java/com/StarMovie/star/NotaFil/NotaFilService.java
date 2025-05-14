package com.StarMovie.star.NotaFil;

import com.StarMovie.star.Filme.Filme;
import com.StarMovie.star.Filme.FilmeRepository;
import com.StarMovie.star.User.Usuario;
import com.StarMovie.star.User.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import com.StarMovie.star.Filme.Filme; // Certifique-se que está importado
import java.util.List; // Para retornar uma lista de notas

@Service
public class NotaFilService {

    @Autowired private NotaFilRepository notaFilRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private FilmeRepository filmeRepository;

    // Busca a nota que um usuário deu para um filme específico
    public Integer getNotaDoUsuarioParaFilme(Integer usuarioId, Integer filmeId) {
        if (usuarioId == null || filmeId == null) {
            return 0; // Ou lança exceção, ou retorna Optional<Integer>
        }
        Optional<NotaFil> notaOpt = notaFilRepository.findByUsuarioIdAndFilmeId(usuarioId, filmeId);
        // Se encontrou a nota, retorna o valor, senão retorna 0 (sem nota)
        return notaOpt.map(NotaFil::getNota).orElse(0);
    }

    // Salva ou atualiza a nota de um usuário para um filme
    @Transactional // Importante para garantir consistência
    public NotaFil salvarOuAtualizarNota(Integer usuarioId, Integer filmeId, int nota) {
        // Valida a nota (1 a 5)
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException("A nota deve ser entre 1 e 5.");
        }

        // Busca as entidades Usuario e Filme
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para dar nota: " + usuarioId));
        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado para dar nota: " + filmeId));

        // Tenta encontrar uma nota existente
        Optional<NotaFil> notaExistenteOpt = notaFilRepository.findByUsuarioIdAndFilmeId(usuarioId, filmeId);

        NotaFil notaFil;
        if (notaExistenteOpt.isPresent()) {
            // Se já existe, atualiza a nota
            notaFil = notaExistenteOpt.get();
            notaFil.setNota(nota);
            System.out.println("Atualizando nota existente para " + nota);
        } else {
            // Se não existe, cria uma nova nota
            notaFil = new NotaFil(usuario, filme, nota);
            System.out.println("Criando nova nota " + nota);
        }

        // Salva (cria ou atualiza) a entidade NotaFil no banco
        return notaFilRepository.save(notaFil);
    }

    /**
     * Busca todas as avaliações (objetos NotaFil) feitas por um usuário específico.
     * @param usuarioId O ID do usuário.
     * @return Uma lista de NotaFil, ou lista vazia se nenhuma for encontrada.
     */
    public List<NotaFil> getNotasDoUsuario(Integer usuarioId) {
        if (usuarioId == null) {
            return Collections.emptyList();
        }
        // Precisamos de um método no NotaFilRepository para buscar por usuarioId
        // O JOIN FETCH com f.generos é opcional aqui, mas pode ser útil se o filme
        // não carregar os gêneros por padrão e você quiser exibi-los no perfil.
        // Se filme.generos for LAZY e você acessá-los no template sem FETCH, pode dar erro.
        return notaFilRepository.findByUsuarioIdWithFilmeAndGeneros(usuarioId);
    }

    public Page<NotaFil> findAvaliacoesPaginated(Integer usuarioId, int pageNo, int pageSize, String sortField, String sortDirection) {
        if (usuarioId == null) {
            return Page.empty();
        }
        // ATENÇÃO: sortField aqui se refere a campos da entidade NotaFil ou Filme (ex: "nota", "filme.nome")
        // Se for "filme.nome", o Sort.by("filme.nome") pode funcionar.
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return notaFilRepository.findByUsuarioIdWithFilmeAndGenerosPaginated(usuarioId, pageable);
    }

    // TODO: Adicionar método para deletar nota se necessário (ex: clicar na mesma estrela?)
    // TODO: Adicionar método para buscar média de notas de um filme
}