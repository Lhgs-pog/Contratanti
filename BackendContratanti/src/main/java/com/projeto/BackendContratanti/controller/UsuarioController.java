package com.projeto.BackendContratanti.Controller;

import com.projeto.BackendContratanti.Model.Usuario;
import com.projeto.BackendContratanti.Dto.UsuarioRequestDTO;
import com.projeto.BackendContratanti.Dto.UsuarioResponseDTO;
import com.projeto.BackendContratanti.Services.UsuarioServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

//Fala para o spring que esta classe e um controller
@RestController
//define o endpoint d aplicacao
@RequestMapping("usuario")
public class UsuarioController {

    //indica ao spring que quando ele for instanciar esse controller ele tera que injetar esse depedencia
    @Autowired
    private UsuarioServices services;

    //Faz com que aceite todas as origens e headers enviados pelo cliente, mas e melhor limitar aa origem para apenas o nosso dominio
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    //faz com que todos os requests de post dirigidos ao endpoint seja redirecionado para está função
    @PostMapping
    public void saveUsuario(@RequestBody UsuarioRequestDTO data){
        services.saveUsuario(data);
    }

    //evitar conflito com as apis
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    //faz com que tod vez que um request get seja dirigido ao endpoint usuario ele entrara aqui
    @GetMapping("/usuarios")
    public List<UsuarioResponseDTO> getAll(){
        return services.usuariosGetAll();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    //faz com que tod vez que um request get seja dirigido ao endpoint usuario ele entrara aqui
    @GetMapping("/usuarios/{uid}")
    public Optional<Usuario> getById(@PathVariable("uid")BigInteger uid, @RequestBody Usuario usuario){
        return services.usuariofindById(usuario, uid);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping(path = "/usuarios/update", consumes = "application/json")
    public Usuario updateUsuario(@RequestBody Usuario user){
        return services.usuarioUpdate(user);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("usuarios/delete")
    public void deleteAll(){
        services.usuarioDeleteAll();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("usuarios/delete/{uid}")
    public void deleteById(@PathVariable("uid") BigInteger uid){
        services.usuarioDeleteById(uid);
    }

}
