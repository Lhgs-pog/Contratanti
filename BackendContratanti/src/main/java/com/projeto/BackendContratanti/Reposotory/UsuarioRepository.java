package com.projeto.BackendContratanti.Reposotory;

import com.projeto.BackendContratanti.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, BigInteger> {

    UserDetails findByEmail(String login);
}
