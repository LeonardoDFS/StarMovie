package com.StarMovie.star.NotaFil; // Mesmo pacote

import com.StarMovie.star.Filme.Filme; // Importe Filme
import com.StarMovie.star.User.Usuario; // Importe Usuario
import jakarta.persistence.*;

@Entity
@Table(name = "notafil")
@IdClass(NotaFilId.class) // Especifica a classe da chave composta
public class NotaFil {

    // --- Chave Primária Composta ---
    @Id
    @Column(name = "usuario_id") // Coluna no banco
    private Integer usuarioId;    // Nome IGUAL ao da classe NotaFilId

    @Id
    @Column(name = "filme_id")  // Coluna no banco
    private Integer filmeId;     // Nome IGUAL ao da classe NotaFilId
    // -----------------------------

    // --- Relacionamentos (Opcional carregar EAGER aqui, LAZY é melhor) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId") // Diz que o campo 'usuarioId' acima mapeia para o ID deste relacionamento
    @JoinColumn(name = "usuario_id") // FK no banco
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("filmeId") // Diz que o campo 'filmeId' acima mapeia para o ID deste relacionamento
    @JoinColumn(name = "filme_id") // FK no banco
    private Filme filme;
    // -----------------------------

    @Column(name = "nota") // Coluna 'nota' TINYINT no banco
    private Integer nota; // 1 a 5 (Integer permite null se não houver nota)

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario; // Para usar depois (RF11)

    // Construtor vazio
    public NotaFil() {}

    // Construtor útil
    public NotaFil(Usuario usuario, Filme filme, Integer nota) {
        this.usuario = usuario;
        this.filme = filme;
        this.usuarioId = usuario.getId(); // Define as partes da chave
        this.filmeId = filme.getId();     // Define as partes da chave
        this.nota = nota;
    }

    // Getters e Setters (incluindo para usuario e filme)
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getFilmeId() { return filmeId; }
    public void setFilmeId(Integer filmeId) { this.filmeId = filmeId; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Filme getFilme() { return filme; }
    public void setFilme(Filme filme) { this.filme = filme; }
    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}