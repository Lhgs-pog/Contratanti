package com.projeto.BackendContratanti.Controller;

// Importa classes e pacotes necessários para DTOs, modelo de usuário, repositório, segurança, etc.
import com.projeto.BackendContratanti.Dto.AuthenticationDTO;
import com.projeto.BackendContratanti.Dto.LoginResponseDTO;
import com.projeto.BackendContratanti.Model.Empresa;
import com.projeto.BackendContratanti.Model.Usuario;
import com.projeto.BackendContratanti.Reposotory.UsuarioRepository;
import com.projeto.BackendContratanti.Security.CustomAuthenticationManager;
import com.projeto.BackendContratanti.Security.TokenService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Importa o logger para registrar mensagens de erro
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController // Indica que essa classe é um controlador REST
@RequestMapping("/auth") // Define o endpoint base como /auth para todos os métodos desta classe
public class AuthenticationController {

    // Criação do logger
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

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
    @PostMapping("/usuario/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        try {
            // Criação do token de autenticação usando as credenciais fornecidas
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());

            // Tenta autenticar as credenciais fornecidas
            var auth = this.authenticationManager.authenticate(usernamePassword);

            // Verifica se o principal é do tipo esperado (Usuario)
            if (auth.getPrincipal() instanceof Usuario usuario) {
                // Após autenticação, gera o token JWT
                var token = tokenService.generateToken(usuario);
                // Retorna a resposta com o token gerado
                return ResponseEntity.ok(new LoginResponseDTO(token));
            } else {
                // Caso o principal não seja um Usuario, retorna erro de autenticação
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro na autenticação.");
            }
        } catch (BadCredentialsException e) {
            // Loga o erro de credenciais inválidas
            logger.error("Credenciais inválidas para o usuário: " + data.email(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        } catch (Exception e) {
            // Loga qualquer outro erro
            logger.error("Erro ao processar a autenticação para o usuário: " + data.email(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a autenticação.");
        }
    }


    // Método para autenticação de empresas no endpoint /auth/empresa/login
    @PostMapping("/empresa/login")
    public ResponseEntity emplogin(@RequestBody @Valid AuthenticationDTO data){
        try {
            // Criação do token de autenticação usando as credenciais fornecidas
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(),data.senha());

            // Tenta autenticar as credenciais fornecidas
            var auth = this.authenticationManager.authenticate(usernamePassword);

            // Após autenticação, gera o token JWT para a empresa
            var token = tokenService.generateTokenForEmpresa((Empresa) auth.getPrincipal());

            // Retorna a resposta com o token gerado
            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (BadCredentialsException e) {
            // Loga o erro de credenciais inválidas para a empresa
            logger.error("Credenciais inválidas para a empresa: " + data.email(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        } catch (Exception e) {
            // Loga qualquer outro erro para a empresa
            logger.error("Erro ao processar a autenticação para a empresa: " + data.email(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a autenticação.");
        }
    }

}
