package com.StarMovie.star.User;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    // Injete seu UserDetailsService customizado (criaremos no próximo passo)
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        // SEMPRE use um encoder forte para senhas
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register/**", "/process_register", "/css/**", "/js/**", "/images/**").permitAll() // Permite acesso ao registro e recursos estáticos
                        .requestMatchers("/login").permitAll() // Permite acesso à página de login
                        .anyRequest().authenticated() // Exige autenticação para qualquer outra requisição
                )
                .formLogin(form -> form
                        .loginPage("/login") // Define a página de login customizada
                        .loginProcessingUrl("/login") // URL que o Spring Security processa o login (pode ser a mesma)
                        .defaultSuccessUrl("/home", true) // Redireciona para /home após login sucesso
                        .permitAll() // Permite acesso a todos para a URL de processamento do login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para fazer logout
                        .logoutSuccessUrl("/login?logout") // Redireciona após logout
                        .permitAll()
                )
                .userDetailsService(userDetailsService); // Informa ao Spring Security como carregar usuários

        return http.build();
    }
}
