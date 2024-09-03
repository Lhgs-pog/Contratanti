package com.projeto.BackendContratanti.Services;

import com.projeto.BackendContratanti.Model.Empresa;
import com.projeto.BackendContratanti.Reposotory.EmpresaRepository;
import com.projeto.BackendContratanti.Dto.EmpresaRequestDTO;
import com.projeto.BackendContratanti.Dto.EmpresaResponseDTO;
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
    public boolean empresaValidarCnpj(String cnpj){

        cnpj = cnpj.replace("\\.","");
        cnpj = cnpj.replace("\\/","");
        cnpj = cnpj.replace("\\-","");

        if (cnpj.equals("00000000000000") || cnpj.equals("11111111111111")
                || cnpj.equals("22222222222222") || cnpj.equals("33333333333333")
                || cnpj.equals("44444444444444") || cnpj.equals("55555555555555")
                || cnpj.equals("66666666666666") || cnpj.equals("77777777777777")
                || cnpj.equals("88888888888888") || cnpj.equals("99999999999999") || (cnpj.length() != 14)){
            return (false);
        }
        char dig13, dig14;
        int sm, i, r, num, peso;

            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 11; i >= 0; i--) {
                // converte o i-ésimo caractere do CNPJ em um número: // por
                // exemplo, transforma o caractere '0' no inteiro 0 // (48 eh a
                // posição de '0' na tabela ASCII)
                num = (int) (cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig13 = '0';
            else
                dig13 = (char) ((11 - r) + 48);

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 12; i >= 0; i--) {
                num = (int) (cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }
            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig14 = '0';
            else
                dig14 = (char) ((11 - r) + 48);
            // Verifica se os dígitos calculados conferem com os dígitos
            // informados.
            if ((dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13)))
                return (true);
            else
                return (false);
    }

    public boolean validarEmail(String email){
        Pattern pattern = Pattern.compile(
                "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
        Matcher matcher;
        return pattern.matcher(email).find();
    }

    public boolean validarTelefone(String telefone){
        telefone = telefone.replaceAll("\\D", "");

        //verifica se tem a qtde de numeros correta
        if (!(telefone.length() >= 10 && telefone.length() <= 11)) {
            return false;
        }
        //Se tiver 11 caracteres, verificar se começa com 9 o celular
        if (telefone.length() == 11 && Integer.parseInt(telefone.substring(2, 3)) != 9) {
            return false;
        }
        //verifica se o numero foi digitado com todos os dígitos iguais
        Pattern p = Pattern.compile(telefone.charAt(0) + "{" + telefone.length() + "}");
        Matcher m = p.matcher(telefone);
        if (m.find()) {
            return false;
        }
        //DDDs validos
        Integer[] codigosDDD = {
                11, 12, 13, 14, 15, 16, 17, 18, 19,
                21, 22, 24, 27, 28, 31, 32, 33, 34,
                35, 37, 38, 41, 42, 43, 44, 45, 46,
                47, 48, 49, 51, 53, 54, 55, 61, 62,
                64, 63, 65, 66, 67, 68, 69, 71, 73,
                74, 75, 77, 79, 81, 82, 83, 84, 85,
                86, 87, 88, 89, 91, 92, 93, 94, 95,
                96, 97, 98, 99};
        //verifica se o DDD é valido
        if (!java.util.Arrays.asList(codigosDDD).contains(Integer.parseInt(telefone.substring(0, 2)))) {
            return false;
        }
        //Se o número só tiver dez digitos não é um celular e por isso o número logo após o DDD deve ser 2, 3, 4, 5 ou 7
        Integer[] prefixos = {2, 3, 4, 5, 7};

        return telefone.length() != 10 || java.util.Arrays.asList(prefixos).contains(Integer.parseInt(telefone.substring(2, 3)));
    }
}
