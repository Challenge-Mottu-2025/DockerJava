package br.com.fiap.mottu.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

import org.springframework.hateoas.RepresentationModel;

@Entity(name = "funcionarios")
@Table(name = "T_MT_Funcionario")
@Data
public class Funcionario extends RepresentationModel<Funcionario> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MT_FUNCIONARIO")
    @SequenceGenerator(name = "SEQ_MT_FUNCIONARIO", sequenceName = "SEQ_MT_FUNCIONARIO", allocationSize = 1)
    @Column(name = "ID_FUNCIONARIO")
    private Long id;

    @Column(name = "ID_NOME")
    private String nome;

    @Pattern(
            regexp = "^\\d{11}$|^\\d{3}\\.\\d{3}\\.\\d{3}-?\\d{2}$",
            message = "CPF inválido. Use 11 dígitos, com ou sem formatação."
    )
    @NotBlank(message = "O CPF é obrigatório")
    @Column(name = "CD_CPF")
    private String cpf;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$",
            message = "A senha deve ter 6 caracteres alfanuméricos, com pelo menos uma letra maiúscula e uma minúscula."
    )
    @NotBlank(message = "A senha é obrigatória")
    @Column(name = "CD_SENHA")
    private String senha;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "NR_CEP", referencedColumnName = "NR_CEP", nullable = false)
    private Endereco endereco;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "T_MT_FUNCIONARIO_ROLE",
        joinColumns = @JoinColumn(name = "ID_FUNCIONARIO"),
        inverseJoinColumns = @JoinColumn(name = "ID_ROLE")
    )
    private Set<Role> roles;
}