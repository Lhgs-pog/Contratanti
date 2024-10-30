package com.projeto.BackendContratanti.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

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
    private int id;
    @Column(name = "NOME", nullable = false,length = 30)
    private String nome;

    public Competencias(int id){
        this.id = id;
    }
}
