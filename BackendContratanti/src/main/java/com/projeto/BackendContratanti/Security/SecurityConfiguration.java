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
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration // Indica que esta classe é uma configuração do Spring
@EnableWebSecurity // Ativa a configuração de segurança web para a aplicação
public class SecurityConfiguration {

    // Centralização de roles
    private static final String ROLE_ADMIN = "ADMIN";

    @Autowired
    SecurityFilter securityFilter; // Injeção do filtro personalizado de segurança

    // Bean que define a configuração de segurança para as requisições HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csfr -> csfr.disable()) // Desabilita a proteção CSRF (não necessária em APIs sem estado)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000")); // Domínio correto
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Define a política de sessões como stateless (sem estado)
                .authorizeHttpRequests(authorize -> authorize // Configuração de autorização de requisições

                        // Configurações de empresa
                        .requestMatchers(HttpMethod.DELETE, "/empresa/deleteall").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/empresa/updateall").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/empresa/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/empresa/{eid}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/empresa/delete/{eid}").authenticated()

                        // Configurações de usuário
                        .requestMatchers(HttpMethod.GET, "/usuario/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/usuario/deleteall").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/usuario/delete/{uid}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/usuario/update").authenticated()

                        // Configurações de autenticação
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()

                        // Qualquer outra requisição exige autenticação
                        .anyRequest().authenticated()
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
