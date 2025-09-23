package br.com.fiap.mottu.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity(name = "moto")
@Table(name = "T_MT_Moto")
@Data
public class Moto {

    @Id
    @Column(name = "CD_PLACA")
    @Pattern(
            regexp = "^([A-Z]{3}[0-9]{4}|[A-Z]{3}[0-9][A-Z][0-9]{2})$",
            message = "Tipo de placa não suportada."
    )
    private String placa;

    @Column(name = "CD_CPF", nullable = false)
    @Size(max = 11, message = "CPF deve ter no máximo 11 dígitos")
    @NotBlank(message = "CPF não pode ser vazio")
    private String cpf;

    @Column(name = "CD_NIV", nullable = false)
    @Pattern(
            regexp = "^[A-HJ-NPR-Z0-9]{17}$",
            message = "Número do chassi (NIV) inválido."
    )
    @NotBlank
    private String niv;

    @Column(name = "CD_MOTOR", nullable = false)
    @Pattern(
            regexp = "^[A-Z]{2,3}[0-9]{4,8}$",
            message = "Número do motor não identificado."
    )
    @NotBlank
    private String motor;

    @Column(name = "CD_RENAVAM", nullable = false)
    @NotNull(message = "Renavam não pode ser nulo")
    @Min(value = 1000000, message = "Renavam deve ter no mínimo 7 dígitos")
    @Max(value = 99999999999L, message = "Renavam deve ter no máximo 11 dígitos")
    private Long renavam;

    @Column(name = "CD_FIPE", nullable = false)
    @NotNull(message = "FIPE não pode ser nulo")
    @Min(value = 0, message = "FIPE deve ser positivo")
    @Max(value = 9999999, message = "FIPE deve ter no máximo 7 dígitos")
    private Long fipe;
}