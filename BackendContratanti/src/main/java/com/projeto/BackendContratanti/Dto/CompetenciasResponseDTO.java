package com.projeto.BackendContratanti.Dto;

import com.projeto.BackendContratanti.Model.Competencias;

public record CompetenciasResponseDTO (Integer id, String nome, String email){
    public CompetenciasResponseDTO(Competencias competencias){
        this(competencias.getId(), competencias.getNome(), competencias.getEmail());
    }
}
