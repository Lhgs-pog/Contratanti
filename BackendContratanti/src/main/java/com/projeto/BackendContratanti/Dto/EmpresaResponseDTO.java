package com.projeto.BackendContratanti.Dto;

import com.projeto.BackendContratanti.Model.Empresa;


import java.math.BigInteger;

public record EmpresaResponseDTO (int id,String nome, String email, String telefone, String cnpj, String descricao, String url_linkedin, String senha){
    public EmpresaResponseDTO(Empresa empresa) {
        this(empresa.getId(), empresa.getNome(), empresa.getEmail(), empresa.getTelefone(), empresa.getUrl_linkedin(), empresa.getCnpj(), empresa.getSenha(), empresa.getDescricao());
    }
}
