package com.projeto.BackendContratanti.Dto;

import com.projeto.BackendContratanti.Model.Competencias;

import java.math.BigInteger;

public record CompetenciasResponseDTO (BigInteger id, String nome, String email){
    public CompetenciasResponseDTO(Competencias competencias){
        this(competencias.getId(), competencias.getNome(), competencias.getEmail());
    }
}
