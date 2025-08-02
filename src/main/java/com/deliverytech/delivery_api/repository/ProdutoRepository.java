package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByRestauranteId(Long restauranteId);
    List<Produto> findByDisponivelTrue();
    List<Produto> findByCategoria(String categoria);
    
    @Query("SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Produto> findByNomeContainingIgnoreCase(@Param("nome") String nome);
    
    @Query(value = "SELECT p.nome, COUNT(ip.produto_id) as quantidade_vendida " +
                   "FROM produto p " +
                   "LEFT JOIN item_pedido ip ON p.id = ip.produto_id " +
                   "GROUP BY p.id, p.nome " +
                   "ORDER BY quantidade_vendida DESC " +
                   "LIMIT 5", nativeQuery = true)
    List<Object[]> produtosMaisVendidos();
}


