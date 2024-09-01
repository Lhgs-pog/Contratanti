package com.projeto.BackendContratanti.Controller;

import com.projeto.BackendContratanti.Model.Empresa.Empresa;
import com.projeto.BackendContratanti.Model.Empresa.EmpresaRepository;
import com.projeto.BackendContratanti.Model.Empresa.EmpresaRequestDTO;
import com.projeto.BackendContratanti.Model.Empresa.EmpresaResponseDTO;
import com.projeto.BackendContratanti.Model.Usuario.Usuario;
import com.projeto.BackendContratanti.Services.EmpresaServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

//indica para o spring que está é uma classe controller
@RestController
//manda todos os requests de empresa para essa classe
@RequestMapping("empresa")
public class EmpresaController {

    //diz para o spring que ele tem que injetar essas dependencias
    @Autowired
    private EmpresaServices services;

    //Indica os dominios e os headers permitido, está setado como todos para evitar conflito, mas dessa forma não é seguro
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    //indica que esté funcão é responsável pelos metodos de envio de informações da ape(post)
    @PostMapping
    public void saveEmpresa(@RequestBody EmpresaRequestDTO data){
        services.saveEmpresa(data);
    }
    //Indica os dominios e os headers permitido, está setado como todos para evitar conflito, mas dessa forma não é seguro
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    //indica que está funcão é responsável por trazer as informções pela api(get)
    @GetMapping
    //traz todos as empresas do banco
    public List<EmpresaResponseDTO> getAll(){
        return services.empresaGetAAll();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/empresas/{eid}")
    //@ResponseBody
    public Optional<Empresa> getbyid(@PathVariable("eid") BigInteger eid , @RequestBody Empresa empresa){
        return services.empresaFindById(empresa ,eid);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/empresas/delete/{eid}")
    public void deletePorId (@PathVariable("eid") BigInteger eid,@RequestBody BigInteger idempresa){
        services.empresaDeleteById(eid);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/empresas/delete")
    public void deleteAll (){
        services.empresaDeleteAll();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping(path = "/empresas/update", consumes = {"application/json"})
    public Empresa updataAll(@RequestBody Empresa emp){
        return services.empresaUpdate(emp);
    }

}

