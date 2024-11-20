package com.projeto.BackendContratanti.Reposotory;

import com.projeto.BackendContratanti.Model.Competencias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetenciasRepository extends JpaRepository<Competencias, Integer> {
}
