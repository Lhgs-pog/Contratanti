package com.projeto.BackendContratanti.Security;

// Importação das classes necessárias para configuração de segurança do Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Indica que esta classe é uma configuração do Spring
@EnableWebSecurity // Ativa a configuração de segurança web para a aplicação
public class SecurityConfiguration {

    @Autowired
    SecurityFilter securityFilter; // Injeção do filtro personalizado de segurança

    // Bean que define a configuração de segurança para as requisições HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csfr -> csfr.disable()) // Desabilita a proteção CSRF (não necessária em APIs sem estado)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Define a política de sessões como stateless (sem estado)
                .authorizeHttpRequests(authorize -> authorize // Configuração de autorização de requisições
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll() // Permite o acesso à rota de login sem autenticação
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll() // Permite o acesso à rota de registro sem autenticação
                        .requestMatchers(HttpMethod.POST, "/usuario").hasRole("ADMIN") // Apenas usuários com a role 'ADMIN' podem acessar esta rota
                        .anyRequest().authenticated() // Exige autenticação para qualquer outra requisição
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) // Adiciona o filtro de segurança antes do filtro de autenticação padrão
                .build(); // Constrói a configuração de segurança
    }

    // Bean que cria o AuthenticationManager, necessário para autenticação
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // Retorna o AuthenticationManager configurado
    }

    // Bean que cria o PasswordEncoder para codificar senhas com BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Retorna uma instância do BCryptPasswordEncoder
    }
}
