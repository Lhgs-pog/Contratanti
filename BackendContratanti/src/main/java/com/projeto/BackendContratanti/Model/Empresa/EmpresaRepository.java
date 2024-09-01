package com.projeto.BackendContratanti.Model.Empresa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, BigInteger> {
}
