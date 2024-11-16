package com.projeto.BackendContratanti.Services;

import com.projeto.BackendContratanti.Model.Empresa;
import com.projeto.BackendContratanti.Reposotory.EmpresaRepository;
import com.projeto.BackendContratanti.Dto.EmpresaRequestDTO;
import com.projeto.BackendContratanti.Dto.EmpresaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void saveEmpresa(EmpresaRequestDTO data) {
        Empresa empresa = new Empresa(data);
        repository.save(empresa);

        mailServices.enviarEmailTexto(
                empresa.getEmail(),
                "Bem-vindo!",
                "Obrigado por criar uma conta no nosso site. Te manteremos atualizado quando uma empresa se interessar por você."
        );
    }

    /**
     * Atualiza os dados de uma empresa e notifica o cliente via e-mail.
     */
    @Transactional
    public Empresa updateEmpresa(Empresa emp) {
        Empresa updated = repository.save(emp);

        mailServices.enviarEmailTexto(
                emp.getEmail(),
                "Confirmação de Atualização de Dados",
                "Prezado(a) " + emp.getNome() + ",\n\n" +
                        "Gostaríamos de informar que os dados da sua conta foram atualizados com sucesso. " +
                        "Se você não reconhece esta atualização, entre em contato conosco imediatamente.\n\n" +
                        "Atenciosamente,\nEquipe Contratanti."
        );

        return updated;
    }

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

    /**
     * Valida um e-mail.
     */
    public boolean validarEmail(String email) {
        return Pattern.matches(
                "^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$",
                email
        );
    }

    /**
     * Valida um número de telefone.
     */
    public boolean validarTelefone(String telefone) {
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

    private boolean validarDDD(String telefone) {
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

    private boolean validarPrefixo(String telefone) {
        Integer[] prefixosValidos = {2, 3, 4, 5, 7};
        int prefixo = Integer.parseInt(telefone.substring(2, 3));
        return telefone.length() != 10 || List.of(prefixosValidos).contains(prefixo);
    }

    public Optional<Empresa> empresaFindById(BigInteger eid) {
        return repository.findById(eid);
    }
}
