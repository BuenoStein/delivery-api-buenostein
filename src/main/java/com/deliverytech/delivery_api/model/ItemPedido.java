package com.deliverytech.delivery_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "item_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    // ✅ MÉTODO PARA CALCULAR SUBTOTAL AUTOMATICAMENTE
    @PrePersist
    @PreUpdate
    private void calcularSubtotal() {
        if (precoUnitario != null && quantidade != null) {
            this.subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        }
    }

    // ✅ MÉTODO AUXILIAR PARA DEFINIR SUBTOTAL MANUALMENTE SE NECESSÁRIO
    public void setSubtotal() {
        calcularSubtotal();
    }

    // ✅ MÉTODO AUXILIAR PARA OBTER VALOR TOTAL DO ITEM
    public BigDecimal getValorTotal() {
        return subtotal != null ? subtotal : BigDecimal.ZERO;
    }
}
