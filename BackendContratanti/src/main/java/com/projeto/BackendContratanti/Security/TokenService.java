package com.projeto.BackendContratanti.Security;

// Importa classes necessárias para manipulação de tokens JWT e do usuário
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.projeto.BackendContratanti.Model.Empresa;
import com.projeto.BackendContratanti.Model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service // Define esta classe como um serviço gerenciado pelo Spring
public class TokenService {

    // Injeta o valor do segredo para assinar os tokens a partir das configurações da aplicação
    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Gera um token JWT para um usuário.
     *
     * @param usuario Objeto do tipo Usuario.
     * @return Token JWT gerado.
     */
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(usuario.getEmail())
                    .withClaim("tipo", "USUARIO")
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            logger.error("Erro ao gerar token: ", exception);
            throw new RuntimeException("Erro ao gerar token: ", exception);
        }
    }

    /**
     * Gera um token JWT para uma empresa.
     *
     * @param empresa Objeto do tipo Empresa.
     * @return Token JWT gerado.
     */
    public String generateTokenForEmpresa(Empresa empresa) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret); // Algoritmo de assinatura HMAC com o segredo
            return JWT.create()
                    .withIssuer("auth-api") // Define o emissor
                    .withSubject(empresa.getEmail()) // Assunto do token: email da empresa
                    .withClaim("tipo", "EMPRESA") // Adiciona a claim tipo como "EMPRESA"
                    .withExpiresAt(genExpirationDate()) // Define a data de expiração
                    .sign(algorithm); // Assina o token
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token: ", exception); // Trata erros de criação
        }
    }

    /**
     * Valida um token JWT e retorna o email associado se válido.
     *
     * @param token Token JWT a ser validado.
     * @return Email associado ao token se válido, ou string vazia caso inválido.
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret); // Algoritmo de validação
            return JWT.require(algorithm)
                    .withIssuer("auth-api") // Verifica o emissor
                    .build()
                    .verify(token) // Valida o token
                    .getSubject(); // Retorna o assunto (email)
        } catch (JWTVerificationException exception) {
            return ""; // Retorna vazio se o token for inválido
        }
    }

    /**
     * Obtém o tipo de usuário a partir de um token JWT.
     *
     * @param token Token JWT.
     * @return Tipo do usuário (ex.: "USUARIO" ou "EMPRESA").
     */
    public String getTipoUsuario(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret); // Algoritmo de validação
            return JWT.require(algorithm)
                    .withIssuer("auth-api") // Verifica o emissor
                    .build()
                    .verify(token) // Valida o token
                    .getClaim("tipo") // Obtém a claim "tipo"
                    .asString(); // Converte para string
        } catch (JWTVerificationException exception) {
            return ""; // Retorna vazio se o token for inválido
        }
    }

    /**
     * Gera a data de expiração para o token JWT.
     *
     * @return Data de expiração como um objeto Instant.
     */
    private Instant genExpirationDate() {
        return LocalDateTime.now()
                .plusHours(2) // Expira em 2 horas
                .toInstant(ZoneOffset.of("-03:00")); // Usa o fuso horário UTC-3
    }
}