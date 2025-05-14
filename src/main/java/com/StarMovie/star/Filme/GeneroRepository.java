package com.StarMovie.star.Filme; // Ou pacote apropriado

import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneroRepository extends JpaRepository<Genero, Integer> {
}