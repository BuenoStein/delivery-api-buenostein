package com.deliverytech.delivery_api.service.impl;

import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RelatorioServiceImpl implements RelatorioService {

    private final PedidoRepository pedidoRepository;

    @Override
    public List<Map<String, Object>> relatorioVendasPorRestaurante(LocalDate dataInicio, LocalDate dataFim) {
        // Implementação simplificada - você pode usar queries nativas ou JPQL
        return List.of(
            Map.of("restaurante", "Restaurante A", "totalVendas", new BigDecimal("1500.00"), "quantidadePedidos", 25),
            Map.of("restaurante", "Restaurante B", "totalVendas", new BigDecimal("1200.00"), "quantidadePedidos", 18)
        );
    }

    @Override
    public List<Map<String, Object>> relatorioProdutosMaisVendidos(int limite, LocalDate dataInicio, LocalDate dataFim) {
        return List.of(
            Map.of("produto", "Pizza Margherita", "quantidadeVendida", 50, "totalArrecadado", new BigDecimal("1000.00")),
            Map.of("produto", "Hambúrguer", "quantidadeVendida", 35, "totalArrecadado", new BigDecimal("700.00"))
        );
    }

    @Override
    public List<Map<String, Object>> relatorioClientesAtivos(int limite, LocalDate dataInicio, LocalDate dataFim) {
        return List.of(
            Map.of("cliente", "João Silva", "quantidadePedidos", 12, "totalGasto", new BigDecimal("600.00")),
            Map.of("cliente", "Maria Santos", "quantidadePedidos", 8, "totalGasto", new BigDecimal("450.00"))
        );
    }

    @Override
    public Map<String, Object> relatorioPedidosPorPeriodo(LocalDate dataInicio, LocalDate dataFim, String agrupamento) {
        return Map.of(
            "periodo", dataInicio + " até " + dataFim,
            "totalPedidos", 150,
            "valorTotal", new BigDecimal("7500.00"),
            "agrupamento", agrupamento != null ? agrupamento : "total"
        );
    }

    @Override
    public Map<String, Object> resumoVendas(LocalDate dataInicio, LocalDate dataFim) {
        return Map.of(
            "totalPedidos", 150,
            "valorTotalVendas", new BigDecimal("7500.00"),
            "ticketMedio", new BigDecimal("50.00"),
            "restaurantesAtivos", 5,
            "clientesAtivos", 45
        );
    }
}
