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

    //Dempendencias
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private MailServices mailServices;

    /*
     * Retorna todas us usuários usando o stream para passar po cada um dos elmentos da lista
     *  e o map para fazer uma modificação nesses elemento. No caso alí é para criar um novo record
     * com as informações buscadas.
     * */
    public List<UsuarioResponseDTO> usuariosGetAll() {
        // Converte a lista de entidades Usuario para DTOs
        return repository.findAll()
                .stream()
                .map(UsuarioResponseDTO::new)
                .toList();
    }

    /*
    * Retorna um usuário que contenha o id informado
    * */
    public Optional<Usuario> usuariofindById(BigInteger id) {
        return repository.findById(id);
    }

    /*
    * Envia um email para o soporte da equipe contendo a mensagem do usuário
    * */
    public void enviarEmailSuporte(SuportEmailDTO data){
        //Email de agradecimento ao usuário pelo envio da mensagem
        mailServices.enviarEmailTexto(
                data.email(),
                "Suporte contratanti",
                "Prezado(a) " + data.nome() + ",\n\n" +
                        "Gostariamos de aguadecer por entrar em contato com o nosso suporte tecnico, resolveremos a sua solicitação o quanto antes\n\n" +
                        "Atenciosamente,\nEquipe Contratanti."
        );

        //Envia o email para nós mesmos para análise futura
        mailServices.enviarEmailTexto(
                "contratanti@gmail.com",
                "Suporte contratanti",
                "Foi enivado do usuário" + data.nome() + ",\n\n" +
                        "uma mensagem do nosso suporte, conteúdo: " + data.mensagem()
        );
    }

    /*
    * Salva um novo usuário, validanod informações como:
    * Se o email é nulo
    * Se o email já existe
    * Se o telefone é válido
    * Se o email é válido
    * Criptografia da senha que será salva no banco
    * */
    public ResponseEntity<Object> saveUsuario(UsuarioRequestDTO data) {
        Usuario usuariodata = new Usuario(data);

        // Verifica se o e-mail já está registrado, retornando erro caso já exista
        if (data.email() == null)
            throw new IllegalArgumentException("O email não pode ser nulo");

        if (this.repository.findByEmail(data.email()) != null){
            return ResponseEntity.badRequest().build(); // Retorna erro 400 se o e-mail já existir
        }

        //validação de telefone
        if (!EmpresaServices.validarTelefone (usuariodata.getTelefone())){
            throw new IllegalArgumentException("O telefone informado não é válido.");
        }

        // Validação de formato de email
        if (!EmpresaServices.validarEmail (usuariodata.getEmail())){
            throw new IllegalArgumentException("O email informado não é válido.");
        }

        // Criptografa a senha antes de salvar
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());

        // Atualiza a senha do usuário com a senha criptografada
        usuariodata.setSenha(encryptedPassword);

        // Salva o usuário no banco de dados
        repository.save(usuariodata);

        // Retorna resposta positiva indicando que o cadastro foi realizado com sucesso
        return ResponseEntity.ok().build();
    }


    /*
    * Atualiza usuário já existente no banco com informações válidas
    * */
    public Usuario usuarioUpdate(BigInteger uid, UsuarioRequestDTO usuarioAtualizado) {
        // Busca o usuário pelo ID
        Usuario usuarioExistente = repository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

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
        usuarioExistente.setCidade(usuarioAtualizado.cidade());
        usuarioExistente.setDescricao(usuarioAtualizado.descricao());
        usuarioExistente.setTelefone(usuarioAtualizado.telefone());
        usuarioExistente.setUrl_curriculo(usuarioAtualizado.url_curriculo());
        usuarioExistente.setUrl_github(usuarioAtualizado.url_github());
        usuarioExistente.setUrl_linkedin(usuarioAtualizado.url_linkedin());
        usuarioExistente.setSenha(encryptedPassword);

        //validação de telefone
        if (usuarioExistente.getTelefone() == null) {

        } else if (!EmpresaServices.validarTelefone (usuarioExistente.getTelefone())){
            throw new IllegalArgumentException("O telefone informado não é válido.");
        }

        // Validação de formato de email
        if (usuarioExistente.getEmail() == null) {
            throw new IllegalArgumentException("O email não pode ser nulo");
        }else if (!EmpresaServices.validarEmail (usuarioExistente.getEmail())){
            throw new IllegalArgumentException("O email informado não é válido.");
        }

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

    /*
    * Deleta todos os usuários no banco de dados
    * */
    public void usuarioDeleteAll() {
        repository.deleteAll();
    }

    /*
    * Deleta um usuário com o id informado
    * */
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

    /*
    *
    *
    *
    * Voltar aqui depois
    *
    *
    *
    * */

    // Método auxiliar para salvar um usuário
    public void saveUsuario(Usuario usuario) {
        repository.save(usuario);
    }

    /*
    * Válida um texto para somente caracteres permitidos
    * */
    public String validarString(String linha) {
        Pattern pattern = Pattern.compile("^[A-Za-z1-9]$");
        Matcher matcher = pattern.matcher(linha);
        boolean matchfound = matcher.find();
        if (matchfound)
            return linha;
        return "";
    }

    /*
    * Válida o email
    * */
    public boolean validarEmail(String email) {
        Pattern pattern = Pattern.compile(
                "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
        Matcher matcher;
        return pattern.matcher(email).find();
    }

    /*
    * Válida um número de telefone
    * */
    public boolean validarTelefone(String telefone) {
        // Verifica se o telefone é nulo ou vazio
        if (telefone == null || telefone.isBlank()) {
            return false; // Considera inválido se for nulo ou vazio
        }

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
