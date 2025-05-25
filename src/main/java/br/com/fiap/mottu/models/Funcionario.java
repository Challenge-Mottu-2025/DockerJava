package br.com.fiap.mottu.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Entity(name = "funcionarios")
@Table(name = "T_MT_Funcionario")
@Data
public class Funcionario extends RepresentationModel<Funcionario> {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;
    @Column(name = "ID_NOME")
    private String nome;
    @Pattern(
            regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$",
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

}
