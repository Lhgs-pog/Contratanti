package com.projeto.BackendContratanti.Services;

import com.projeto.BackendContratanti.Model.Usuario.Usuario;
import com.projeto.BackendContratanti.Model.Usuario.UsuarioRepository;
import com.projeto.BackendContratanti.Model.Usuario.UsuarioRequestDTO;
import com.projeto.BackendContratanti.Model.Usuario.UsuarioResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UsuarioServices {

    @Autowired
    private UsuarioRepository repository;

    public List<UsuarioResponseDTO> usuariosGetAll(){
        List<UsuarioResponseDTO> listausuarios = repository.findAll().stream().map(UsuarioResponseDTO::new).toList();
        return listausuarios;
    }
    public Optional<Usuario> usuariofindById(Usuario usuario, BigInteger id){
        return repository.findById(id);
    }
    public void saveUsuario(UsuarioRequestDTO data){
        Usuario usuariodata = new Usuario(data);
        repository.save(usuariodata);
    }

    public Usuario usuarioUpdate(Usuario user){
        repository.save(user);
        return user;
    }

    public void usuarioDeleteAll(){
        repository.deleteAll();
    }

    public void usuarioDeleteById(BigInteger id){
        Usuario user = repository.getOne(id);
        repository.delete(user);
    }

    //regex para permitir a entrada de somente letras e numeros, prevenindo sqlinjection e outros tipos de ataque
    public String validarString(String linha){
        Pattern pattern = Pattern.compile("^[A-Za-z1-9]$");
        Matcher matcher = pattern.matcher(linha);
        boolean matchfound = matcher.find();
        if (matchfound)
            return linha;
        return "";
    }
}
