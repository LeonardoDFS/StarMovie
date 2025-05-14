package com.StarMovie.star.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Para validação
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid; // Para validação


@Controller
public class AuthController {
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Exibir página de Login
    @GetMapping("/login")
    public String showLoginForm() {
        // Spring Security geralmente lida com a exibição se a página existir
        // Este método garante que temos um endpoint mapeado
        return "login"; // Nome do arquivo HTML (login.html)
    }

    // Exibir página de Registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Adiciona um objeto Usuario vazio ao modelo para o formulário Thymeleaf
        model.addAttribute("usuario", new Usuario());
        return "register"; // Nome do arquivo HTML (register.html)
    }

    // Processar formulário de Registro
    @PostMapping("/process_register")
    public String processRegistration(@Valid @ModelAttribute("usuario") Usuario usuario,
                                      BindingResult bindingResult, // Resultados da validação
                                      RedirectAttributes redirectAttributes) {

        // Se houver erros de validação (definidos na Entidade com @NotBlank, @Email, etc.)
        if (bindingResult.hasErrors()) {
            // Retorna para o formulário de registro mostrando os erros
            return "register";
        }

        try {
            usuarioService.registerUser(usuario);
            redirectAttributes.addFlashAttribute("successMessage", "Registro realizado com sucesso! Faça o login.");
            return "redirect:/login"; // Redireciona para a página de login após sucesso
        } catch (Exception e) {
            // Adiciona o erro ao BindingResult para exibir na página
            bindingResult.rejectValue(null, "error.registration", e.getMessage()); // Chave genérica
            // Ou pode adicionar diretamente ao RedirectAttributes se preferir
            // redirectAttributes.addFlashAttribute("errorMessage", "Erro ao registrar: " + e.getMessage());
            // return "redirect:/register"; // Redireciona de volta ou re-renderiza
            return "register"; // Re-renderiza a página de registro com o erro
        }
    }
/*
    // Exemplo de página inicial após login
    @GetMapping("/home")
    public String homePage() {
        return "home"; // Nome do arquivo HTML (home.html)
    }
    // Mapeamento raiz apenas para ter um ponto de entrada inicial
    @GetMapping("/")
    public String rootPage() {
        return "redirect:/home"; // Ou redireciona para login se não autenticado
    }*/
}
