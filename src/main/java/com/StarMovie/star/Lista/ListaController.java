package com.StarMovie.star.Lista;

// Import CORRETO para o Model do Spring MVC
import org.springframework.ui.Model;
import com.StarMovie.star.Filme.Filme;
import com.StarMovie.star.User.Usuario;
// Importe o UsuarioRepository se o método getUsuarioIdLogado precisar dele
// import com.StarMovie.star.User.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Certifique-se que Page está importado
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // Import para @RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List; // Necessário para List<Filme> listFilmesFavoritos
import java.util.Optional; // Se for usar no getUsuarioIdLogado

@Controller
public class ListaController {

    @Autowired
    private ListaService listaService;

    // Descomente e injete se o seu getUsuarioIdLogado precisar buscar no repositório
    // @Autowired
    // private UsuarioRepository usuarioRepository;

    // Endpoint para ADICIONAR favorito (como antes)
    @PostMapping("/filmes/{filmeId}/favoritar")
    public String adicionarFavorito(@PathVariable("filmeId") Integer filmeId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            Integer usuarioId = getUsuarioIdLogado(userDetails);
            if(usuarioId == null) throw new RuntimeException("Não foi possível obter ID do usuário logado.");
            listaService.addFilmeFavorito(usuarioId, filmeId);
            redirectAttributes.addFlashAttribute("successMessage", "Filme adicionado aos favoritos!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar aos favoritos: " + e.getMessage());
        }
        return "redirect:/filmes/" + filmeId;
    }

    // Endpoint para REMOVER favorito (como antes)
    @PostMapping("/filmes/{filmeId}/desfavoritar")
    public String removerFavorito(@PathVariable("filmeId") Integer filmeId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            Integer usuarioId = getUsuarioIdLogado(userDetails);
            if(usuarioId == null) throw new RuntimeException("Não foi possível obter ID do usuário logado.");
            listaService.removeFilmeFavorito(usuarioId, filmeId);
            redirectAttributes.addFlashAttribute("successMessage", "Filme removido dos favoritos!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao remover dos favoritos: " + e.getMessage());
        }
        return "redirect:/filmes/" + filmeId;
    }

    // Método auxiliar para pegar o ID do usuário (COPIE do seu FilmeController ou centralize)
    private Integer getUsuarioIdLogado(UserDetails userDetails) {
        if (userDetails instanceof Usuario) {
            return ((Usuario) userDetails).getId();
        }
        // Adicione a lógica para buscar no UsuarioRepository se UserDetails não for sua classe Usuario
        // else if (userDetails != null && usuarioRepository != null) {
        //     Optional<Usuario> userOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        //     return userOpt.map(Usuario::getId).orElse(null);
        // }
        System.out.println("AVISO [ListaController]: Adaptar busca de ID do usuário se UserDetails não for instancia de Usuario.");
        return null;
    }

    // MÉTODO PARA EXIBIR A PÁGINA DE "MEUS FAVORITOS" PAGINADA
    @GetMapping("/meus-favoritos")
    public String viewMeusFavoritosPage(Model model,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                        @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
                                        @RequestParam(name = "sortField", defaultValue = "nome") String sortField,
                                        @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Integer usuarioId = getUsuarioIdLogado(userDetails);
        if (usuarioId == null) {
            model.addAttribute("errorMessage", "Erro: Usuário não identificado para carregar favoritos.");
            // Retorna para home ou uma página de erro genérica, ou mesmo o template 'meus_favoritos' que mostrará a mensagem
            return "home";
        }

        Page<Filme> page = listaService.findFavoritosPaginated(usuarioId, pageNo, pageSize, sortField, sortDir);
        List<Filme> listFilmesFavoritos = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listFilmes", listFilmesFavoritos);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("baseUrl", "/meus-favoritos");

        return "meus_favoritos";
    }
}