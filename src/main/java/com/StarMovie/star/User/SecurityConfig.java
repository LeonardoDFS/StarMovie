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
                        // Permissões públicas
                        .requestMatchers("/", "/home", "/login", "/register/**", "/process_register",
                                "/css/**", "/js/**", "/images/**",
                                "/filmes", "/filmes/{id}", "/buscar", "/webjars/**").permitAll()

                        // Permissões para usuários logados (USER ou ADMIN)
                        .requestMatchers("/perfil", "/meus-favoritos", "/minhas-avaliacoes",
                                "/filmes/{filmeId}/favoritar", "/filmes/{filmeId}/desfavoritar",
                                "/filmes/{filmeId}/avaliar").authenticated() // Ou .hasAnyRole("USER", "ADMIN")

                        // Permissões SOMENTE PARA ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Qualquer URL começando com /admin exige ROLE_ADMIN

                        .anyRequest().authenticated() // Todas as outras requisições exigem autenticação
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .userDetailsService(userDetailsService);
        return http.build();
    }
}
