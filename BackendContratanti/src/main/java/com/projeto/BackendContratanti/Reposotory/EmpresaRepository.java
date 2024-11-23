package com.projeto.BackendContratanti.Reposotory;

import com.projeto.BackendContratanti.Model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, BigInteger> {
    UserDetails findByEmail(String email);
    Optional<Empresa> findOptionalByEmail(String email);
    Optional<Empresa> findOptionalByCnpj(String cnpj);
}
