package com.deliverytech.delivery_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String senha;

    private String nome;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean ativo = true;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    private Long restauranteId;
}
