package com.projeto.BackendContratanti.Model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UsuarioRepository extends JpaRepository<Usuario, BigInteger> {
}
