package br.com.fiap.mottu.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;

@Entity(name = "usuario")
@Table(name = "T_MT_Usuario")
@Data
public class Usuario {

    @Id
    @Column(name = "CD_CPF")
    @NotBlank(message = "CPF não pode ser vazio")
    @Pattern(
            regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$",
            message = "CPF inválido. Use 11 dígitos, com ou sem formatação."
    )
    private String cpf;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cep", referencedColumnName = "NR_CEP")
    private Endereco endereco;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "placa", referencedColumnName = "CD_PLACA")
    private Moto placa;
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Data de nascimento não pode ser nula")
    private Date dataNascimento;
    private String nome;
}
