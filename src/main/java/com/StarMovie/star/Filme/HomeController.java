package com.StarMovie.star.Filme; // Verifique se este é o pacote correto

// Imports necessários
import com.StarMovie.star.Lista.ListaService; // Para buscar favoritos
import com.StarMovie.star.User.Usuario;      // Sua entidade Usuario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Set; // ListaService retorna um Set<Filme> para favoritos

@Controller
public class HomeController {

    // Injeta os serviços necessários
    @Autowired
    private FilmeService filmeService;

    @Autowired
    private ListaService listaService;

    // Considere injetar UsuarioRepository se precisar buscar Usuario pelo username do UserDetails padrão
    // @Autowired private UsuarioRepository usuarioRepository;

    /**
     * Manipula as requisições para a página inicial (raiz "/" e "/home").
     * Busca filmes para os carrosséis principal, populares e favoritos (reais).
     */
    @GetMapping({"/", "/home"})
    public String homePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Busca filmes para carrossel principal e populares
        List<Filme> filmesCarrosselPrincipal = filmeService.getFilmesCarrosselPrincipal(5);
        List<Filme> filmesPopulares = filmeService.getFilmesPopulares(10); // Já filtra não adultos no Service/Repo

        // 2. Tenta obter o ID do usuário logado
        Integer usuarioId = null;
        if (userDetails != null) {
            // Adapte esta lógica conforme sua implementação de UserDetails/Usuario
            if (userDetails instanceof Usuario) {
                usuarioId = ((Usuario) userDetails).getId();
            } else {
                System.out.println("AVISO [Home]: Adaptar busca de ID do usuário para favoritos.");
                // Se userDetails não for sua classe Usuario, você precisaria:
                // Optional<Usuario> userOpt = usuarioRepository.findByUsername(userDetails.getUsername());
                // if (userOpt.isPresent()) {
                //     usuarioId = userOpt.get().getId();
                // }
            }
        }

        // 3. Busca a lista REAL de filmes favoritos usando o ListaService
        //    Se usuarioId for null (não logado), o serviço deve retornar um Set vazio
        Set<Filme> filmesFavoritos = listaService.getFilmesFavoritos(usuarioId);

        // 4. Adiciona todas as listas e informações ao Model
        model.addAttribute("filmesCarrosselPrincipal", filmesCarrosselPrincipal);
        model.addAttribute("filmesPopulares", filmesPopulares);
        model.addAttribute("filmesFavoritos", filmesFavoritos); // Adiciona a lista REAL
        model.addAttribute("usuarioLogado", userDetails != null);

        // 5. Debug (Opcional, pode remover depois)
        System.out.println("--- Filmes Carrossel Principal (Debug Detalhado) ---");
        if (filmesCarrosselPrincipal != null) {
            for (Filme filme : filmesCarrosselPrincipal) {
                System.out.println("ID: " + filme.getId() +
                        ", Nome: [" + filme.getNome() +
                        "], Poster: [" + filme.getPosterPath() +
                        "], Fundo: [" + filme.getCaminhoFundo() + "]");
            }
        } else {
            System.out.println("Lista filmesCarrosselPrincipal é NULA!");
        }
        System.out.println("----------------------------------------------------");
        System.out.println("Carrossel Principal Size: " + (filmesCarrosselPrincipal != null ? filmesCarrosselPrincipal.size() : "null"));
        System.out.println("Favoritos Reais Size: " + (filmesFavoritos != null ? filmesFavoritos.size() : "null")); // Debug favoritos

        // 6. Retorna o nome do template HTML
        return "home";
    }

    /**
     * Manipula as requisições para a página de busca.
     */
    @GetMapping("/buscar")
    public String buscarFilmes(@RequestParam("query") String query, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("Buscando por: " + query);
        // O método buscarFilmesPorNome no service já deve filtrar não adultos
        List<Filme> resultados = filmeService.buscarFilmesPorNome(query, 20);

        model.addAttribute("query", query);
        model.addAttribute("resultadosBusca", resultados);
        model.addAttribute("usuarioLogado", userDetails != null);

        // Debug da busca (Opcional)
        System.out.println("--- Debug Resultados da Busca ---");
        // ... (loop de debug que você já tinha) ...
        System.out.println("-------------------------------");

        return "resultados_busca";
    }
}