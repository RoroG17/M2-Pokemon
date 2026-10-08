package com.example.m2pokemon.controller;

import com.example.m2pokemon.dto.DresseurDto;
import com.example.m2pokemon.entity.Dresseur;
import com.example.m2pokemon.exception.DresseurNotFoundException;
import com.example.m2pokemon.service.DresseurService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DresseurController.class)
class DresseurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DresseurService dresseurService;

    @Test
    void should_return_200_and_dto_when_dresseur_exists() throws Exception {
        // Arrange
        DresseurDto d = new DresseurDto("Sacha", 3, 135);
        when(dresseurService.getDresseur(1L)).thenReturn(d);

        // Act & Assert
        mockMvc.perform(get("/dresseurs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomDresseur").value(d.nomDresseur()))
                .andExpect(jsonPath("$.nombrePokemons").value(d.nombrePokemons()))
                .andExpect(jsonPath("$.niveauDresseur").value(d.niveauDresseur()));
    }

    @Test
    void should_return_404_when_dresseur_does_not_exist() throws Exception {
        // Arrange
        when(dresseurService.getDresseur(99L)).thenThrow(new DresseurNotFoundException(99L));

        // Act & Assert
        mockMvc.perform(get("/dresseurs/99"))
                .andExpect(status().isNotFound());
    }
}
