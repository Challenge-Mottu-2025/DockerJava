package br.com.fiap.mottu.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity(name = "endereco")
@Table(name = "T_MT_Endereco")
@Data
public class Endereco {

    @Id
    @NotNull
    @Column(name = "NR_CEP")
    private Long cep;

    @NotNull
    @Size(max = 35, message = "País deve ter no máximo 35 caracteres")
    @Column(name = "ID_PAIS", nullable = false)
    private String pais;

    @NotNull
    @Pattern(regexp = "^[A-Z]{2}$", message = "Estado inválido")
    @Column(name = "SG_ESTADO", nullable = false, length = 2)
    private String estado;

    @NotNull
    @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres")
    @Column(name = "ID_CIDADE", nullable = false)
    private String cidade;

    @NotNull
    @Size(max = 50, message = "Bairro deve ter no máximo 50 caracteres")
    @Column(name = "ID_BAIRRO", nullable = false)
    private String bairro;

    @NotNull
    @Column(name = "NR_NUMERO", nullable = false)
    private Long numero;

    @NotNull
    @Size(max = 50, message = "Logradouro deve ter no máximo 50 caracteres")
    @Column(name = "DS_LOGRADOURO", nullable = false)
    private String logradouro;

    @Size(max = 25, message = "Complemento deve ter no máximo 25 caracteres")
    @Column(name = "DS_COMPLEMENTO")
    private String complemento;
}
