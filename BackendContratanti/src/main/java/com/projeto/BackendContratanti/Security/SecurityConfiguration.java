package com.projeto.BackendContratanti.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    // Centralização de roles
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USUARIO = "USUARIO";

    @Autowired
    private SecurityFilter securityFilter; // Injeção do filtro personalizado de segurança

    @Autowired
    private CustomAuthenticationManager customAuthenticationManager; // Injetado o CustomAuthenticationManager

    // Configuração de segurança HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csfr -> csfr.disable()) // Desabilita CSRF para APIs sem estado
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*")); // Domínio correto para o frontend
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    return config;
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Política sem estado
                .authorizeHttpRequests(authorize -> authorize
                        // Autorização para endpoints de empresa (sem ROLE_EMPRESA, mas com permissão para alguns endpoints)
                        .requestMatchers(HttpMethod.DELETE, "/empresa/deleteall").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/empresa/updateall").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/empresa/{eid}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/empresa/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/empresa/delete/{eid}").permitAll()

                        // Autorização para endpoints de usuário
                        .requestMatchers(HttpMethod.GET, "/usuario/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/usuario/delete/{uid}").permitAll() // Deletar usuário autenticado
                        .requestMatchers(HttpMethod.PUT, "/usuario/update").authenticated() // Atualizar usuário autenticado
                        .requestMatchers(HttpMethod.PUT, "/usuario/{uid}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/usuario/deleteall").hasRole(ROLE_ADMIN) // Deletar todos usuários (admin)

                        // Autorização para endpoints de competência
                        .requestMatchers(HttpMethod.GET, "/competencias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/competencias/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/competencias/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/competencias/**").permitAll()

                        // Configurações de autenticação
                        .requestMatchers(HttpMethod.POST, "/auth/empresa/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/usuario/login").permitAll()

                        // Qualquer outra requisição exige autenticação
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) // Filtro de segurança
                .build();
    }

    // Bean do AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // Retorna o AuthenticationManager configurado
    }

    // Bean que cria o PasswordEncoder para codificar senhas com BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Instância do BCryptPasswordEncoder
    }
}
