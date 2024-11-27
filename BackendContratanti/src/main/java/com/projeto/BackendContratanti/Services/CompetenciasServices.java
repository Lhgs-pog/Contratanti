package com.projeto.BackendContratanti.Services;


import com.projeto.BackendContratanti.Dto.CompetenciasRequestDTO;
import com.projeto.BackendContratanti.Dto.CompetenciasResponseDTO;
import com.projeto.BackendContratanti.Model.Competencias;
import com.projeto.BackendContratanti.Reposotory.CompetenciasRepository;
import com.projeto.BackendContratanti.Security.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.Normalizer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Competencias updateCompetencias(BigInteger cid, Competencias compatt){
        Competencias comp = repository.findById(cid)
                .orElseThrow(() -> new ResourceNotFoundException("Competencia não encontrada"));

        comp.setNome(compatt.getNome());
        comp.setEmail(compatt.getEmail());

        return repository.save(comp);
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

    public class CompetenciaUtils {

        public static List<String> padronizarCompetencias(List<String> competencias) {
            if (competencias == null || competencias.isEmpty()) {
                return List.of();
            }

            return competencias.stream()
                    .map(CompetenciaUtils::removerAcentos)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }

        private static String removerAcentos(String texto) {
            if (texto == null) {
                return null;
            }

            // Normaliza o texto para decompor caracteres com acentos
            String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);

            // Remove os caracteres que não sejam letras ou números (inclui remoção dos diacríticos)
            return textoNormalizado.replaceAll("\\p{M}", "");
        }
    }

}
