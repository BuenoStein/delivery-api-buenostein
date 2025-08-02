package com.deliverytech.delivery_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurante {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String categoria;

    private String telefone;

    //mudança 16/07/ Remover exceção do setter
    private BigDecimal avaliacao;

    private BigDecimal taxaEntrega;

    private Integer tempoEntregaMinutos;

    private Boolean ativo = true;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL)
    private List<Produto> produtos;

    // Mudança 16/07
    @OneToMany(mappedBy = "restaurante")
    private List<Pedido> pedidos;

    @JsonIgnore
    public void inativar() {
        this.ativo = false;
    }

}
