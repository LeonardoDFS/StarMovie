package com.StarMovie.star.Perfil; // Ou o pacote do seu PerfilController

import com.StarMovie.star.Filme.Filme; // Para o Set<Filme> dos favoritos
import com.StarMovie.star.Lista.ListaService;
import com.StarMovie.star.NotaFil.NotaFil; // Para a List<NotaFil> das avaliações
import com.StarMovie.star.NotaFil.NotaFilService;
import com.StarMovie.star.User.Usuario;
import com.StarMovie.star.User.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class PerfilController {

    @Autowired
    private ListaService listaService;

    @Autowired
    private NotaFilService notaFilService;

    @Autowired
    private UsuarioRepository usuarioRepository; // Para buscar o objeto Usuario completo

    // Método auxiliar para pegar o ID do usuário (ADAPTAR À SUA REALIDADE)
    private Integer getUsuarioIdLogado(UserDetails userDetails) {
        if (userDetails instanceof Usuario) {
            return ((Usuario) userDetails).getId();
        } else if (userDetails != null) { // Se UserDetails for o padrão do Spring
            Optional<Usuario> userOpt = usuarioRepository.findByUsername(userDetails.getUsername());
            if (userOpt.isPresent()) {
                return userOpt.get().getId();
            }
        }
        System.out.println("AVISO [PerfilController]: Não foi possível obter ID do usuário logado de forma customizada.");
        return null;
    }

    private Usuario getUsuarioLogadoCompleto(UserDetails userDetails) {
        if (userDetails instanceof Usuario) {
            return (Usuario) userDetails;
        } else if (userDetails != null) {
            return usuarioRepository.findByUsername(userDetails.getUsername()).orElse(null);
        }
        return null;
    }


    @GetMapping("/perfil")
    public String viewPerfilPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogado = getUsuarioLogadoCompleto(userDetails);

        if (usuarioLogado == null) {
            return "redirect:/login?error=usuariodesconhecido";
        }

        model.addAttribute("usuario", usuarioLogado);

        // 1. Buscar filmes favoritos
        Set<Filme> filmesFavoritos = listaService.getFilmesFavoritos(usuarioLogado.getId());
        model.addAttribute("listaFavoritos", filmesFavoritos);

        // 2. Buscar avaliações do usuário
        List<NotaFil> notasDoUsuario = notaFilService.getNotasDoUsuario(usuarioLogado.getId());
        model.addAttribute("notasDoUsuario", notasDoUsuario);

        return "perfil";
    }
}