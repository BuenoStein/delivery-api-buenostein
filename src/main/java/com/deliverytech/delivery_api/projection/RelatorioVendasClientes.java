package com.deliverytech.delivery_api.projection;

import java.math.BigDecimal;

public interface RelatorioVendasClientes {
    Long getIdCliente();
    String getNomeCliente();
    BigDecimal getTotalCompras();
    Long getQuantidadePedidos();
}
