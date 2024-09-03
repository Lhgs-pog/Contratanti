package com.projeto.BackendContratanti.Model;


import com.projeto.BackendContratanti.Dto.EmpresaRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Table(name = "empresa")
@Entity(name = "empresa")
/*@Getter*/
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")

@Component
public class Empresa {

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
    @Column(name = "SENHA",nullable = false)
    private String senha;

    public Empresa(EmpresaRequestDTO dto){
        this.nome=dto.nome();
        this.cnpj= dto.cnpj();
        this.email=dto.email();
        this.telefone= dto.telefone();
        this.descricao= dto.descricao();
        this.url_linkedin= dto.url_linkedin();
        this.senha= dto.senha();
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

    public String getCnpj() {
        return cnpj;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUrl_linkedin() {
        return url_linkedin;
    }

    public String getSenha() {
        return senha;
    }
}
