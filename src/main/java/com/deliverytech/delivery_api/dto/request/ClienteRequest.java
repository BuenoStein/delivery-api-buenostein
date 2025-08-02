package com.deliverytech.delivery_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min=10, max = 100, message = "Não pode conter menos de 10 ou mais de 100 caracteres")
    private String nome;
    
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
    private String telefone;

    @Email
    @Size(max = 255, message = "Email não pode ter mais de 255 caracteres")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 255, message = "Endereço não pode ter mais de 255 caracteres")
    private String endereco;
}
