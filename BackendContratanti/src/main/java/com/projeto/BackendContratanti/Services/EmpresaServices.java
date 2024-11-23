package com.projeto.BackendContratanti.Services;

import com.projeto.BackendContratanti.Model.Empresa;
import com.projeto.BackendContratanti.Reposotory.EmpresaRepository;
import com.projeto.BackendContratanti.Dto.EmpresaRequestDTO;
import com.projeto.BackendContratanti.Dto.EmpresaResponseDTO;
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

    @Autowired
    private EmpresaRepository repository;

    @Autowired
    private MailServices mailServices;

    /**
     * Retorna uma lista de todas as empresas cadastradas como DTOs.
     */
    public List<EmpresaResponseDTO> findAllEmpresas() {
        return repository.findAll().stream()
                .map(EmpresaResponseDTO::new)
                .toList();
    }

    /**
     * Busca uma empresa pelo ID.
     */
    public Optional<Empresa> findEmpresaById(BigInteger id) {
        return repository.findById(id);
    }

    /**
     * Salva uma nova empresa e envia e-mail de boas-vindas.
     */
    @Transactional
    public ResponseEntity<Object> saveEmpresa(EmpresaRequestDTO data) {
        Empresa empresa = new Empresa(data);

        // Verifica se o e-mail já está registrado
        if (this.repository.findByEmail(data.email()) != null)
            return ResponseEntity.badRequest().build(); // Retorna erro se o e-mail já existir

        //validação de telefone
        if (!EmpresaServices.validarTelefone (empresa.getTelefone())){
            throw new IllegalArgumentException("O telefone informado não é válido.");
        }

        // Validação de formato de email
        if (!EmpresaServices.validarEmail (empresa.getEmail())){
            throw new IllegalArgumentException("O email informado não é válido.");
        }

        // Validação de formato do cnpj
        if (!EmpresaServices.validarCnpj (empresa.getCnpj())) {
            throw new IllegalArgumentException("O cnpj informado não é válido.");
        }
        // Criptografa a senha antes de salvar
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());

        //muda a senha da empresa para a senha hash
        empresa.setSenha(encryptedPassword);

        //salva a empresa no nosso banco de dados
        repository.save(empresa);

        //envia o email
        mailServices.enviarEmailTexto(
                empresa.getEmail(),
                "Bem-vindo!",
                "Obrigado por criar uma conta no nosso site. Te manteremos atualizado quando uma empresa se interessar por você."
        );
        return ResponseEntity.ok().build();
    }



    /**
     * Atualiza os dados de uma empresa e notifica o cliente via e-mail.
     */
    @Transactional
    public Empresa updateEmpresa(Empresa emp) {
        // Verifica se o CNPJ já existe no banco e não pertence à mesma empresa
        Optional<Empresa> empresaComMesmoCnpj = repository.findOptionalByCnpj(emp.getCnpj());
        if (empresaComMesmoCnpj.isPresent() && !empresaComMesmoCnpj.get().getId().equals(emp.getId())) {
            // Lança uma exceção caso o CNPJ já esteja sendo usado por outra empresa
            throw new DataIntegrityViolationException("O CNPJ informado já está em uso por outra empresa.");
        }

        // Verifica se o email já está registrado no banco e não pertence à mesma empresa
        Optional<Empresa> empresaComMesmoEmail = repository.findOptionalByEmail(emp.getEmail());
        if (empresaComMesmoEmail.isPresent() && !empresaComMesmoEmail.get().getId().equals(emp.getId())) {
            // Lança uma exceção caso o email já esteja em uso por outra empresa
            throw new DataIntegrityViolationException("O email informado já está em uso por outra empresa.");
        }

        //validação de telefone
        if (!EmpresaServices.validarTelefone (emp.getTelefone())){
            throw new IllegalArgumentException("O telefone informado não é válido.");
        }

        // Validação de formato de email
        if (!EmpresaServices.validarEmail (emp.getEmail())){
            throw new IllegalArgumentException("O email informado não é válido.");
        }

        // Validação de formato do cnpj
        if (!EmpresaServices.validarCnpj (emp.getCnpj())) {
            throw new IllegalArgumentException("O cnpj informado não é válido.");
        }
        // Atualiza a empresa no banco de dados
        Empresa updated = repository.save(emp);

        // Envia um e-mail informando que os dados da empresa foram atualizados com sucesso
        mailServices.enviarEmailTexto(
                emp.getEmail(),  // Destinatário do e-mail (o e-mail da empresa)
                "Confirmação de Atualização de Dados",  // Assunto do e-mail
                "Prezado(a) " + emp.getNome() + ",\n\n" +  // Saudação personalizada usando o nome da empresa
                        "Gostaríamos de informar que os dados da sua conta foram atualizados com sucesso. " +
                        "Se você não reconhece esta atualização, entre em contato conosco imediatamente.\n\n" +  // Corpo do e-mail
                        "Atenciosamente,\nEquipe Contratanti."  // Assinatura do e-mail
        );
        return emp;
    }
    public Optional<Empresa> empresaFindById(BigInteger eid) {
        return repository.findById(eid);
    }
        // Reto


        /**
         * Deleta uma empresa pelo ID e notifica o cliente.
         */
    @Transactional
    public void deleteEmpresaById(BigInteger id) {
        Empresa emp = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada com ID: " + id));

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

    /**
     * Deleta todas as empresas cadastradas.
     */
    @Transactional
    public void deleteAllEmpresas() {
        repository.deleteAll();
    }
    /**
     * Valida uma string para conter apenas letras e números.
     */
    public String validarString(String linha) {
        if (Pattern.matches("^[A-Za-z0-9]+$", linha)) {
            return linha;
        }
        return "";
    }
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


    /**
     * Valida um e-mail.
     */
    public static boolean validarEmail(String email) {
        return Pattern.matches(
                "^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$",
                email
        );
    }

    /**
     * Valida um número de telefone.
     */
    public static boolean validarTelefone(String telefone) {
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

    private static boolean validarPrefixo(String telefone) {
        Integer[] prefixosValidos = {2, 3, 4, 5, 7};
        int prefixo = Integer.parseInt(telefone.substring(2, 3));
        return telefone.length() != 10 || List.of(prefixosValidos).contains(prefixo);
    }

}
