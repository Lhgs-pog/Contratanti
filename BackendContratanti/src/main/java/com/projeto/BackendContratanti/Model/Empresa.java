package com.projeto.BackendContratanti.Model;


import com.projeto.BackendContratanti.Dto.EmpresaRequestDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Table(name = "empresa")
@Entity(name = "empresa")
//getters
@Getter
//setters
@Setter
//construtor com todos os atributos
@AllArgsConstructor
//construtor vazio
@NoArgsConstructor
@EqualsAndHashCode(of = "id")

@Component
public class Empresa implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EMPRESAS",nullable = false)
    private BigInteger id;
    @Column(name = "NOME",nullable = false,length = 50)
    private String nome;
    @Column(name = "EMAIL",nullable = false,length = 50,unique = true)
    private String email;
    @Column(name = "TELEFONE",nullable = true,length = 11)
    private String telefone;
    @Column(name = "CNPJ",nullable = false,length = 14,unique = true)
    private String cnpj;
    @Column(name = "DESCRICAO",columnDefinition = "TEXT",nullable = true)
    private String descricao;
    @Column(name = "LINK_LINKEDIN",columnDefinition = "TEXT",nullable = true)
    private String url_linkedin;
    @Column(name = "SENHA",nullable = false, length = 100)
    private String senha;

    public Empresa(EmpresaRequestDTO dto){
        this.nome=dto.nome();
        this.email=dto.email();
        this.telefone= dto.telefone();
        this.cnpj= dto.cnpj();
        this.descricao= dto.descricao();
        this.url_linkedin= dto.url_linkedin();
        this.senha= dto.senha();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
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
