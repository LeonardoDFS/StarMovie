package com.StarMovie.star.Filme; // Ou seu pacote de Genero

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GeneroService {
    @Autowired
    private GeneroRepository generoRepository;

    public List<Genero> findAll() {
        return generoRepository.findAll(); // Ordenação padrão (por ID)
        // Se quiser ordenado por nome: return generoRepository.findAll(Sort.by("nome"));
    }
}