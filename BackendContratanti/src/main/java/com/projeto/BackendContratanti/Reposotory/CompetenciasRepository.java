package com.projeto.BackendContratanti.Reposotory;

import com.projeto.BackendContratanti.Model.Competencias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CompetenciasRepository extends JpaRepository<Competencias, BigInteger> {
    List<Competencias> findByEmail(String email);
    List<Competencias> findByNome(String nome);
}
