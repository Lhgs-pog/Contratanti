package com.projeto.BackendContratanti.Controller;

import com.projeto.BackendContratanti.Model.Usuario;
import com.projeto.BackendContratanti.Dto.UsuarioRequestDTO;
import com.projeto.BackendContratanti.Dto.UsuarioResponseDTO;
import com.projeto.BackendContratanti.Services.UsuarioServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

// Informa que essa classe é um controller e expõe endpoints HTTP.
@RestController
// Define a URL base para os endpoints relacionados ao usuário.
@RequestMapping("/usuario")
public class UsuarioController {

    // Injeta a dependência de services no controller.
    @Autowired
    private UsuarioServices services;

    @Autowired
    private PasswordEncoder passwordEncoder;
    // A configuração de CORS pode ser feita em um arquivo de configuração global.
    // Aqui está sendo aplicada a cada método, mas pode ser centralizada.

    /**
     * Endpoint para salvar um novo usuário.
     */
    @PostMapping
    public ResponseEntity<Void> saveUsuario(@RequestBody UsuarioRequestDTO data) {
        Usuario usuario = new Usuario(data);
        String senhaCripitografda = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCripitografda);
        services.saveUsuario(usuario);
        return ResponseEntity.status(201).build();  // Retorna status HTTP 201 (Created)
    }

    /**
     * Endpoint para retornar todos os usuários.
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAll() {
        List<UsuarioResponseDTO> usuarios = services.usuariosGetAll();
        return ResponseEntity.ok(usuarios);  // Retorna status HTTP 200 (OK)
    }

    /**
     * Endpoint para retornar um usuário específico por ID.
     * Utiliza `ResponseEntity` para permitir retornar status adequado.
     */
    @GetMapping("/{uid}")
    public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable("uid") BigInteger uid) {
        Optional<Usuario> usuario = services.usuariofindById(uid);

        return usuario.map(value -> ResponseEntity.ok(new UsuarioResponseDTO(value)))  // Retorna 200 OK com o DTO
                .orElseGet(() -> ResponseEntity.status(404).build());  // Retorna 404 caso não encontrado
    }

    /**
     * Endpoint para atualizar os dados de um usuário.
     */
    @PutMapping("/{uid}")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(@PathVariable("uid") BigInteger uid, @RequestBody UsuarioRequestDTO data) {
        Usuario updatedUsuario = services.usuarioUpdate(uid, data);
        return ResponseEntity.ok(new UsuarioResponseDTO(updatedUsuario));  // Retorna 200 OK com o DTO atualizado
    }

    /**
     * Endpoint para deletar todos os usuários.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        services.usuarioDeleteAll();
        return ResponseEntity.status(204).build();  // Retorna 204 No Content após exclusão
    }

    /**
     * Endpoint para deletar um usuário específico por ID.
     */
    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> deleteById(@PathVariable("uid") BigInteger uid) {
        services.usuarioDeleteById(uid);
        return ResponseEntity.noContent().build();
    }
}
