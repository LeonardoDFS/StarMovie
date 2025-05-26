package com.StarMovie.star.Filme; // Ou o pacote do seu controller

import com.StarMovie.star.Lista.ListaService;
import com.StarMovie.star.NotaFil.NotaFilService;
import com.StarMovie.star.User.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FilmeController {

    @Autowired
    private FilmeService filmeService;

    @Autowired
    private ListaService listaService;

    @Autowired
    private NotaFilService notaFilService;

    @Autowired
    private GeneroService generoService; // Injetar

    @GetMapping("/filmes")
    public String viewFilmesPage(Model model,
                                 @RequestParam(defaultValue = "1") int pageNo, // Página atual (começa em 1 para UI)
                                 @RequestParam(defaultValue = "20") int pageSize, // Filmes por página
                                 @RequestParam(defaultValue = "nome") String sortField, // Campo padrão para ordenar
                                 @RequestParam(defaultValue = "asc") String sortDir,
                                 @RequestParam(required = false) Integer generoId) { // Direção padrão

        // Busca todos os gêneros para o dropdown
        List<Genero> todosOsGeneros = generoService.findAll();
        model.addAttribute("todosOsGeneros", todosOsGeneros);
        model.addAttribute("selectedGeneroId", generoId); // Para manter o dropdown selecionado

        // Busca a página de filmes do serviço
        Page<Filme> page = filmeService.findPaginated(pageNo, pageSize, sortField, sortDir, generoId);
        List<Filme> listFilmes = page.getContent(); // Pega a lista de filmes da página atual

        // Adiciona atributos ao Model para o Thymeleaf usar
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc"); // Para links de ordenação

        model.addAttribute("listFilmes", listFilmes); // A lista de filmes da página atual
        model.addAttribute("baseUrl", "/filmes"); // Manter baseUrl

        return "filmes"; // Nome do arquivo HTML (filmes.html)
    }

    // Método dos detalhes (/filmes/{id}) - COM CORREÇÕES
    @GetMapping("/filmes/{id}")
    public String viewDetalhesFilme(@PathVariable("id") Integer filmeId, Model model,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        Filme filme = filmeService.getFilmeById(filmeId);

        if (filme == null) {
            return "redirect:/home?error=filme_nao_encontrado";
        }

        Integer usuarioRating = 0;
        boolean isFavorito = false;

        // --- DECLARAR E OBTER usuarioId ---
        Integer usuarioId = null; // <<< DECLARAÇÃO que faltava
        if (userDetails != null) {
            // Adapte esta lógica se seu UserDetails for diferente
            if (userDetails instanceof Usuario) {
                usuarioId = ((Usuario) userDetails).getId();
            } else {
                System.out.println("AVISO: Detalhes - Adaptar busca de ID do usuário.");
                // Lógica alternativa para buscar ID se necessário
            }

            // Chama os métodos do service SOMENTE se temos um usuarioId
            if(usuarioId != null) {
                // Chama o método REAL do NotaFilService
                usuarioRating = notaFilService.getNotaDoUsuarioParaFilme(usuarioId, filmeId);
                isFavorito = listaService.isFilmeFavorito(usuarioId, filmeId); // <<< AGORA DEVE FUNCIONAR
            }
        }
        // ------------------------------------

        model.addAttribute("filme", filme);
        model.addAttribute("usuarioLogado", userDetails != null);
        model.addAttribute("usuarioRating", usuarioRating);
        model.addAttribute("isFavorito", isFavorito); // Passa o status real

        return "detalhes_filme";
    }

}