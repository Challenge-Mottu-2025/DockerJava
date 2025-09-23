package br.com.fiap.mottu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UsuarioForm {

    // Usuario
    @NotBlank
    @Pattern(regexp = "^\\d{11}$|^\\d{3}\\.\\d{3}\\.\\d{3}-?\\d{2}$", message = "CPF inválido")
    private String cpf;

    @NotBlank
    private String nome;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    // Endereco
    @NotNull private Long cep;
    @NotBlank private String pais;
    @NotBlank @Pattern(regexp = "^[A-Z]{2}$") private String estado;
    @NotBlank private String cidade;
    @NotBlank private String bairro;
    @NotNull private Long numero;
    @NotBlank private String logradouro;
    private String complemento;

    // Moto
    @NotBlank
    @Pattern(regexp = "^([A-Z]{3}[0-9]{4}|[A-Z]{3}[0-9][A-Z][0-9]{2})$", message = "Placa inválida")
    private String placa;

    // Se não informar, usaremos o mesmo CPF do usuário
    private String cpfMoto;

    @NotBlank
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "NIV inválido")
    private String niv;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,8}$", message = "Motor inválido")
    private String motor;

    @NotNull
    private Long renavam;

    @NotNull
    private Long fipe;
}