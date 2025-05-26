package com.StarMovie.star.User;

import com.StarMovie.star.Lista.Lista; // Import para Lista
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importar para getAuthorities
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections; // Para Collections.emptyList()
import java.util.HashSet;   // Para inicializar o Set
import java.util.Set;
import java.util.stream.Collectors; // Para o stream em getAuthorities

@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Nome de usuário é obrigatório")
    @Size(min = 3, max = 50, message = "Nome de usuário deve ter entre 3 e 50 caracteres")
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Senha é obrigatória")
    private String senha; // Senha HASHED

    // Campo para Roles
    @Column(length = 100, nullable = false, columnDefinition = "VARCHAR(100) DEFAULT 'ROLE_USER'")
    private String roles = "ROLE_USER"; // Valor padrão

    // Relacionamento Um-para-Muitos com Lista
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Lista> listas = new HashSet<>();

    // Construtor vazio (JPA)
    public Usuario() {
    }

    // Construtor com campos essenciais (exemplo)
    public Usuario(String username, String email, String senha) {
        this.username = username;
        this.email = email;
        this.senha = senha;
        // 'roles' usará o valor padrão "ROLE_USER"
    }

    // Construtor completo (opcional, pode remover se não usar)
    public Usuario(Integer id, String username, String email, String senha, String roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.senha = senha;
        this.roles = roles;
    }


    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    // getUsername() já faz parte da interface UserDetails
    @Override
    public String getUsername() { return this.username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // getSenha() já faz parte da interface UserDetails
    @Override
    public String getPassword() { return this.senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    public Set<Lista> getListas() { return listas; }
    public void setListas(Set<Lista> listas) { this.listas = listas; }

    // --- MÉTODOS DA INTERFACE UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.roles == null || this.roles.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // Converte a string de roles (separadas por vírgula) em uma coleção de SimpleGrantedAuthority
        return Set.of(this.roles.split(",")) // Usa Set.of para criar o Set de strings
                .stream()
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(SimpleGrantedAuthority::new) // Cria um SimpleGrantedAuthority para cada role string
                .collect(Collectors.toSet()); // Coleta em um Set de GrantedAuthority
    }

    // Para simplificar, vamos assumir que as contas estão sempre ativas e não bloqueadas/expiradas.
    // Você pode adicionar lógica mais complexa aqui se precisar (ex: campos booleanos no banco).
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}