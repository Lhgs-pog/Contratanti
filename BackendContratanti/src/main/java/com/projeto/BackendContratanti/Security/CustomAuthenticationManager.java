package com.projeto.BackendContratanti.Security;

import com.projeto.BackendContratanti.Model.Usuario;
import com.projeto.BackendContratanti.Reposotory.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomAuthenticationManager implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomAuthenticationManager(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            // Procura o usuário pelo email
            Usuario usuario = (Usuario) usuarioRepository.findByEmail(username);

            // Se o usuário não for encontrado, lança a exceção
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuário não encontrado");
            }

            // Retorna o UserDetails com o email e senha do usuário, e uma lista de autoridades (vazia neste exemplo)
            return new org.springframework.security.core.userdetails.User(usuario.getEmail(), usuario.getSenha(),
                    new ArrayList<>()); // Aqui você pode incluir as roles do usuário, caso necessário
        }
    }

