package com.projeto.BackendContratanti.Security;

// Importa classes necessárias para o filtro de autenticação
import com.projeto.BackendContratanti.Reposotory.EmpresaRepository;
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

@Component // Define esta classe como um componente gerenciado pelo Spring
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService; // Serviço de manipulação de tokens JWT

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositório de usuários

    @Autowired
    private EmpresaRepository empresaRepository; // Repositório de empresas

    /**
     * Intercepta e processa todas as requisições para verificar o token de autenticação.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recoveryToken(request);
        if (token != null) {
            String email = tokenService.validateToken(token);
            String tipoUsuario = tokenService.getTipoUsuario(token);

            if ("USUARIO".equals(tipoUsuario)) {
                UserDetails user = usuarioRepository.findByEmail(email);
                if (user == null) {
                    // Caso o usuário não seja encontrado
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não encontrado.");
                    return;
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if ("EMPRESA".equals(tipoUsuario)) {
                UserDetails empresa = empresaRepository.findByEmail(email);
                if (empresa == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Empresa não encontrada.");
                    return;
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(empresa, null, empresa.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Recupera o token JWT do cabeçalho de autorização da requisição.
     *
     * @param request Objeto HttpServletRequest.
     * @return Token JWT sem o prefixo "Bearer ", ou null se não existir.
     */
    private String recoveryToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization"); // Cabeçalho Authorization
        if (authHeader == null) return null; // Retorna null se o cabeçalho não estiver presente
        return authHeader.replace("Bearer ", ""); // Remove o prefixo "Bearer " e retorna o token puro
    }
}
