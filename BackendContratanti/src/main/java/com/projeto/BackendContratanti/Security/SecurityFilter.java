package com.projeto.BackendContratanti.Security;

// Importa classes necessárias para o filtro de autenticação
import com.projeto.BackendContratanti.Reposotory.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Define essa classe como um componente gerenciado pelo Spring
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService; // Injeção de dependência do serviço de tokens

    @Autowired
    UsuarioRepository usuarioRepository; // Injeção de dependência do repositório de usuários

    // Método que intercepta e trata todas as requisições para verificar o token de autenticação
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Ignorar autenticação para caminhos públicos
        if (requestPath.equals("/empresa")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Recupera o token da requisição
        var token = this.recoveryToken(request);
        // Ignorar autenticação para caminhos públicos
        if (token != null) { // Se o token não for nulo, faz a validação
            var login = tokenService.validateToken(token); // Valida o token e recupera o login do usuário
            UserDetails user = usuarioRepository.findByEmail(login); // Busca o usuário no repositório pelo email

            // Cria uma autenticação para o usuário encontrado e define as autoridades
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication); // Define a autenticação no contexto de segurança
        }
        filterChain.doFilter(request, response); // Continua o fluxo do filtro
    }

    // Método auxiliar para extrair o token JWT do cabeçalho da requisição
    public String recoveryToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization"); // Recupera o cabeçalho de autorização
        if (authHeader == null) return null; // Retorna null se o cabeçalho não estiver presente
        return authHeader.replace("Bearer ", ""); // Remove o prefixo "Bearer " e retorna o token puro
    }
}
