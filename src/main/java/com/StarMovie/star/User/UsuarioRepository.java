package com.StarMovie.star.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    /* Método para buscar um usuário pelo username (usado pelo Spring Security)*/
    Optional<Usuario> findByUsername(String username);

    // Método para buscar um usuário pelo email (útil para validação no registro)
    Optional<Usuario> findByEmail(String email);

    // Spring Data JPA gera as implementações básicas (save, findById, findAll, etc.)
}
