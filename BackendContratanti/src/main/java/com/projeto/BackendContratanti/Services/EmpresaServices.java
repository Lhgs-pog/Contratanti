package com.projeto.BackendContratanti.Services;

import com.projeto.BackendContratanti.Model.Empresa;
import com.projeto.BackendContratanti.Reposotory.EmpresaRepository;
import com.projeto.BackendContratanti.Dto.EmpresaRequestDTO;
import com.projeto.BackendContratanti.Dto.EmpresaResponseDTO;
import com.projeto.BackendContratanti.Security.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class EmpresaServices {

    //Dependencias
    @Autowired
    private EmpresaRepository repository;

    @Autowired
    private MailServices mailServices;

    /*
    * Retorna todas as empresas usando o stream para passar po cada um dos elmentos da lista
    *  e o map para fazer uma modificação nesses elemento. No caso alí é para criar um novo record
    * com as informações buscadas.
    * */
    public List<EmpresaResponseDTO> findAllEmpresas() {
        return repository.findAll().stream()
                .map(EmpresaResponseDTO::new)
                .toList();
    }

    /*
    * Usa o Jpa para buscar um usuário com o id passado
    * */
    public Optional<Empresa> findEmpresaById(BigInteger id) {
        return repository.findById(id);
    }

    /*
    * Processo para salvar uma nova empresa, onde é avaliado:
    * Se o campo email está preenchido.
    * Se alguma empresa já usa este email
    * Se o cnpj, telefone e email estão em um formato válido
    * Criptografa a senha para salvar no banco
    * Envia um emial de boas vindas
    * */
    @Transactional
    public ResponseEntity<Empresa> saveEmpresa(EmpresaRequestDTO data) {
        //Verifica se o email existe
        if (data.email() == null || data.email().isBlank()) {
            throw new IllegalArgumentException("O email não pode ser nulo ou vazio");
        }

        //Verifica se o email já existe
        if (repository.findByEmail(data.email()) != null) {
            throw new IllegalArgumentException("O email já está registrado");
        }

        //Verifica se o telefone está em um formato válido
        if (!EmpresaServices.validarTelefone(data.telefone())) {
            throw new IllegalArgumentException("O telefone informado não é válido");
        }

        //Verifica se o email está em um formato válido
        if (!EmpresaServices.validarEmail(data.email())) {
            throw new IllegalArgumentException("O email informado não é válido");
        }

        //Verifica se o cnpj está em um formato válido
        if (!EmpresaServices.validarCnpj(data.cnpj())) {
            throw new IllegalArgumentException("O CNPJ informado não é válido");
        }

        //Após todas as verificações cria uma nova empresa com esses dados.
        Empresa empresa = new Empresa(data);

        //Criptografa a senha da empresa
        empresa.setSenha(new BCryptPasswordEncoder().encode(data.senha()));

        //Salva usando o jpa
        repository.save(empresa);

        //Envia um email e boas vindas
        mailServices.enviarEmailTexto(
                empresa.getEmail(),
                "Bem-vindo!",
                "Obrigado por criar uma conta no nosso site. Te manteremos atualizado quando uma empresa se interessar por você."
        );

        return ResponseEntity.ok(empresa);
    }




    /*
    * Atualiza uma empresa já cadatrada passando por verificações de:
    * Se a empresa realmente existe no banco de dados
    * Se o cnpj já está em uso
    * Se o telefone, email e cnpj está em formato válido
    * */
    @Transactional
    public Empresa updateEmpresa(BigInteger eid, Empresa empAtualizada) {
        // Busca a empresa pelo já existente com o ID passado
        Empresa empresaExistente = repository.findById(eid)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        // Verifica se o CNPJ já está em uso por outra empresa
        Optional<Empresa> empresaComMesmoCnpj = repository.findOptionalByCnpj(empAtualizada.getCnpj());
        if (empresaComMesmoCnpj.isPresent() && !empresaComMesmoCnpj.get().getId().equals(eid)) {
            throw new DataIntegrityViolationException("O CNPJ informado já está em uso por outra empresa.");
        }

        // Verifica se o e-mail já está em uso por outra empresa
        Optional<Empresa> empresaComMesmoEmail = repository.findOptionalByEmail(empAtualizada.getEmail());
        if (empresaComMesmoEmail.isPresent() && !empresaComMesmoEmail.get().getId().equals(eid)) {
            throw new DataIntegrityViolationException("O email informado já está em uso por outra empresa.");
        }

        // Validação de telefone
        if (empAtualizada.getTelefone() == null || !EmpresaServices.validarTelefone(empAtualizada.getTelefone())) {
            throw new IllegalArgumentException("O telefone informado não é válido.");
        }

        // Validação de formato de email
        if (empAtualizada.getEmail() == null || !EmpresaServices.validarEmail(empAtualizada.getEmail())) {
            throw new IllegalArgumentException("O email informado não é válido.");
        }

        // Validação de formato do CNPJ
        if (!EmpresaServices.validarCnpj(empAtualizada.getCnpj())) {
            throw new IllegalArgumentException("O CNPJ informado não é válido.");
        }

        // Criptografa a nova senha do usuário
        String encryptedPassword = new BCryptPasswordEncoder().encode(empAtualizada.getSenha());

        // Atualiza os dados da empresa
        empresaExistente.setNome(empAtualizada.getNome());
        empresaExistente.setEmail(empAtualizada.getEmail());
        empresaExistente.setCnpj(empAtualizada.getCnpj());
        empresaExistente.setTelefone(empAtualizada.getTelefone());
        empresaExistente.setSenha(encryptedPassword);

        // Salva a empresa atualizada no banco de dados
        Empresa empresaAtualizada = repository.save(empresaExistente);

        // Envia um e-mail confirmando a atualização dos dados
        mailServices.enviarEmailTexto(
                empresaAtualizada.getEmail(),
                "Confirmação de Atualização de Dados",
                "Prezado(a) " + empresaAtualizada.getNome() + ",\n\n" +
                        "Gostaríamos de informar que os dados da sua conta foram atualizados com sucesso. " +
                        "Se você não reconhece esta atualização, entre em contato conosco imediatamente.\n\n" +
                        "Atenciosamente,\nEquipe Contratanti."
        );

        return empresaAtualizada;
    }

    /*
    * Deleta um empresa no banco e dados com id informado
    * */
    public void deleteEmpresaById(BigInteger id) {
        //Verifica se a empresa existe
        Empresa emp = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada com ID: " + id));

        //Envio de email de notificação
        mailServices.enviarEmailTexto(
                emp.getEmail(),
                "Conta Excluída",
                "Prezado(a) " + emp.getNome() + ",\n\n" +
                        "Sua conta foi excluída com sucesso. Todos os dados associados foram removidos permanentemente.\n\n" +
                        "Agradecemos por fazer parte da nossa comunidade e esperamos vê-lo(a) novamente no futuro.\n\n" +
                        "Atenciosamente,\nEquipe Contratanti."
        );

        repository.delete(emp);
    }

    /*
    * Apaga todas as empresas salvas no banco e dados
    * */
    @Transactional
    public void deleteAllEmpresas() {
        repository.deleteAll();
    }

    /*
    * Formata textos para somente carcateres permitidos
    * */
    public String validarString(String linha) {
        if (Pattern.matches("^[A-Za-z0-9]+$", linha)) {
            return linha;
        }
        return "";
    }

    /*
    * Válida se um cnpj é válido
    * */
    public static boolean validarCnpj(String cnpj) {
        // Remove caracteres não numéricos
        cnpj = cnpj.replaceAll("\\D", "");

        // Verifica se o CNPJ tem exatamente 14 dígitos e não é uma sequência repetida
        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        char dig13, dig14;
        int soma, peso, num, resto;

        // Cálculo do 1º dígito verificador
        soma = 0;
        peso = 2;
        for (int i = 11; i >= 0; i--) {
            num = cnpj.charAt(i) - '0'; // Converte char para número
            soma += num * peso;
            peso = (peso == 9) ? 2 : peso + 1; // Reinicia peso para 2 após 9
        }

        resto = soma % 11;
        dig13 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

        // Cálculo do 2º dígito verificador
        soma = 0;
        peso = 2;
        for (int i = 12; i >= 0; i--) {
            num = cnpj.charAt(i) - '0';
            soma += num * peso;
            peso = (peso == 9) ? 2 : peso + 1;
        }

        resto = soma % 11;
        dig14 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

        // Verifica se os dígitos calculados correspondem aos dígitos fornecidos
        return dig13 == cnpj.charAt(12) && dig14 == cnpj.charAt(13);
    }


    /*
    * Verifica se um email é válido
    * */
    public static boolean validarEmail(String email) {
        return Pattern.matches(
                "^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$",
                email
        );
    }

    /*
    * Veridica se um telefone é válido
    * */
    public static boolean validarTelefone(String telefone) {
        //verifica se o telefone é nulo ou vazio
        if (telefone == null || telefone.isEmpty()) {
            return true; // Se telefone for opcional, considere válido
        }

        telefone = telefone.replaceAll("\\D", "");

        if (!(telefone.length() >= 10 && telefone.length() <= 11)) {
            return false;
        }

        if (telefone.length() == 11 && telefone.charAt(2) != '9') {
            return false;
        }

        if (telefone.matches("(\\d)\\1{" + telefone.length() + "}")) {
            return false;
        }

        // Verificar DDDs e prefixos
        return validarDDD(telefone) && validarPrefixo(telefone);
    }

    /*
    * Verifica se o ddd é um válido
    * */
    private static boolean validarDDD(String telefone) {
        Integer[] dddsValidos = {
                11, 12, 13, 14, 15, 16, 17, 18, 19,
                21, 22, 24, 27, 28, 31, 32, 33, 34,
                35, 37, 38, 41, 42, 43, 44, 45, 46,
                47, 48, 49, 51, 53, 54, 55, 61, 62,
                64, 63, 65, 66, 67, 68, 69, 71, 73,
                74, 75, 77, 79, 81, 82, 83, 84, 85,
                86, 87, 88, 89, 91, 92, 93, 94, 95,
                96, 97, 98, 99
        };
        int ddd = Integer.parseInt(telefone.substring(0, 2));
        return List.of(dddsValidos).contains(ddd);
    }

    /*
    * Válida o prefixo do telefone
    * */
    private static boolean validarPrefixo(String telefone) {
        Integer[] prefixosValidos = {2, 3, 4, 5, 7};
        int prefixo = Integer.parseInt(telefone.substring(2, 3));
        return telefone.length() != 10 || List.of(prefixosValidos).contains(prefixo);
    }

}
