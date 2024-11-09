package com.projeto.BackendContratanti.Dto;

import com.projeto.BackendContratanti.Model.Usuario;

import java.math.BigInteger;

public record UsuarioResponseDTO(BigInteger id, String nome, String email , String telefone , String url_curriculo , String url_linkedin , String url_github , String cpf , String cidade , String senha , String descricao) {
    public UsuarioResponseDTO(Usuario usuario){
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTelefone(), usuario.getUrl_curriculo(), usuario.getUrl_linkedin(), usuario.getUrl_github(), usuario.getCpf(), usuario.getCidade(), usuario.getSenha(), usuario.getDescricao());
    }
}
