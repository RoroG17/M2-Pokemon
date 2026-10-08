package com.example.m2pokemon.service;

import com.example.m2pokemon.dto.DresseurDto;
import com.example.m2pokemon.entity.Dresseur;
import com.example.m2pokemon.entity.Pokemon;
import com.example.m2pokemon.exception.DresseurNotFoundException;
import com.example.m2pokemon.repository.DresseurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DresseurService {

    private static final int SEUIL_DIVERSITE = 3;
    private static final int BONUS_DIVERSITE = 20;
    private static final int SEUIL_ELITE = 50;
    private static final int BONUS_ELITE = 10;

    private final DresseurRepository dresseurRepository;

    public DresseurService(DresseurRepository dresseurRepository) {
        this.dresseurRepository = dresseurRepository;
    }

    @Transactional(readOnly = true)
    public int calculerNiveauDresseur(Long dresseurId) {
        Dresseur dresseur = dresseurRepository.findById(dresseurId)
                .orElseThrow(() -> new DresseurNotFoundException(dresseurId));
        return calculerNiveau(dresseur);
    }

    @Transactional(readOnly = true)
    public DresseurDto getDresseur(Long id) {
        Dresseur dresseur = dresseurRepository.findById(id)
                .orElseThrow(() -> new DresseurNotFoundException(id));
        return new DresseurDto(dresseur.getNom(), dresseur.getPokemons().size(), calculerNiveau(dresseur));
    }

    private int calculerNiveau(Dresseur dresseur) {
        List<Pokemon> pokemons = dresseur.getPokemons();
        if (pokemons.isEmpty()) {
            return 1;
        }

        int niveau = pokemons.stream().mapToInt(Pokemon::getNiveau).sum();

        long typesDistincts = pokemons.stream().map(Pokemon::getType).distinct().count();
        if (typesDistincts >= SEUIL_DIVERSITE) {
            niveau += BONUS_DIVERSITE;
        }

        long nombreElites = pokemons.stream().filter(p -> p.getNiveau() >= SEUIL_ELITE).count();
        niveau += (int) nombreElites * BONUS_ELITE;

        return niveau;
    }
}
