package com.deliverytech.delivery_api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RelatorioService {
    
    /**
     * Relatório de vendas por restaurante
     */
    List<Map<String, Object>> relatorioVendasPorRestaurante(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Relatório dos produtos mais vendidos
     */
    List<Map<String, Object>> relatorioProdutosMaisVendidos(int limite, LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Relatório dos clientes mais ativos
     */
    List<Map<String, Object>> relatorioClientesAtivos(int limite, LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Relatório de pedidos por período
     */
    Map<String, Object> relatorioPedidosPorPeriodo(LocalDate dataInicio, LocalDate dataFim, String agrupamento);
    
    /**
     * Resumo geral de vendas
     */
    Map<String, Object> resumoVendas(LocalDate dataInicio, LocalDate dataFim);
}
