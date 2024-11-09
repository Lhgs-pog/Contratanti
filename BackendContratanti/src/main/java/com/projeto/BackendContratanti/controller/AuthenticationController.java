package com.projeto.BackendContratanti.Controller;

// Importa classes e pacotes necessários para DTOs, modelo de usuário, repositório, segurança, etc.
import com.projeto.BackendContratanti.Dto.AuthenticationDTO;
import com.projeto.BackendContratanti.Dto.LoginResponseDTO;
import com.projeto.BackendContratanti.Dto.RegisterDTO;
import com.projeto.BackendContratanti.Model.Usuario;
import com.projeto.BackendContratanti.Reposotory.UsuarioRepository;
import com.projeto.BackendContratanti.Security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController // Indica que essa classe é um controlador REST
@RequestMapping("auth") // Define o endpoint base como /auth para todos os métodos desta classe
public class AuthenticationController {

    // Injeta o gerenciador de autenticação para validar credenciais
    @Autowired
    private AuthenticationManager authenticationManager;

    // Injeta o repositório de usuários para manipulação de dados do usuário
    @Autowired
    private UsuarioRepository repository;

    // Injeta o serviço de token para geração de tokens de autenticação
    @Autowired
    TokenService tokenService;

    // Método para autenticação de usuários no endpoint /auth/login
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        // Cria um token de autenticação a partir das credenciais fornecidas
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(),data.senha());

        // Autentica as credenciais usando o AuthenticationManager
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // Gera um token JWT para o usuário autenticado
        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        // Retorna uma resposta com o token gerado
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    // Método para registro de novos usuários no endpoint /auth/register
    @PostMapping("/register")
    public ResponseEntity registrar(@RequestBody @Valid RegisterDTO data){
        // Verifica se o e-mail já está registrado
        if (this.repository.findByEmail(data.email()) != null)
            return ResponseEntity.badRequest().build(); // Retorna erro se o e-mail já existir

        // Criptografa a senha antes de salvar
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());

        // Cria uma nova instância de usuário com os dados fornecidos
        Usuario usuario = new Usuario(data.email(), encryptedPassword, data.role());

        // Salva o novo usuário no banco de dados
        this.repository.save(usuario);

        // Retorna uma resposta de sucesso
        return ResponseEntity.ok().build();
    }
}
