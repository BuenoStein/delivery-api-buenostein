package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller responsável pelos endpoints de relatórios gerenciais
 */
@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    /**
     * Relatório de vendas por restaurante
     * GET /api/relatorios/vendas-por-restaurante
     */
    @GetMapping("/vendas-por-restaurante")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> vendasPorRestaurante(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        List<Map<String, Object>> vendas = relatorioService.relatorioVendasPorRestaurante(dataInicio, dataFim);
        return ResponseEntity.ok(vendas);
    }

    /**
     * Relatório dos produtos mais vendidos
     * GET /api/relatorios/produtos-mais-vendidos
     */
    @GetMapping("/produtos-mais-vendidos")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> produtosMaisVendidos(
            @RequestParam(defaultValue = "10") int limite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        List<Map<String, Object>> produtos = relatorioService.relatorioProdutosMaisVendidos(limite, dataInicio, dataFim);
        return ResponseEntity.ok(produtos);
    }

    /**
     * Relatório dos clientes mais ativos
     * GET /api/relatorios/clientes-ativos
     */
    @GetMapping("/clientes-ativos")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> clientesAtivos(
            @RequestParam(defaultValue = "10") int limite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        List<Map<String, Object>> clientes = relatorioService.relatorioClientesAtivos(limite, dataInicio, dataFim);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Relatório de pedidos por período
     * GET /api/relatorios/pedidos-por-periodo
     */
    @GetMapping("/pedidos-por-periodo")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> pedidosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String agrupamento) { // 'dia', 'mes', 'ano'
        
        Map<String, Object> relatorio = relatorioService.relatorioPedidosPorPeriodo(dataInicio, dataFim, agrupamento);
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Resumo geral de vendas
     * GET /api/relatorios/resumo-vendas
     */
    @GetMapping("/resumo-vendas")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> resumoVendas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        Map<String, Object> resumo = relatorioService.resumoVendas(dataInicio, dataFim);
        return ResponseEntity.ok(resumo);
    }
}
