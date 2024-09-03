package com.projeto.BackendContratanti.Model;

import com.projeto.BackendContratanti.Dto.UsuarioRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

//pasta para representar a tabela do banco

//Define como e o nome da tabela no banco
@Table(name = "usuario")

//Defini o nome da entidade
@Entity(name = "usuario")

//todos os @ a seguir são do lombok
//pede ao lombok para criar um construtor sem argumento
@NoArgsConstructor
//pede ao mombok para criar um constructor com todos os argumentos
@AllArgsConstructor
//indica que o id e a representcao unica do usuario
@EqualsAndHashCode(of = "id")

@Component
public class Usuario {
    //Define que o id e a chave primaria e que o valor e gerado utomaticamente seguindo a estrategia identity que faz com que os valores sejam gerados me sequencia diferentemente do uid que sera aletorio, o uid e recomenddo para aplicacoes de verdade
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIOS",nullable = false)
    private BigInteger id;
    @Column( name = "NOME", nullable = false, length = 50)
    private String nome;
    @Column(name = "EMAIL" ,nullable = false,length = 50,unique = true)
    private String email;
    @Column(name = "TELEFONE" ,nullable = true,length = 11)
    private String telefone;
    @Column(name = "LINK_CURRICULO",columnDefinition = "TEXT",nullable = false)
    private String url_curriculo;
    @Column(name = "LINK_LINKEDIN",columnDefinition = "TEXT",nullable = true)
    private String url_linkedin;
    @Column(name = "LINK_GITHUB",columnDefinition = "TEXT",nullable = true)
    private String url_github;
    @Column(name = "CPF",nullable = false,length = 11,unique = true)
    private String cpf;
    @Column(name = "CIDADE_NOME",nullable = true,length = 255)
    private String cidade;
    @Column(name = "SENHA",nullable = false,length = 30)
    private String senha;
    @Column(name = "DESCRICAO",columnDefinition = "TEXT",nullable = false)
    private String descricao;

    public Usuario(UsuarioRequestDTO data){
        this.nome = data.nome();
        this.cidade = data.cidade();
        this.cpf = data.cpf();
        this.descricao = data.descricao();
        this.email = data.email();
        this.senha = data.senha();
        this.telefone = data.telefone();
        this.url_curriculo = data.url_curriculo();
        this.url_github = data.url_github();
        this.url_linkedin = data.url_linkedin();
    }

    public BigInteger getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getUrl_curriculo() {
        return url_curriculo;
    }

    public String getUrl_linkedin() {
        return url_linkedin;
    }

    public String getUrl_github() {
        return url_github;
    }

    public String getCpf() {
        return cpf;
    }

    public String getCidade() {
        return cidade;
    }

    public String getSenha() {
        return senha;
    }

    public String getDescricao() {
        return descricao;
    }
}
