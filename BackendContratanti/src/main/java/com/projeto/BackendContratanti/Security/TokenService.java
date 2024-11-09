package com.projeto.BackendContratanti.Security;

// Importa classes necessárias para manipulação de tokens JWT e do usuário
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.projeto.BackendContratanti.Model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service // Define essa classe como um serviço, tornando-a gerenciada pelo Spring
public class TokenService {

    // Injeta o valor secreto do token a partir das configurações do aplicativo
    @Value("${api.security.token.secret}")
    private String secret;

    // Método para gerar um token JWT para um usuário autenticado
    public String generateToken(Usuario usuario){
        try {
            // Define o algoritmo de criptografia HMAC usando o segredo fornecido
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Cria o token com informações do emissor, assunto e data de expiração
            String token = JWT.create()
                    .withIssuer("auth-api") // Define o emissor do token
                    .withSubject(usuario.getEmail()) // Define o assunto como o email do usuário
                    .withExpiresAt(genExpirationDate()) // Define a data de expiração
                    .sign(algorithm); // Assina o token usando o algoritmo

            // Retorna o token gerado
            return token;
        } catch (JWTCreationException exception) {
            // Lança uma exceção caso ocorra erro durante a criação do token
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    // Método para validar um token JWT e extrair o assunto (email do usuário) se válido
    public String validateToken(String token) {
        try {
            // Define o algoritmo para validação usando o segredo
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Valida o token e obtém o assunto (email do usuário) se o token for válido
            return JWT.require(algorithm)
                    .withIssuer("auth-api") // Verifica o emissor do token
                    .build()
                    .verify(token) // Verifica o token
                    .getSubject(); // Extrai o assunto (email do usuário)
        } catch (JWTVerificationException exception) {
            // Retorna uma string vazia caso o token seja inválido
            return "";
        }
    }

    // Método auxiliar para gerar a data de expiração do token (2 horas a partir do momento atual)
    private Instant genExpirationDate() {
        return LocalDateTime.now()
                .plusHours(2) // Define expiração para 2 horas no futuro
                .toInstant(ZoneOffset.of("-03:00")); // Define o fuso horário como UTC-3
    }
}
