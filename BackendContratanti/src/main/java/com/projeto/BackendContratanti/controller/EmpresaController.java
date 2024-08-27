package com.projeto.BackendContratanti.Controller;

import com.projeto.BackendContratanti.Model.Empresa.Empresa;
import com.projeto.BackendContratanti.Model.Empresa.EmpresaRepository;
import com.projeto.BackendContratanti.Model.Empresa.EmpresaRequestDTO;
import com.projeto.BackendContratanti.Model.Empresa.EmpresaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//indica para o spring que está é uma classe controller
@RestController
//manda todos os requests de empresa para essa classe
@RequestMapping("empresa")
public class EmpresaController {

    //diz para o spring que ele tem que injetar essas dependencias
    @Autowired
    private EmpresaRepository repository;

    //Indica os dominios e os headers permitido, está setado como todos para evitar conflito, mas dessa forma não é seguro
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    //indica que esté funcão é responsável pelos metodos de envio de informações da ape(post)
    @PostMapping
    public void saveEmpresa(@RequestBody EmpresaRequestDTO data){
        //converte o objeto dto pra empresa
        Empresa empresadata = new Empresa(data);
        repository.save(empresadata);
    }
    //Indica os dominios e os headers permitido, está setado como todos para evitar conflito, mas dessa forma não é seguro
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    //indica que está funcão é responsável por trazer as informções pela api(get)
    @GetMapping
    //traz todos as empresas do banco
    public List<EmpresaResponseDTO> getAll(){
        List<EmpresaResponseDTO> listaempresa = repository.findAll().stream().map(EmpresaResponseDTO::new).toList();
        return listaempresa;
    }
}

