package com.projeto.BackendContratanti.Model;

import com.projeto.BackendContratanti.Dto.CompetenciasRequestDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Table(name = "competencias")
@Entity(name = "competenciaas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Component

public class Competencias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMPETENCIA",nullable = false)
    private BigInteger id;
    @Column(name = "NOME", nullable = false,length = 30)
    private String nome;
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    public Competencias(BigInteger id){
        this.id = id;
    }

    public Competencias(CompetenciasRequestDTO dto){
        this.id=dto.id();
        this.email= dto.email();
        this.nome= dto.nome();
    }
}
