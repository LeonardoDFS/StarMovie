package com.StarMovie.star.NotaFil;

import java.io.Serializable;
import java.util.Objects;

// Não precisa de anotações JPA aqui, apenas ser Serializable e ter equals/hashCode
public class NotaFilId implements Serializable {

    private Integer usuarioId; // Deve ter o MESMO NOME E TIPO dos campos @Id em NotaFil
    private Integer filmeId;   // Deve ter o MESMO NOME E TIPO dos campos @Id em NotaFil

    // Construtor vazio (necessário)
    public NotaFilId() {}

    public NotaFilId(Integer usuarioId, Integer filmeId) {
        this.usuarioId = usuarioId;
        this.filmeId = filmeId;
    }

    // Getters e Setters
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getFilmeId() { return filmeId; }
    public void setFilmeId(Integer filmeId) { this.filmeId = filmeId; }

    // Equals e HashCode (MUITO IMPORTANTE para chaves compostas!)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotaFilId that = (NotaFilId) o;
        return Objects.equals(usuarioId, that.usuarioId) && Objects.equals(filmeId, that.filmeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, filmeId);
    }
}