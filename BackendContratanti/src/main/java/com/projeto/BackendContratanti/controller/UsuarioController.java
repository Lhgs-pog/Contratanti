package com.projeto.BackendContratanti.Controller;

import com.projeto.BackendContratanti.Model.Usuario.Usuario;
import com.projeto.BackendContratanti.Model.Usuario.UsuarioRepository;
import com.projeto.BackendContratanti.Model.Usuario.UsuarioRequestDTO;
import com.projeto.BackendContratanti.Model.Usuario.UsuarioResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Fala para o spring que esta classe e um controller
@RestController
//define o endpoint d aplicacao
@RequestMapping("usuario")
public class UsuarioController {

    //indica ao spring que quando ele for instanciar esse controller ele tera que injetar esse depedencia
    @Autowired
    private UsuarioRepository repository;

    //Faz com que aceite todas as origens e headers enviados pelo cliente, mas e melhor limitar aa origem para apenas o nosso dominio
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    //faz com que todos os requests de post dirigidos ao endpoint seja redirecionado para está função
    @PostMapping
    public void saveUsuario(@RequestBody UsuarioRequestDTO data){
        //converte o objeto dto para o objeto usuario
        Usuario usuariodata = new Usuario(data);
        repository.save(usuariodata);
    }

    //evitar conflito com as apis
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    //faz com que tod vez que um request get seja dirigido ao endpoint usuario ele entrara aqui
    @GetMapping
    public List<UsuarioResponseDTO> getAll(){
        List<UsuarioResponseDTO> listausuario = repository.findAll().stream().map(UsuarioResponseDTO::new).toList();
        return listausuario;
    }
}
