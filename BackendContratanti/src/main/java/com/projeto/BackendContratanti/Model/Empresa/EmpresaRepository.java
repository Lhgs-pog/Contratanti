package com.projeto.BackendContratanti.Model.Empresa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface EmpresaRepository extends JpaRepository<Empresa, BigInteger> {
}
