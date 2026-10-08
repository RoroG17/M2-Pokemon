package com.example.m2pokemon.repository;

import com.example.m2pokemon.entity.Dresseur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class DresseurRepositoryTest {

    @Autowired
    private DresseurRepository dresseurRepository;

    @Test
    void should_find_dresseur_when_id_exists() {
        // Arrange
        Long id = 1L;

        // Act
        Optional<Dresseur> result = dresseurRepository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Sacha", result.get().getNom());
        assertEquals("Kanto", result.get().getRegion());
    }

    @Test
    void should_return_empty_when_id_does_not_exist() {
        // Arrange
        Long id = 999L;

        // Act
        Optional<Dresseur> result = dresseurRepository.findById(id);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void should_load_pokemons_when_dresseur_has_pokemons() {
        // Arrange
        Long id = 1L;

        // Act
        Dresseur dresseur = dresseurRepository.findById(id).orElseThrow();

        // Assert
        assertEquals(3, dresseur.getPokemons().size());
    }

    @Test
    void should_persist_dresseur_when_saved() {
        // Arrange
        Dresseur nouveau = new Dresseur("Pierre", "Kanto");

        // Act
        Dresseur saved = dresseurRepository.save(nouveau);

        // Assert
        assertNotNull(saved.getId());
        assertTrue(dresseurRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void should_find_dresseur_when_nom_exists() {
        // Arrange
        String nom = "Ondine";

        // Act
        Optional<Dresseur> result = dresseurRepository.findByNom(nom);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Kanto", result.get().getRegion());
    }

    @Test
    void should_return_dresseurs_when_region_matches() {
        // Arrange
        String region = "Kanto";

        // Act
        List<Dresseur> result = dresseurRepository.findByRegion(region);

        // Assert
        assertEquals(2, result.size());
    }
}
