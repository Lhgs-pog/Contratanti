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
    public ResponseEntity<Competencias> getById(@PathVariable Integer id){
        return services.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Void> saveCompetencia(@Valid @RequestBody CompetenciasRequestDTO competencias){
        services.saveCompetencias(competencias);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Competencias> updateCompetencia(@Valid @RequestBody Competencias competencias){
        return ResponseEntity.ok(services.updateCompetencias(competencias));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(){
        services.deleteAllCompetencias();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cid}")
    public ResponseEntity<Void> deleteById(Integer id){
        services.deleteCompetenciasById(id);
        return ResponseEntity.noContent().build();
    }
}
