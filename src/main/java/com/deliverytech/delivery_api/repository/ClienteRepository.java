package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Cliente> findByAtivoTrue();
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    
    @Query(value = "SELECT c.nome, COUNT(p.id) as total_pedidos " +
                   "FROM cliente c " +
                   "LEFT JOIN pedido p ON c.id = p.cliente_id " +
                   "GROUP BY c.id, c.nome " +
                   "ORDER BY total_pedidos DESC " +
                   "LIMIT 10", nativeQuery = true)
    List<Object[]> rankingClientesPorPedidos();
}
































