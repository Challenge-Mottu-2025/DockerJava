package br.com.fiap.mottu.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoDTO(
        @NotNull Long cep,
        @NotNull @Size(max = 35, message = "País deve ter no máximo 35 caracteres") String pais,
        @NotNull @Pattern(regexp = "^[A-Z]{2}$", message = "Estado inválido") String estado,
        @NotNull @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres") String cidade,
        @NotNull @Size(max = 50, message = "Bairro deve ter no máximo 50 caracteres") String bairro,
        @NotNull Long numero,
        @NotNull @Size(max = 50, message = "Logradouro deve ter no máximo 50 caracteres") String logradouro,
        @Size(max = 25, message = "Complemento deve ter no máximo 25 caracteres") String complemento
) {}