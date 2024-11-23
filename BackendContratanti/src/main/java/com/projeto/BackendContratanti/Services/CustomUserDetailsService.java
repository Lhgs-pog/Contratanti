package com.projeto.BackendContratanti.Services;

import com.projeto.BackendContratanti.Model.Usuario;
import com.projeto.BackendContratanti.Reposotory.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Seu repositório de usuários

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tente encontrar o usuário pelo email ou outra chave
        Usuario usuario = (Usuario) usuarioRepository.findByEmail(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + username);
        }

        return new org.springframework.security.core.userdetails.User(usuario.getEmail(), usuario.getPassword(), new ArrayList<>());
    }
}
