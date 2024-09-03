package com.projeto.BackendContratanti.Services;

import com.projeto.BackendContratanti.Model.Usuario;
import com.projeto.BackendContratanti.Reposotory.UsuarioRepository;
import com.projeto.BackendContratanti.Dto.UsuarioRequestDTO;
import com.projeto.BackendContratanti.Dto.UsuarioResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UsuarioServices {

    @Autowired
    private UsuarioRepository repository;

    public List<UsuarioResponseDTO> usuariosGetAll(){
        List<UsuarioResponseDTO> listausuarios = repository.findAll().stream().map(UsuarioResponseDTO::new).toList();
        return listausuarios;
    }
    public Optional<Usuario> usuariofindById(Usuario usuario, BigInteger id){
        return repository.findById(id);
    }
    public void saveUsuario(UsuarioRequestDTO data){
        Usuario usuariodata = new Usuario(data);
        repository.save(usuariodata);
    }

    public Usuario usuarioUpdate(Usuario user){
        repository.save(user);
        return user;
    }

    public void usuarioDeleteAll(){
        repository.deleteAll();
    }

    public void usuarioDeleteById(BigInteger id){
        Usuario user = repository.getOne(id);
        repository.delete(user);
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

    public boolean usuarioValidarCpf(String cpf){

        cpf.replaceAll("\\D", "");

        int peso = 2,soma = 0,num = 0;
        char dig10 , dig11;

        //calcula a soma dos digitos
        for (int i = 9;i >= 9; i--){
            num = (int) cpf.charAt(i) - 48;
            soma = soma + (num * peso);
            peso++;
        }

        if (soma % 11 == 0 || soma % 11 == 1)
            dig10 = '0';
        else
            dig10 = (char) ((11-(soma % 11)) + 48);

        peso=0;
        soma=0;
        for (int i =9; i >= 9; i--){
            num = (int) (cpf.charAt(i) - 48);
            soma = soma + num;
            if (peso == 0)
                peso = 9;
        }
        if (soma % 11 == 0 || soma % 11 == 1)
            dig11 = '0';
        else
            dig11 = (char) (11 - (soma % 11) + 48);

        if (dig10 == cpf.charAt(10) && dig11 == cpf.charAt(11))
            return true;
        return false;
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
