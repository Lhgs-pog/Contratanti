package com.projeto.BackendContratanti.Controller;

import com.projeto.BackendContratanti.Model.Empresa;
import com.projeto.BackendContratanti.Dto.EmpresaRequestDTO;
import com.projeto.BackendContratanti.Dto.EmpresaResponseDTO;
import com.projeto.BackendContratanti.Services.EmpresaServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8080") // Configuração global de CORS para a classe
@RestController
@RequestMapping("/empresa") // Define o prefixo dos endpoints
public class EmpresaController {

    private final EmpresaServices services;

    public EmpresaController(EmpresaServices services) {
        this.services = services;
    }

    /**
     * Salva uma nova empresa com base nos dados fornecidos.
     *
     * @param data Dados da empresa enviados no corpo da requisição.
     * @return Resposta HTTP 201 (Created) se bem-sucedido.
     */
    @PostMapping
    public ResponseEntity<Void> saveEmpresa(@Valid @RequestBody EmpresaRequestDTO data) {
        services.saveEmpresa(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retorna uma lista de todas as empresas cadastradas.
     *
     * @return Lista de empresas no formato de resposta DTO.
     */
    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> getAll() {
        return ResponseEntity.ok(services.findAllEmpresas());
    }

    /**
     * Busca uma empresa pelo ID.
     *
     * @param eid ID da empresa fornecido no caminho da URL.
     * @return Empresa encontrada ou resposta HTTP 404 se não encontrada.
     */
    @GetMapping("/{eid}")
    public ResponseEntity<Empresa> getById(@PathVariable BigInteger eid) {
        return services.empresaFindById(eid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Exclui uma empresa pelo ID.
     *
     * @param eid ID da empresa fornecido no caminho da URL.
     * @return Resposta HTTP 204 (No Content) se bem-sucedido.
     */
    @DeleteMapping("/{eid}")
    public ResponseEntity<Void> deleteById(@PathVariable BigInteger eid) {
        services.deleteEmpresaById(eid);
        return ResponseEntity.noContent().build();
    }

    /**
     * Exclui todas as empresas cadastradas.
     *
     * @return Resposta HTTP 204 (No Content) se bem-sucedido.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        services.deleteAllEmpresas();
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza os dados de uma empresa.
     *
     * @param emp Dados da empresa a serem atualizados.
     * @return Empresa atualizada.
     */
    @PutMapping
    public ResponseEntity<Empresa> update(@Valid @RequestBody Empresa emp) {
        return ResponseEntity.ok(services.updateEmpresa(emp));
    }
}
