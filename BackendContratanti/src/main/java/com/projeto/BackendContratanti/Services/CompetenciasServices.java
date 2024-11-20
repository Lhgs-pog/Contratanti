package com.projeto.BackendContratanti.Services;


import com.projeto.BackendContratanti.Dto.CompetenciasRequestDTO;
import com.projeto.BackendContratanti.Dto.CompetenciasResponseDTO;
import com.projeto.BackendContratanti.Model.Competencias;
import com.projeto.BackendContratanti.Reposotory.CompetenciasRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class CompetenciasServices {

    @Autowired
    private CompetenciasRepository repository;

    @Autowired
    private MailServices mailServices;

    public CompetenciasServices(CompetenciasRepository competenciaRepository) {
        this.repository = competenciaRepository;
    }

    public List<CompetenciasResponseDTO> findAllCompetencias(){
        return repository.findAll().stream()
                .map(CompetenciasResponseDTO::new)
                .toList();
    }

    public Optional<Competencias> findById(BigInteger id){
        return repository.findById(id);
    }

    public List<Competencias> findByEmail(String email){
        return repository.findByEmail(email);
    }

    public List<Competencias> findByNome(String nome){
        return repository.findByNome(nome);
    }

    @Transactional
    public void saveCompetencias(CompetenciasRequestDTO data){
        Competencias competencias = new Competencias(data);
        repository.save(competencias);
    }

    @Transactional
    public Competencias updateCompetencias(Competencias comp){
        Competencias updated = repository.save(comp);
        return updated;
    }

    @Transactional
    public void deleteCompetenciasById(BigInteger id){
        Competencias competencias = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competencia não encontrada com o id: "+id));
        repository.deleteById(id);
    }

    @Transactional
    public void deleteAllCompetencias(){
        repository.deleteAll();
    }
}
