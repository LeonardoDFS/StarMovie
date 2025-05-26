package com.StarMovie.star.Admin; // Novo pacote Admin

import com.StarMovie.star.Filme.FilmeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // Outra forma de autorizar
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin") // Todas as rotas neste controller começarão com /admin
// @PreAuthorize("hasRole('ADMIN')") // Alternativamente, pode proteger a classe toda
public class AdminController {

    @Autowired
    private FilmeService filmeService;

    // Endpoint para deletar filme (requer POST para segurança)
    @PostMapping("/filmes/{id}/deletar")
    // @PreAuthorize("hasRole('ADMIN')") // Protege o método individualmente
    public String deletarFilme(@PathVariable("id") Integer filmeId, RedirectAttributes redirectAttributes) {
        try {
            filmeService.deletarFilmePorId(filmeId);
            redirectAttributes.addFlashAttribute("successMessage", "Filme ID " + filmeId + " deletado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar filme: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/filmes"; // Redireciona para o catálogo de filmes
    }

    // TODO: Adicionar GET para página de gerenciamento de filmes que lista filmes com botão de delete
    // TODO: Adicionar GET e POST para gerenciamento de usuários (listar, promover admin)
}