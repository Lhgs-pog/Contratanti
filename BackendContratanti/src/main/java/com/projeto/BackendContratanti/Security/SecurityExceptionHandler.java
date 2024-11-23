package com.projeto.BackendContratanti.Security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        // Captura a mensagem de erro detalhada da exceção
        String errorMessage = "Autenticação necessária.";

        // Se a exceção contiver informações adicionais, você pode usar isso
        if (ex.getMessage() != null) {
            errorMessage = ex.getMessage();
        }

        // Retorna uma resposta mais detalhada com o erro
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Erro de autenticação: " + errorMessage);
    }
}
