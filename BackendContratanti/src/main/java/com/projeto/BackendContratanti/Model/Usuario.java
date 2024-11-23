package com.projeto.BackendContratanti.Model;

import com.projeto.BackendContratanti.Dto.UsuarioRequestDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

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
//getters
@Getter
//setters
@Setter
//indica que o id e a representcao unica do usuario
@EqualsAndHashCode(of = "id")

@Component
public class Usuario implements UserDetails {
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
    @Enumerated(EnumType.STRING)
    @Column(name = "roles" ,nullable = false)
    private UsuarioRoles role;

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

    public Usuario(String email, String senha, UsuarioRoles role){
        this.email = email;
        this.senha = senha;
        this.role = role;
    }
//tempo do video 17:40
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UsuarioRoles.ADMIN) return List.of(new SimpleGrantedAuthority("UsuarioRoles.ADMIN"), new SimpleGrantedAuthority("UsuarioRoles.USER"));
        return null;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        //return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
        //return UserDetails.super.isEnabled();
        return true;
    }
}
