package com.deliverytech.delivery_api.model;

public enum StatusPedido {
    CRIADO("Criado"),
    PENDENTE("Pendente"),
    CONFIRMADO("Confirmado"),
    PREPARANDO("Preparando"),
    SAIU_PARA_ENTREGA("Saiu para Entrega"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
