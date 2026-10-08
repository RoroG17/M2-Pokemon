package com.example.m2pokemon.service;

import com.example.m2pokemon.dto.DresseurDto;
import com.example.m2pokemon.entity.Dresseur;
import com.example.m2pokemon.entity.Pokemon;
import com.example.m2pokemon.exception.DresseurNotFoundException;
import com.example.m2pokemon.repository.DresseurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DresseurServiceTest {

    @Mock
    private DresseurRepository dresseurRepository;

    @InjectMocks
    private DresseurService dresseurService;

    private Dresseur dresseurAvec(Pokemon... pokemons) {
        Dresseur dresseur = new Dresseur("Sacha", "Kanto");
        for (Pokemon pokemon : pokemons) {
            pokemon.setDresseur(dresseur);
            dresseur.getPokemons().add(pokemon);
        }
        return dresseur;
    }

    @Test
    void should_return_1_when_dresseur_has_no_pokemon() {
        // Arrange
        when(dresseurRepository.findById(1L)).thenReturn(Optional.of(dresseurAvec()));

        // Act
        int niveau = dresseurService.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(1, niveau);
        verify(dresseurRepository).findById(1L);
    }

    @Test
    void should_return_sum_of_levels_when_no_bonus_applies() {
        // Arrange
        Dresseur dresseur = dresseurAvec(new Pokemon("Pikachu", "Electrik", 20), new Pokemon("Salameche", "Feu", 30));
        when(dresseurRepository.findById(1L)).thenReturn(Optional.of(dresseur));

        // Act
        int niveau = dresseurService.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(50, niveau);
    }

    @Test
    void should_apply_diversity_bonus_when_dresseur_has_3_different_types() {
        // Arrange
        Dresseur dresseur = dresseurAvec(
                new Pokemon("Pikachu", "Electrik", 10),
                new Pokemon("Salameche", "Feu", 20),
                new Pokemon("Carapuce", "Eau", 30));
        when(dresseurRepository.findById(1L)).thenReturn(Optional.of(dresseur));

        // Act
        int niveau = dresseurService.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(80, niveau);
    }

    @Test
    void should_apply_elite_bonus_when_pokemon_level_is_50() {
        // Arrange
        Dresseur dresseur = dresseurAvec(new Pokemon("Pikachu", "Electrik", 50));
        when(dresseurRepository.findById(1L)).thenReturn(Optional.of(dresseur));

        // Act
        int niveau = dresseurService.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(60, niveau);
    }

    @Test
    void should_cumulate_bonuses_when_diversity_and_elite_apply() {
        // Arrange
        Dresseur dresseur = dresseurAvec(
                new Pokemon("Pikachu", "Electrik", 55),
                new Pokemon("Salameche", "Feu", 20),
                new Pokemon("Carapuce", "Eau", 30));
        when(dresseurRepository.findById(1L)).thenReturn(Optional.of(dresseur));

        // Act
        int niveau = dresseurService.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(135, niveau);
    }

    @Test
    void should_throw_exception_when_dresseur_does_not_exist() {
        // Arrange
        when(dresseurRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DresseurNotFoundException.class, () -> dresseurService.calculerNiveauDresseur(99L));
    }

    @Test
    void should_return_dto_when_dresseur_exists() {
        // Arrange
        Dresseur dresseur = dresseurAvec(
                new Pokemon("Pikachu", "Electrik", 55),
                new Pokemon("Salameche", "Feu", 20),
                new Pokemon("Carapuce", "Eau", 30));
        when(dresseurRepository.findById(1L)).thenReturn(Optional.of(dresseur));

        // Act
        DresseurDto dto = dresseurService.getDresseur(1L);

        // Assert
        assertEquals(new DresseurDto("Sacha", 3, 135), dto);
    }
}
