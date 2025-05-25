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
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "cpf", referencedColumnName = "CD_CPF")
    private String cpf;
    @Pattern(
            regexp = "^[A-HJ-NPR-Z0-9]{17}$",
            message = "Número do chassi inválido."
    )
    @NotBlank
    private String niv;
    @Pattern(
            regexp = "^[A-Z]{2,3}[0-9]{4,8}$",
            message = "Número do motor não identificado."
    )
    @NotBlank
    private String motor;
    @Pattern(
            regexp = "^[A-Z0-9]{6,10}$",
            message = "FIPE não suportado."
    )
    private String fipe;
    @Size(
            max= 11,
            message = "Sequência de números inválida."
    )
    @NotNull(message = "Renavam não pode ser nulo")
    @Min(value = 1000000, message = "Renavam deve ter no mínimo 7 dígitos")
    @Max(value = 99999999999L, message = "Renavam deve ter no máximo 11 dígitos")
    private Long renavam;

}
