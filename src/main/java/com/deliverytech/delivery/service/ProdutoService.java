package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.model.Produto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProdutoService {
    // === OPERAÇÕES BÁSICAS ===
    Produto cadastrar(Produto produto);                        
    Optional<Produto> buscarPorId(Long id);                   
    List<Produto> listarTodos();                              
    Produto atualizar(Long id, Produto produtoAtualizado);    
    void inativar(Long id);                                   
    void deletar(Long id);
    
    // === BUSCAS ESPECÍFICAS ===
    List<Produto> buscarPorRestaurante(Long restauranteId);   
    List<Produto> buscarPorCategoria(String categoria);       
    List<Produto> listarDisponiveis();                       
    
    // === REGRAS DE NEGÓCIO ===
    void alterarDisponibilidade(Long id, boolean disponivel); 
    void validarPreco(BigDecimal preco);                      
}
