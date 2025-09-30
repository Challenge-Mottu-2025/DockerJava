package br.com.fiap.mottu.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "T_MT_ROLE")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ROLE")
    private Long id;

    @Column(name = "NM_ROLE", unique = true, nullable = false)
    private String nome;
}