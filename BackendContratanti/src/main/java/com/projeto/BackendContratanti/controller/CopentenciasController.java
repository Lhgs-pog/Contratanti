package com.projeto.BackendContratanti.Controller;

import com.projeto.BackendContratanti.Dto.CompetenciasRequestDTO;
import com.projeto.BackendContratanti.Dto.CompetenciasResponseDTO;
import com.projeto.BackendContratanti.Model.Competencias;
import com.projeto.BackendContratanti.Services.CompetenciasServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/competencias")
public class CopentenciasController {

    @Autowired
    private CompetenciasServices services;

    public CopentenciasController(CompetenciasServices competenciasService){
        this.services=competenciasService;
    }

    @GetMapping
    public ResponseEntity<List<CompetenciasResponseDTO>> getAll(){
        return ResponseEntity.ok(services.findAllCompetencias());
    }

    @GetMapping("/{cid}")
    public ResponseEntity<Competencias> getById(@PathVariable BigInteger id){
        return services.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<Competencias>> getByEmail(@PathVariable String email){
        List<Competencias> competencias = services.findByEmail(email);
        if (competencias.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(competencias);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<Competencias>> findByNome(@PathVariable("nome") String nome){
        List<Competencias> competencias = services.findByNome(nome);
        if (competencias.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(competencias);
    }

    @PostMapping
    public ResponseEntity<Void> saveCompetencia(@Valid @RequestBody CompetenciasRequestDTO competencias){
        services.saveCompetencias(competencias);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{cid}")
    public ResponseEntity<Competencias> updateCompetencia(@PathVariable("cid") BigInteger cid,@Valid @RequestBody Competencias competencias){
        return ResponseEntity.ok(services.updateCompetencias(cid, competencias));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(){
        services.deleteAllCompetencias();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cid}")
    public ResponseEntity<Void> deleteById(@PathVariable("cid") BigInteger id){
        services.deleteCompetenciasById(id);
        return ResponseEntity.noContent().build();
    }
}
