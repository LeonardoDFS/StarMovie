package com.StarMovie.star.User;


import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional; // Se necessário

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional // Garante atomicidade
    public void registerUser(Usuario usuario) throws Exception {
        // 1. Verificar se username já existe
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new Exception("Username já cadastrado: " + usuario.getUsername());
        }
        // 2. Verificar se email já existe
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new Exception("Email já cadastrado: " + usuario.getEmail());
        }

        // 3. HASH a senha ANTES de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getPassword()));

        // 4. Salvar o usuário
        usuarioRepository.save(usuario);
    }
}
