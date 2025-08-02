package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.ItemPedidoRequest;
import java.time.LocalDate;

import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.model.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    
    // === OPERAÇÕES BÁSICAS ===
    Pedido criar(Pedido pedido);
    Pedido buscarPorId(Long id);
    
    // === BUSCAS ESPECÍFICAS ===
    List<Pedido> buscarPorCliente(Long clienteId);         
    List<Pedido> buscarPorRestaurante(Long restauranteId);  
    List<Pedido> buscarPorStatus(StatusPedido status);     
    
    // === GESTÃO DE STATUS ===
    Pedido atualizarStatus(Long id, StatusPedido status); 
    Pedido confirmar(Long id);                             
    Pedido cancelar(Long pedidoId); //MUDAR para retornar Pedido
    
    // === GESTÃO DE ITENS ===
    Pedido adicionarItem(Long pedidoId, Long produtoId, Integer quantidade);
    
    // === CÁLCULOS ===
    BigDecimal calcularTotal(Pedido pedido);                
    /**
     * Calcular total do pedido baseado nos itens (sem salvar)
     * Para cotações e pré-cálculos
     * @param itens lista de itens do pedido
     * @return valor total calculado
     */
    BigDecimal calcularTotalPedido(List<ItemPedidoRequest> itens);

    /**
     * Listar pedidos com filtros opcionais
     */
    List<Pedido> listarComFiltros(StatusPedido status, LocalDate dataInicio, LocalDate dataFim);
    
    // === RELATÓRIOS ===
    List<Pedido> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);
    
    // === BUSCAS COM ITENS ===
    Optional<Pedido> buscarPorIdComItens(Long id);
    List<Pedido> buscarPorClienteComItens(Long clienteId);
    List<Pedido> listarTodos();
    void deletar(Long id);
}
