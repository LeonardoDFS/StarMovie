package com.StarMovie.star.Filme; // Ou o pacote correto

import jakarta.persistence.*;

@Entity
@Table(name = "genero")
public class Genero {

    @Id
    private Integer id; // Assumindo que o ID vem da TMDB / Script

    @Column(length = 50)
    private String nome;

    // Construtor vazio
    public Genero() {}

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}