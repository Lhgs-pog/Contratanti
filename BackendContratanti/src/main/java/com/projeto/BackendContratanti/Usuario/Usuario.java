package com.projeto.BackendContratanti.Usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

//pasta para representar a tabela do banco

//Define como e o nome da tabela no banco
@Table(name = "usuario")

//Defini o nome da entidade
@Entity(name = "usuario")

//todos os @ a seguir sao o lombok
//Faz com que os getters de todos os atributos sejam gerados em runtime
@Getter
//pede ao lombok para criar um construtor sem argumento
@NoArgsConstructor
//pede ao mombok para criar um constructor com todos os argumentos
@AllArgsConstructor
//indica que o id e a representcao unica do usuario
@EqualsAndHashCode(of = "id")

public class Usuario {
    //Define que o id e a chave primaria e que o valor e gerado utomaticamente seguindo a estrategia identity que faz com que os valores sejam gerados me sequencia diferentemente do uid que sera aletorio, o uid e recomenddo para aplicacoes de verdade
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String url_curriculo;
    private String url_linkedin;
    private String url_github;
    private String cpf;
    private String cidade;
    private String senha;
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
}
