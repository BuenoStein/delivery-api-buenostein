package com.deliverytech.delivery_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    @Size(max = 500, message = "Descrição não pode ter mais de 500 caracteres")
    private String descricao;

    @DecimalMin("0.1")
    @DecimalMax("500.0")
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")  
    private BigDecimal preco;

    @NotNull(message = "ID do restaurante é obrigatório")
    @Positive(message = "ID do restaurante deve ser positivo") 
    private Long restauranteId;
}
