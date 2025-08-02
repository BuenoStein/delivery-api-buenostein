package com.deliverytech.delivery_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String categoria;

    @NotBlank
    private String endereco;


    @DecimalMin("0.0")
    @NotNull(message = "Taxa de entrega é obrigatória")
    @Positive(message = "Taxa de entrega deve ser positiva")
    private BigDecimal taxaEntrega;

    @NotNull
    @Min(1)
    private Integer tempoEntregaMinutos;

    private String telefone;
    private String email;

    @Positive(message = "Avaliação deve ser positiva")
    @DecimalMax(value = "5.0", message = "Avaliação máxima é 5.0")
    private BigDecimal avaliacao;
}
