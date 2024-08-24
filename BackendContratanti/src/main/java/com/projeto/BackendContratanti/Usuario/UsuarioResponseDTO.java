package com.projeto.BackendContratanti.Usuario;

public record UsuarioResponseDTO(Long id, String nome, String email ,String telefone ,String url_curriculo ,String url_linkedin ,String url_github ,String cpf ,String cidade ,String senha ,String descricao) {
    public UsuarioResponseDTO(Usuario usuario){
        //esse erro e normal deixa ele ai
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTelefone(), usuario.getUrl_curriculo(), usuario.getUrl_linkedin(), usuario.getUrl_github(), usuario.getCpf(), usuario.getCidade(), usuario.getSenha(), usuario.getDescricao());
    }
}
