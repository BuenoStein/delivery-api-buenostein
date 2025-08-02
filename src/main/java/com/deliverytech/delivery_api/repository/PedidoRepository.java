package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByRestauranteId(Long restauranteId);
    List<Pedido> findByStatus(StatusPedido status);
    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    // ✅ ADICIONAR: Query com JOIN FETCH para carregar itens
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.produto WHERE p.id = :id")
    Optional<Pedido> findByIdWithItens(@Param("id") Long id);

    // ✅ ADICIONAR: Query com JOIN FETCH para carregar itens por cliente
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.produto WHERE p.cliente.id = :clienteId")
    List<Pedido> findByClienteIdWithItens(@Param("clienteId") Long clienteId);

    //=== CONSULTAS CUSTOMIZADAS ===
    @Query("SELECT p.restaurante.nome, SUM(p.valorTotal) " +
           "FROM Pedido p " +
           "GROUP BY p.restaurante.nome " +
           "ORDER BY SUM(p.valorTotal) DESC")
    List<Object[]> calcularTotalVendasPorRestaurante();
    
    @Query("SELECT p FROM Pedido p WHERE p.valorTotal > :valor ORDER BY p.valorTotal DESC")
    List<Pedido> buscarPedidosComValorAcimaDe(@Param("valor") BigDecimal valor);
    
    @Query("SELECT p FROM Pedido p " +
           "WHERE p.dataPedido BETWEEN :inicio AND :fim " +
           "AND p.status = :status " +
           "ORDER BY p.dataPedido DESC")
    List<Pedido> relatorioPedidosPorPeriodoEStatus(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("status") StatusPedido status);

    @Query("SELECT p.restaurante.nome as nomeRestaurante, " +
           "SUM(p.valorTotal) as totalVendas, " +
           "COUNT(p.id) as quantidadePedidos " +
           "FROM Pedido p " +
           "GROUP BY p.restaurante.nome")
    List<RelatorioVendas> obterRelatorioVendasPorRestaurante();

    /**
     * Buscar por status e período
     */
    List<Pedido> findByStatusAndDataPedidoBetween(StatusPedido status, LocalDateTime inicio, LocalDateTime fim);

    /**
     * Buscar pedidos a partir de uma data
     */
    List<Pedido> findByDataPedidoGreaterThanEqual(LocalDateTime data);

    /**
     * Buscar pedidos até uma data
     */
    List<Pedido> findByDataPedidoLessThanEqual(LocalDateTime data);
}
