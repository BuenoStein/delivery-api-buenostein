package com.deliverytech.delivery_api.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponse {
    private Long id;
    private String nome;
    private String email;
    private String telefone; 
    private String endereco;
    private Boolean ativo;
    private LocalDateTime dataCadastro;
}
