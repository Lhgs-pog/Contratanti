package com.projeto.BackendContratanti.Services;

import com.projeto.BackendContratanti.Dto.SuportEmailDTO;
import com.projeto.BackendContratanti.Model.Usuario;
import com.projeto.BackendContratanti.Reposotory.UsuarioRepository;
import com.projeto.BackendContratanti.Dto.UsuarioRequestDTO;
import com.projeto.BackendContratanti.Dto.UsuarioResponseDTO;
import com.projeto.BackendContratanti.Security.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private MailServices mailServices;

    // Retorna todos os usuários como uma lista de DTOs (Data Transfer Objects)
    public List<UsuarioResponseDTO> usuariosGetAll() {
        // Converte a lista de entidades Usuario para DTOs
        List<UsuarioResponseDTO> listausuarios = repository.findAll().stream().map(UsuarioResponseDTO::new).toList();
        return listausuarios;
    }

    // Retorna um usuário a partir do ID (utilizando BigInteger como tipo de ID)
    public Optional<Usuario> usuariofindById(BigInteger id) {
        return repository.findById(id);
    }

    // Retorna uma lista de usuários para a parte de exibição de curriculos na página principal
    public List<UsuarioResponseDTO> buscarComLimite(){
        Pageable pageable = PageRequest.of(0, 30);
        return repository.buscarComLimite(pageable);
    }

    public List<UsuarioResponseDTO> buscarUsuariosPorCompetencias(List<String> competencias) {
        return repository.buscarUsuariosPorCompetencias(competencias, (long) competencias.size());
    }

    public void enviarEmailSuporte(SuportEmailDTO data){
        mailServices.enviarEmailTexto(
                data.email(),
                "Suporte contratanti",
                "Prezado(a) " + data.nome() + ",\n\n" +
                        "Gostariamos de aguadecer por entrar em contato com o nosso suporte tecnico, resolveremos a sua solicitação o quanto antes\n\n" +
                        "Atenciosamente,\nEquipe Contratanti."
        );
        mailServices.enviarEmailTexto(
                "contratanti@gmail.com",
                "Suporte contratanti",
                "Foi enivado do usuário" + data.nome() + ",\n\n" +
                        "uma mensagem o nosso suporte, conteúdo: " + data.mensagem()
        );
    }

    // Salva um novo usuário no banco de dados após realizar validações e criptografar a senha
    public ResponseEntity<Object> saveUsuario(UsuarioRequestDTO data) {
        Usuario usuariodata = new Usuario(data);

        // Verifica se o e-mail já está registrado, retornando erro caso já exista
        if (this.repository.findByEmail(data.email()) != null)
            return ResponseEntity.badRequest().build(); // Retorna erro 400 se o e-mail já existir

        //validação de telefone
        if (!EmpresaServices.validarTelefone (usuariodata.getTelefone())){
            throw new IllegalArgumentException("O telefone informado não é válido.");
        }

        // Validação de formato de email
        if (!EmpresaServices.validarEmail (usuariodata.getEmail())){
            throw new IllegalArgumentException("O email informado não é válido.");
        }

        // Validação de formato do cpf
        /*if (!UsuarioServices.validarCpf (usuariodata.getCpf())) {
            throw new IllegalArgumentException("O cpf informado não é válido.");
        } */
        // Criptografa a senha antes de salvar
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());

        // Atualiza a senha do usuário com a senha criptografada
        usuariodata.setSenha(encryptedPassword);

        // Salva o usuário no banco de dados
        repository.save(usuariodata);

        // Retorna resposta positiva indicando que o cadastro foi realizado com sucesso
        return ResponseEntity.ok().build();
    }


    // Atualiza os dados de um usuário existente
    public Usuario usuarioUpdate(BigInteger uid, UsuarioRequestDTO usuarioAtualizado) {
        // Busca o usuário pelo ID
        Usuario usuarioExistente = repository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // Verifica se o CPF já existe no banco e se pertence a outro usuário
        Optional<Usuario> usuarioComMesmoCpf = repository.findByCpf(usuarioAtualizado.cpf());
        if (usuarioComMesmoCpf.isPresent() && !usuarioComMesmoCpf.get().getId().equals(uid)) {
            throw new DataIntegrityViolationException("O CPF informado já está em uso por outro usuário.");
        }

        // Verifica se o e-mail já existe no banco e se pertence a outro usuário
        Optional<Usuario> usuarioComMesmoEmail = repository.findOptionalByEmail(usuarioAtualizado.email());
        if (usuarioComMesmoEmail.isPresent() && !usuarioComMesmoEmail.get().getId().equals(uid)) {
            throw new DataIntegrityViolationException("O email informado já está em uso por outro usuário.");
        }


        // Criptografa a nova senha do usuário
        String encryptedPassword = new BCryptPasswordEncoder().encode(usuarioAtualizado.senha());

        // Atualiza os dados do usuário
        usuarioExistente.setNome(usuarioAtualizado.nome());
        usuarioExistente.setEmail(usuarioAtualizado.email());
        usuarioExistente.setCpf(usuarioAtualizado.cpf());
        usuarioExistente.setCidade(usuarioAtualizado.cidade());
        usuarioExistente.setDescricao(usuarioAtualizado.descricao());
        usuarioExistente.setTelefone(usuarioAtualizado.telefone());
        usuarioExistente.setUrl_curriculo(usuarioAtualizado.url_curriculo());
        usuarioExistente.setUrl_github(usuarioAtualizado.url_github());
        usuarioExistente.setUrl_linkedin(usuarioAtualizado.url_linkedin());
        usuarioExistente.setSenha(encryptedPassword);

        //validação de telefone
        if (!EmpresaServices.validarTelefone (usuarioExistente.getTelefone())){
            throw new IllegalArgumentException("O telefone informado não é válido.");
        }

        // Validação de formato de email
        if (!EmpresaServices.validarEmail (usuarioExistente.getEmail())){
            throw new IllegalArgumentException("O email informado não é válido.");
        }

        // Validação de formato do cpf
        /*if (!UsuarioServices.validarCpf (usuarioExistente.getCpf())) {
            throw new IllegalArgumentException("O cnpj informado não é válido.");
        }*/

        // Envia um e-mail confirmando a atualização dos dados
        mailServices.enviarEmailTexto(
                usuarioExistente.getEmail(),
                "Confirmação de Atualização de Dados",
                "Prezado(a) " + usuarioExistente.getNome() + ",\n\n" +
                        "Gostaríamos de informar que os dados da sua conta foram atualizados com sucesso. " +
                        "Se você não reconhece esta atualização, entre em contato conosco imediatamente.\n\n" +
                        "Atenciosamente,\nEquipe Contratanti."
        );

        // Salva o usuário atualizado no banco de dados
        return repository.save(usuarioExistente);
    }

    // Deleta todos os usuários do banco de dados
    public void usuarioDeleteAll() {
        repository.deleteAll();
    }

    // Deleta um usuário pelo ID
    public void usuarioDeleteById(BigInteger id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada com ID: " + id));

        // Envia um e-mail confirmando a exclusão da conta
        mailServices.enviarEmailTexto(
                usuario.getEmail(),
                "Conta Excluída",
                "Prezado(a) " + usuario.getNome() + ",\n\n" +
                        "Sua conta foi excluída com sucesso. Todos os dados associados foram removidos permanentemente.\n\n" +
                        "Agradecemos por fazer parte da nossa comunidade e esperamos vê-lo(a) novamente no futuro.\n\n" +
                        "Atenciosamente,\nEquipe Contratanti."
        );

        // Deleta o usuário do banco de dados
        repository.delete(usuario);
    }

    // Método auxiliar para salvar um usuário
    public void saveUsuario(Usuario usuario) {
        repository.save(usuario);
    }

    // Valida uma string para permitir somente letras e números, prevenindo SQL Injection e outros ataques
    public String validarString(String linha) {
        Pattern pattern = Pattern.compile("^[A-Za-z1-9]$");
        Matcher matcher = pattern.matcher(linha);
        boolean matchfound = matcher.find();
        if (matchfound)
            return linha;
        return "";
    }

    // Valida um CPF, verificando sua estrutura e dígitos
    public static boolean validarCpf(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("\\D", "");

        // Verifica se o CPF tem exatamente 11 dígitos e não é uma sequência repetida
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        char dig10, dig11;
        int soma, peso, num, resto;

        // Cálculo do 1º dígito verificador
        soma = 0;
        peso = 10;
        for (int i = 0; i < 9; i++) {
            num = cpf.charAt(i) - '0'; // Converte char para número
            soma += num * peso;
            peso--;
        }

        resto = soma % 11;
        dig10 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

        // Cálculo do 2º dígito verificador
        soma = 0;
        peso = 11;
        for (int i = 0; i < 10; i++) {
            num = cpf.charAt(i) - '0';
            soma += num * peso;
            peso--;
        }

        resto = soma % 11;
        dig11 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

        // Verifica se os dígitos calculados correspondem aos dígitos fornecidos
        return dig10 == cpf.charAt(9) && dig11 == cpf.charAt(10);
    }


    // Valida se o e-mail tem um formato correto
    public boolean validarEmail(String email) {
        Pattern pattern = Pattern.compile(
                "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
        Matcher matcher;
        return pattern.matcher(email).find();
    }

    // Valida um número de telefone, garantindo que ele siga um formato correto
    public boolean validarTelefone(String telefone) {
        telefone = telefone.replaceAll("\\D", "");  // Remove caracteres não numéricos

        // Verifica se o telefone tem a quantidade correta de dígitos
        if (!(telefone.length() >= 10 && telefone.length() <= 11)) {
            return false;
        }

        // Se for um celular (11 dígitos), verifica se começa com 9
        if (telefone.length() == 11 && Integer.parseInt(telefone.substring(2, 3)) != 9) {
            return false;
        }

        // Verifica se o número contém todos os dígitos iguais
        Pattern p = Pattern.compile(telefone.charAt(0) + "{" + telefone.length() + "}");
        Matcher m = p.matcher(telefone);
        if (m.find()) {
            return false;
        }

        // Verifica se o DDD é válido
        Integer[] codigosDDD = {
                11, 12, 13, 14, 15, 16, 17, 18, 19,
                21, 22, 24, 27, 28, 31, 32, 33, 34,
                35, 37, 38, 41, 42, 43, 44, 45, 46,
                47, 48, 49, 51, 53, 54, 55, 61, 62,
                64, 63, 65, 66, 67, 68, 69, 71, 73,
                74, 75, 77, 79, 81, 82, 83, 84, 85,
                86, 87, 88, 89, 91, 92, 93, 94, 95,
                96, 97, 98, 99};
        if (!java.util.Arrays.asList(codigosDDD).contains(Integer.parseInt(telefone.substring(0, 2)))) {
            return false;
        }

        // Verifica se o número está em formato válido
        Integer[] prefixos = {2, 3, 4, 5, 7};
        return telefone.length() != 10 || java.util.Arrays.asList(prefixos).contains(Integer.parseInt(telefone.substring(2, 3)));
    }
}
