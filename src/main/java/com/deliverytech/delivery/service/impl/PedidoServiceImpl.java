package com.deliverytech.delivery_api.service.impl;

import com.deliverytech.delivery_api.dto.request.ItemPedidoRequest;import com.deliverytech.delivery_api.model.*;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    @Override
    public Pedido criar(Pedido pedido) {
        // ✅ SIMPLIFICAR: Definir dados básicos do pedido
        pedido.setStatus(StatusPedido.CRIADO); // ✅ USAR CRIADO ao invés de PENDENTE
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setValorTotal(BigDecimal.ZERO); // ✅ Iniciar com valor zero
        
        // ✅ SALVAR e retornar o pedido
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        log.info("Pedido criado com sucesso - ID: {}", pedidoSalvo.getId());
        
        return pedidoSalvo;
    }

    // ✅ IMPLEMENTAR métodos básicos se não existirem
    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorIdComItens(Long id) {
        return pedidoRepository.findByIdWithItens(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorClienteComItens(Long clienteId) {
        return pedidoRepository.findByClienteIdWithItens(clienteId);
    }

    @Override
    public Pedido adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // ✅ CRIAR item do pedido
        ItemPedido item = ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(quantidade)
                .precoUnitario(produto.getPreco())
                .build();

        // ✅ ADICIONAR item à lista
        if (pedido.getItens() == null) {
            pedido.setItens(new ArrayList<>());
        }
        pedido.getItens().add(item);

        // ✅ RECALCULAR valor total
        BigDecimal novoTotal = calcularTotal(pedido);
        pedido.setValorTotal(novoTotal);

        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido confirmar(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        pedido.setStatus(StatusPedido.CONFIRMADO);
        return pedidoRepository.save(pedido);
    }

    @Override
    public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    // ✅ IMPLEMENTAR método calcularTotal
    @Override
    public BigDecimal calcularTotal(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return pedido.getItens().stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ✅ ADICIONAR: Método faltante para buscar pedidos por restaurante
    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorRestaurante(Long restauranteId) {
        return pedidoRepository.findByRestauranteId(restauranteId);
    }

    // ✅ ADICIONAR: Método para cancelar pedido
    @Override
    public Pedido cancelar(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Verificar se pode cancelar
        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RuntimeException("Não é possível cancelar um pedido já entregue");
        }
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RuntimeException("Pedido já está cancelado");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        Pedido pedidoCancelado = pedidoRepository.save(pedido);
        log.info("Pedido cancelado - ID: {}", pedidoId);
        
        return pedidoCancelado; // ✅ RETORNAR o pedido cancelado
    }

    // ✅ ADICIONAR: Método para buscar por status
    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }

    // ✅ ADICIONAR: Método para buscar por período
    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.findByDataPedidoBetween(inicio, fim);
    }

        @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    /**
     * Calcular total do pedido baseado nos itens (sem salvar)
     * Para cotações e pré-cálculos - método requerido pela atividade
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(List<ItemPedidoRequest> itens) {
        log.info("Calculando total do pedido com {} itens", itens.size());
        
        if (itens == null || itens.isEmpty()) {
            log.warn("Lista de itens vazia, retornando total zero");
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        
        for (ItemPedidoRequest itemRequest : itens) {
            // Buscar produto para obter preço atual
            Produto produto = produtoRepository.findById(itemRequest.getProdutoId()) // CORRIGIDO: itemDTO → itemRequest
                .orElseThrow(() -> new RuntimeException("Produto não encontrado - ID: " + itemRequest.getProdutoId())); // CORRIGIDO
            
            // Validar disponibilidade
            if (!produto.getAtivo()) {
                throw new RuntimeException("Produto não está disponível - ID: " + itemRequest.getProdutoId()); // CORRIGIDO
            }
            
            // Calcular subtotal do item
            BigDecimal precoUnitario = produto.getPreco();
            BigDecimal quantidade = BigDecimal.valueOf(itemRequest.getQuantidade()); // CORRIGIDO: itemDTO → itemRequest
            BigDecimal subtotal = precoUnitario.multiply(quantidade);
            
            total = total.add(subtotal);
            
            log.debug("Item calculado - Produto: {}, Qtd: {}, Preço: R$ {}, Subtotal: R$ {}", 
                    produto.getNome(), itemRequest.getQuantidade(), precoUnitario, subtotal); // CORRIGIDO: itemDTO → itemRequest
        }
        
        log.info("Total calculado: R$ {}", total);
        return total;
    }
}
