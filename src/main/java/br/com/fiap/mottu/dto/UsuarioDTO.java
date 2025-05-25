package br.com.fiap.mottu.dto;

import br.com.fiap.mottu.models.Endereco;
import br.com.fiap.mottu.models.Moto;
import jakarta.validation.Valid;

import java.util.Date;

public record UsuarioDTO(
        String cpf,
        @Valid Endereco endereco,
        @Valid Moto placa,
        Date dataNascimento,
        String nome
) {}
