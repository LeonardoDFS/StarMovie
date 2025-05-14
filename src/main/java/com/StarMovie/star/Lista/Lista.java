package com.StarMovie.star.Lista;

import com.StarMovie.star.Filme.Filme; // Importe Filme
import com.StarMovie.star.User.Usuario; // Importe Usuario
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Lista")
public class Lista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false) // Assumindo VARCHAR(100) pelo diagrama
    private String nome; // Ex: "Favoritos", "Para Assistir"

    // Relacionamento Muitos-para-Um com Usuario
    @ManyToOne(fetch = FetchType.LAZY) // LAZY é geralmente melhor aqui
    @JoinColumn(name = "usuario_id", nullable = false) // Coluna FK no banco
    private Usuario usuario;

    // Relacionamento Muitos-para-Muitos com Filme (via tabela listafilme)
    @ManyToMany(fetch = FetchType.LAZY) // LAZY para não carregar todos os filmes sempre
    @JoinTable(
            name = "listafilme", // Nome da tabela de junção
            joinColumns = @JoinColumn(name = "lista_id"), // FK para Lista nesta tabela
            inverseJoinColumns = @JoinColumn(name = "filme_id") // FK para Filme nesta tabela
    )
    private Set<Filme> filmes = new HashSet<>();

    // Construtores
    public Lista() {}

    public Lista(String nome, Usuario usuario) {
        this.nome = nome;
        this.usuario = usuario;
    }

    // Getters e Setters (para todos os campos)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Set<Filme> getFilmes() { return filmes; }
    public void setFilmes(Set<Filme> filmes) { this.filmes = filmes; }

    // Métodos utilitários (opcional)
    public void addFilme(Filme filme) {
        this.filmes.add(filme);
        // Se Filme tivesse a relação inversa, adicionaria aqui também:
        // filme.getListas().add(this);
    }

    public void removeFilme(Filme filme) {
        this.filmes.remove(filme);
        // filme.getListas().remove(this);
    }
}
