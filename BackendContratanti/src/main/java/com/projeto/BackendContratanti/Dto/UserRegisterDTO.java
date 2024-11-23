package com.projeto.BackendContratanti.Dto;

import com.projeto.BackendContratanti.Model.UsuarioRoles;

public record UserRegisterDTO(String email, String senha, UsuarioRoles role) {
}
