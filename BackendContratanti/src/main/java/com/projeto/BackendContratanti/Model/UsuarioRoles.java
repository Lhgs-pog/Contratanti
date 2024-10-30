package com.projeto.BackendContratanti.Model;

public enum UsuarioRoles {
    ADMIN("admin"),
    USER("user");

    private String roles;

    UsuarioRoles(String roles){
        this.roles = roles;
    }

    public String getRoles(){
        return roles;
    }
}
