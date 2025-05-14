package com.StarMovie.star.NotaFil; // Ou o pacote correto

// --- IMPORTS CORRETOS E NECESSÁRIOS ---
import com.StarMovie.star.Filme.Filme;         // Se precisar da entidade Filme diretamente
import com.StarMovie.star.User.Usuario;        // Sua entidade Usuario
import com.StarMovie.star.User.UsuarioRepository; // Para buscar Usuario se UserDetails for padrão
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // <<< ESTE É O IMPORT CORRETO PARA O MODEL
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List; // Para a lista de NotaFil em viewMinhasAvaliacoesPage
import java.util.Optional; // Para buscar usuário opcionalmente

@Controller
public class NotaController {

    @Autowired
    private NotaFilService notaFilService;

    // Descomente e injete se sua lógica em getUsuarioIdLogado precisar
    // @Autowired
    // private UsuarioRepository usuarioRepository;

    @GetMapping("/minhas-avaliacoes")
    public String viewMinhasAvaliacoesPage(Model model, // Tipo Model correto
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                           @RequestParam(name = "sortField", defaultValue = "nota") String sortField,
                                           @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Integer usuarioId = getUsuarioIdLogado(userDetails);
        if (usuarioId == null) {
            // Adiciona mensagem de erro ao Model para ser exibida na página home
            model.addAttribute("errorMessage", "Usuário não identificado para carregar avaliações.");
            // Idealmente, você teria uma página de erro mais genérica ou logaria isso.
            // Redirecionar para home com mensagem é uma opção.
            return "home";
        }

        Page<NotaFil> page = notaFilService.findAvaliacoesPaginated(usuarioId, pageNo, pageSize, sortField, sortDir);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listAvaliacoes", page.getContent()); // Lista de NotaFil
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("baseUrl", "/minhas-avaliacoes");

        return "minhas_avaliacoes";
    }

    @PostMapping("/filmes/{filmeId}/avaliar")
    public String salvarAvaliacao(@PathVariable("filmeId") Integer filmeId,
                                  @RequestParam("nota") int nota,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            Integer usuarioId = getUsuarioIdLogado(userDetails);
            if(usuarioId == null) {
                // Lança exceção para ser pega pelo catch e mostrar mensagem de erro
                throw new RuntimeException("Não foi possível obter ID do usuário logado para salvar avaliação.");
            }

            notaFilService.salvarOuAtualizarNota(usuarioId, filmeId, nota);
            redirectAttributes.addFlashAttribute("successMessage", "Sua nota " + nota + " foi salva!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao salvar sua nota: " + e.getMessage());
            // e.printStackTrace(); // Bom para debug no console do servidor
        }

        return "redirect:/filmes/" + filmeId;
    }

    // Método auxiliar para pegar o ID do usuário
    // Certifique-se que esta lógica está correta para sua implementação de Usuario e UserDetails
    private Integer getUsuarioIdLogado(UserDetails userDetails) {
        if (userDetails instanceof Usuario) {
            return ((Usuario) userDetails).getId();
        }
        // else if (userDetails != null && usuarioRepository != null) { // Se UserDetails for o padrão do Spring
        //     Optional<Usuario> userOpt = usuarioRepository.findByUsername(userDetails.getUsername());
        //     return userOpt.map(Usuario::getId).orElse(null);
        // }
        System.out.println("AVISO [NotaController]: Adaptar busca de ID do usuário se UserDetails não for instancia de Usuario.");
        return null;
    }
}