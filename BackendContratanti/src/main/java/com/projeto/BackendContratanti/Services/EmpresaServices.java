package com.projeto.BackendContratanti.Services;

import com.projeto.BackendContratanti.Model.Empresa.Empresa;
import com.projeto.BackendContratanti.Model.Empresa.EmpresaRepository;
import com.projeto.BackendContratanti.Model.Empresa.EmpresaRequestDTO;
import com.projeto.BackendContratanti.Model.Empresa.EmpresaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmpresaServices {

    @Autowired
    private EmpresaRepository repository;

    public List<EmpresaResponseDTO> empresaGetAAll(){
        List<EmpresaResponseDTO> listaempresa = repository.findAll().stream().map(EmpresaResponseDTO::new).toList();
        return listaempresa;
    }

    public Optional<Empresa> empresaFindById(Empresa empresa, BigInteger id){
        return repository.findById(id);
    }

    public void saveEmpresa(EmpresaRequestDTO data){
        Empresa empresa = new Empresa(data);
        repository.save(empresa);
    }

    public Empresa empresaUpdate(Empresa emp){
        repository.save(emp);
        return emp;
    }

    public void empresaDeleteById(BigInteger id){
        Empresa emp = repository.getOne(id);
        repository.delete(emp);
    }

    public void empresaDeleteAll(){
        repository.deleteAll();
    }
    //regex para permitir a entrada de somente letras e numeros, prevenindo sqlinjection e outros tipos de ataque
    public String validarString(String linha){
        Pattern pattern = Pattern.compile("^[A-Za-z1-9]$");
        Matcher matcher = pattern.matcher(linha);
        boolean matchfound = matcher.find();
        if (matchfound)
            return linha;
        return "";
    }
}
