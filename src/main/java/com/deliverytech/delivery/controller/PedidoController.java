// mudança 16/07

package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.PedidoRequest;
import com.deliverytech.delivery_api.dto.response.ItemPedidoResponse;
import com.deliverytech.delivery_api.dto.response.PedidoResponse;
import com.deliverytech.delivery_api.model.*;
import com.deliverytech.delivery_api.service.ClienteService;
import com.deliverytech.delivery_api.service.PedidoService;
import com.deliverytech.delivery_api.service.ProdutoService;
import com.deliverytech.delivery_api.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final RestauranteService restauranteService;
    private final ProdutoService produtoService;
    private final ModelMapper modelMapper;

    // 1. CRIAR PEDIDO (Simplificado - sem itens iniciais)
    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest request) {
        Cliente cliente = clienteService.buscarPorId(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        Restaurante restaurante = restauranteService.buscarPorId(request.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .restaurante(restaurante)
                .status(StatusPedido.CRIADO)
                .valorTotal(BigDecimal.ZERO) // Inicia com valor zero
                .enderecoEntrega(request.getEnderecoEntrega())
                .build();

        Pedido salvo = pedidoService.criar(pedido);
        
        return ResponseEntity.ok(new PedidoResponse(
                salvo.getId(),
                cliente.getId(),
                restaurante.getId(),
                salvo.getEnderecoEntrega(),
                salvo.getValorTotal(),
                salvo.getStatus(),
                salvo.getDataPedido(),
                List.of() // Lista vazia de itens inicialmente
        ));
    }

    // 2. BUSCAR PEDIDO POR ID
    @Transactional(readOnly = true) // ✅ ADICIONAR
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        List<ItemPedidoResponse> itensResp = pedido.getItens() != null ? 
            pedido.getItens().stream()
                .map(i -> new ItemPedidoResponse(i.getProduto().getId(), i.getProduto().getNome(), i.getQuantidade(), i.getPrecoUnitario()))
                .collect(Collectors.toList()) : List.of();

        return ResponseEntity.ok(new PedidoResponse(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getRestaurante().getId(),
                pedido.getEnderecoEntrega(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getDataPedido(),
                itensResp
        ));
    }

    // 3. BUSCAR PEDIDOS POR CLIENTE
    @Transactional(readOnly = true) // ✅ ADICIONAR
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> buscarPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.buscarPorCliente(clienteId);
        List<PedidoResponse> pedidosResp = pedidos.stream()
                .map(pedido -> new PedidoResponse(
                        pedido.getId(),
                        pedido.getCliente().getId(),
                        pedido.getRestaurante().getId(),
                        pedido.getEnderecoEntrega(),
                        pedido.getValorTotal(),
                        pedido.getStatus(),
                        pedido.getDataPedido(),
                        pedido.getItens() != null ? pedido.getItens().stream()
                                .map(i -> new ItemPedidoResponse(i.getProduto().getId(), i.getProduto().getNome(), i.getQuantidade(), i.getPrecoUnitario()))
                                .collect(Collectors.toList()) : List.of()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(pedidosResp);
    }

    //  4. ADICIONAR ITEM AO PEDIDO (IMPLEMENTAR)
    @Transactional 
    @PostMapping("/{pedidoId}/itens")
    public ResponseEntity<PedidoResponse> adicionarItem(@PathVariable Long pedidoId,
                                                       @RequestParam Long produtoId,
                                                       @RequestParam Integer quantidade) {
        Pedido pedido = pedidoService.buscarPorId(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        
        if (pedido.getStatus() != StatusPedido.CRIADO) {
            throw new RuntimeException("Não é possível adicionar itens a um pedido confirmado");
        }

        Produto produto = produtoService.buscarPorId(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Pedido pedidoAtualizado = pedidoService.adicionarItem(pedidoId, produtoId, quantidade);
        
        List<ItemPedidoResponse> itensResp = pedidoAtualizado.getItens().stream()
                .map(i -> new ItemPedidoResponse(i.getProduto().getId(), i.getProduto().getNome(), i.getQuantidade(), i.getPrecoUnitario()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PedidoResponse(
                pedidoAtualizado.getId(),
                pedidoAtualizado.getCliente().getId(),
                pedidoAtualizado.getRestaurante().getId(),
                pedidoAtualizado.getEnderecoEntrega(),
                pedidoAtualizado.getValorTotal(),
                pedidoAtualizado.getStatus(),
                pedidoAtualizado.getDataPedido(),
                itensResp
        ));
    }

    // 5. CONFIRMAR PEDIDO (IMPLEMENTAR)
    @Transactional
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<PedidoResponse> confirmar(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        
        if (pedido.getStatus() != StatusPedido.CRIADO) {
            throw new RuntimeException("Pedido já foi confirmado ou cancelado");
        }

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new RuntimeException("Não é possível confirmar um pedido sem itens");
        }

        Pedido pedidoConfirmado = pedidoService.confirmar(id);
        
        List<ItemPedidoResponse> itensResp = pedidoConfirmado.getItens().stream()
                .map(i -> new ItemPedidoResponse(i.getProduto().getId(), i.getProduto().getNome(), i.getQuantidade(), i.getPrecoUnitario()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PedidoResponse(
                pedidoConfirmado.getId(),
                pedidoConfirmado.getCliente().getId(),
                pedidoConfirmado.getRestaurante().getId(),
                pedidoConfirmado.getEnderecoEntrega(),
                pedidoConfirmado.getValorTotal(),
                pedidoConfirmado.getStatus(),
                pedidoConfirmado.getDataPedido(),
                itensResp
        ));
    }

    // 6. ATUALIZAR STATUS DO PEDIDO (IMPLEMENTAR)
    @Transactional 
    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoResponse> atualizarStatus(@PathVariable Long id,
                                                         @RequestParam StatusPedido status) {
        Pedido pedido = pedidoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        Pedido pedidoAtualizado = pedidoService.atualizarStatus(id, status);
        
        List<ItemPedidoResponse> itensResp = pedidoAtualizado.getItens() != null ? 
            pedidoAtualizado.getItens().stream()
                .map(i -> new ItemPedidoResponse(i.getProduto().getId(), i.getProduto().getNome(), i.getQuantidade(), i.getPrecoUnitario()))
                .collect(Collectors.toList()) : List.of();

        return ResponseEntity.ok(new PedidoResponse(
                pedidoAtualizado.getId(),
                pedidoAtualizado.getCliente().getId(),
                pedidoAtualizado.getRestaurante().getId(),
                pedidoAtualizado.getEnderecoEntrega(),
                pedidoAtualizado.getValorTotal(),
                pedidoAtualizado.getStatus(),
                pedidoAtualizado.getDataPedido(),
                itensResp
        ));
    }

    // 7. CANCELAR PEDIDO (NOVO)
     @Transactional 
    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id) {
        Pedido pedidoCancelado = pedidoService.cancelar(id);
        
        // Retornar o pedido cancelado como resposta
        return ResponseEntity.ok(new PedidoResponse(
                pedidoCancelado.getId(),
                pedidoCancelado.getCliente().getId(),
                pedidoCancelado.getRestaurante().getId(),
                pedidoCancelado.getEnderecoEntrega(),
                pedidoCancelado.getValorTotal(),
                pedidoCancelado.getStatus(), // ✅ Agora será CANCELADO
                pedidoCancelado.getDataPedido(),
                List.of() // ou mapear os itens se necessário
        ));
    }

    // ✅ ADICIONAR: Endpoint para listar todos os pedidos
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        List<Pedido> pedidos = pedidoService.listarTodos();
        List<PedidoResponse> pedidosResp = pedidos.stream()
                .map(pedido -> new PedidoResponse(
                        pedido.getId(),
                        pedido.getCliente().getId(),
                        pedido.getRestaurante().getId(),
                        pedido.getEnderecoEntrega(),
                        pedido.getValorTotal(),
                        pedido.getStatus(),
                        pedido.getDataPedido(),
                        pedido.getItens() != null ? pedido.getItens().stream()
                                .map(i -> new ItemPedidoResponse(i.getProduto().getId(), i.getProduto().getNome(), i.getQuantidade(), i.getPrecoUnitario()))
                                .collect(Collectors.toList()) : List.of()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(pedidosResp);
    }

    /**
     * Converter Pedido para PedidoResponse usando ModelMapper
     */
    private PedidoResponse convertToPedidoResponse(Pedido pedido) {
        PedidoResponse response = modelMapper.map(pedido, PedidoResponse.class);
        
        // Mapear itens manualmente (relacionamento complexo)
        if (pedido.getItens() != null) {
            List<ItemPedidoResponse> itensResp = pedido.getItens().stream()
                .map(item -> modelMapper.map(item, ItemPedidoResponse.class))
                .collect(Collectors.toList());
            response.setItens(itensResp);
        }
        
        return response;
    }
}
