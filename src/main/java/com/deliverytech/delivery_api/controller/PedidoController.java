// mudança 16/07

package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.PedidoRequest;
import com.deliverytech.delivery_api.dto.request.StatusUpdateRequest;
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final RestauranteService restauranteService;
    private final ProdutoService produtoService;
    private final ModelMapper modelMapper = new ModelMapper();

    {
        // Ignorar o campo 'itens' ao mapear Pedido -> PedidoResponse
        modelMapper.typeMap(Pedido.class, PedidoResponse.class)
            .addMappings(mapper -> mapper.skip(PedidoResponse::setItens));
    }

    // 1. CRIAR PEDIDO (Simplificado - sem itens iniciais)
    @Transactional
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
        
        return ResponseEntity.status(201).body(new PedidoResponse(
                salvo.getId(),
                cliente.getId(),
                restaurante.getId(),
                salvo.getEnderecoEntrega(),
                salvo.getValorTotal(),
                salvo.getStatus(),
                salvo.getDataPedido(),
                List.of()
        ));
    }

    // 2. BUSCAR PEDIDO POR ID
    @Transactional(readOnly = true) // ✅ ADICIONAR
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        if (pedido == null) {
            throw new RuntimeException("Pedido não encontrado");
        }

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
        Produto produto = produtoService.buscarPorId(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Pedido pedidoAtualizado = pedidoService.adicionarItem(pedidoId, produtoId, quantidade);

        // Se necessário, você pode validar o status do pedido dentro do serviço adicionarItem
        // ou lançar uma exceção apropriada se não for permitido adicionar itens.

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
        Pedido pedido = pedidoService.buscarPorId(id);
        if (pedido == null) {
            throw new RuntimeException("Pedido não encontrado");
        }
        
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

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pedidoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ ADICIONAR: Endpoint para listar todos os pedidos
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<PedidoResponse>> listarTodos(
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        List<Pedido> pedidos = pedidoService.listarComFiltros(status, dataInicio, dataFim);
        List<PedidoResponse> pedidosResp = pedidos.stream()
                .map(this::convertToPedidoResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pedidosResp);
    }

    /**
     * Converter Pedido para PedidoResponse usando ModelMapper
     */
    private PedidoResponse convertToPedidoResponse(Pedido pedido) {
        PedidoResponse response = modelMapper.map(pedido, PedidoResponse.class);

        // Evitar que o ModelMapper tente mapear a coleção lazy
        response.setItens(null);

        // Mapear itens manualmente (relacionamento complexo)
        if (pedido.getItens() != null) {
            List<ItemPedidoResponse> itensResp = pedido.getItens().stream()
                .map(item -> modelMapper.map(item, ItemPedidoResponse.class))
                .collect(Collectors.toList());
            response.setItens(itensResp);
        }

        return response;
    }

    /**
     * Atualizar status do pedido
     * PATCH /api/pedidos/{id}/status
     */
    @Transactional
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponse> atualizarStatus(@PathVariable Long id,
                                                     @Valid @RequestBody StatusUpdateRequest request) {
        try {
            // Extrair status do DTO
            String statusStr = request.getStatus();
            
            // Converter string para enum
            StatusPedido status;
            try {
                status = StatusPedido.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Status inválido: " + statusStr);
            }
            
            // ADICIONAR APENAS ESTAS 4 LINHAS:
            Pedido pedido = pedidoService.buscarPorId(id);
            if (pedido == null) {
                throw new RuntimeException("Pedido não encontrado");
            }
            
            validarTransicaoStatus(pedido.getStatus(), status);
            // FIM DA ADIÇÃO
            
            // Atualizar status
            Pedido pedidoAtualizado = pedidoService.atualizarStatus(id, status);
            return ResponseEntity.ok(convertToPedidoResponse(pedidoAtualizado));
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage());
        }
    }

    /**
     * Validar se a transição de status é válida
     */
    private void validarTransicaoStatus(StatusPedido statusAtual, StatusPedido novoStatus) {
        // Regras de negócio para transição de status
        switch (statusAtual) {
            case CRIADO:
                if (novoStatus != StatusPedido.CONFIRMADO && novoStatus != StatusPedido.CANCELADO) {
                    throw new IllegalArgumentException("De CRIADO só pode ir para CONFIRMADO ou CANCELADO");
                }
                break;
            case CONFIRMADO:
                if (novoStatus != StatusPedido.PREPARANDO && novoStatus != StatusPedido.CANCELADO) {
                    throw new IllegalArgumentException("De CONFIRMADO só pode ir para PREPARANDO ou CANCELADO");
                }
                break;
            case PREPARANDO:
                if (novoStatus != StatusPedido.SAIU_PARA_ENTREGA && novoStatus != StatusPedido.CANCELADO) {
                    throw new IllegalArgumentException("De PREPARANDO só pode ir para SAIU_PARA_ENTREGA ou CANCELADO");
                }
                break;
            case SAIU_PARA_ENTREGA:
                if (novoStatus != StatusPedido.ENTREGUE) {
                    throw new IllegalArgumentException("De SAIU_PARA_ENTREGA só pode ir para ENTREGUE");
                }
                break;
            case ENTREGUE:
            case CANCELADO:
                throw new IllegalArgumentException("Pedido já está em status final: " + statusAtual);
            default:
                throw new IllegalArgumentException("Status atual inválido: " + statusAtual);
        }
    }

    /**
     * Listar pedidos de um restaurante
     * GET /api/restaurantes/{restauranteId}/pedidos
     */
    @GetMapping("/restaurante/{restauranteId}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PedidoResponse>> buscarPorRestaurante(@PathVariable Long restauranteId) {
        List<Pedido> pedidos = pedidoService.buscarPorRestaurante(restauranteId);
        List<PedidoResponse> pedidosResp = pedidos.stream()
                .map(this::convertToPedidoResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pedidosResp);
    }

    /**
     * Calcular total do pedido sem salvar
     * POST /api/pedidos/calcular
     */
    @PostMapping("/calcular")
    public ResponseEntity<Map<String, Object>> calcularTotal(@Valid @RequestBody PedidoRequest request) {
        // Buscar restaurante para obter taxa de entrega
        Restaurante restaurante = restauranteService.buscarPorId(request.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));
        
        BigDecimal subtotal = BigDecimal.ZERO;
        
        // Calcular subtotal dos itens (se houver)
        if (request.getItens() != null && !request.getItens().isEmpty()) {
            for (var itemRequest : request.getItens()) {
                Produto produto = produtoService.buscarPorId(itemRequest.getProdutoId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemRequest.getProdutoId()));
                
                BigDecimal valorItem = produto.getPreco()
                        .multiply(BigDecimal.valueOf(itemRequest.getQuantidade()));
                subtotal = subtotal.add(valorItem);
            }
        }
        
        BigDecimal taxaEntrega = restaurante.getTaxaEntrega();
        BigDecimal valorTotal = subtotal.add(taxaEntrega);
        
        return ResponseEntity.ok(Map.of(
            "subtotal", subtotal,
            "taxaEntrega", taxaEntrega,
            "valorTotal", valorTotal,
            "restaurante", restaurante.getNome(),
            "moeda", "BRL"
        ));
    }
}
