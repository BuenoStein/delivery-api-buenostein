package com.deliverytech.delivery_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String categoria;

    private String descricao;

    private BigDecimal preco;

    @Builder.Default
    private Boolean disponivel = true;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    public boolean getAtivo() {
        return this.disponivel != null ? this.disponivel : false;
    }

    public void setAtivo(boolean ativo) {
        this.disponivel = ativo;
    }
}
