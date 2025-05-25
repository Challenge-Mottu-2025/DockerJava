package br.com.fiap.mottu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FuncionarioDTO(
        String nome,

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(
                regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$",
                message = "CPF inválido. Use 11 dígitos, com ou sem formatação."
        )
        String cpf,

        @NotBlank(message = "A senha é obrigatória")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$",
                message = "A senha deve ter 6 caracteres alfanuméricos, com pelo menos uma letra maiúscula e uma minúscula."
        )
        String senha
) {}
