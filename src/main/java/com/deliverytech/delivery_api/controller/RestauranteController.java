//mudança 15/07

package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.RestauranteRequest;
import com.deliverytech.delivery_api.dto.response.RestauranteResponse;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.deliverytech.delivery_api.dto.response.ProdutoResponse;
import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.service.ProdutoService;

@RestController
@RequestMapping("/api/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteService restauranteService;
    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<RestauranteResponse> cadastrar(@Valid @RequestBody RestauranteRequest request) {
        Restaurante salvo = restauranteService.cadastrar(request);

        return ResponseEntity.status(201).body(new RestauranteResponse(
                salvo.getId(), salvo.getNome(), salvo.getCategoria(), salvo.getTelefone(),
                salvo.getTaxaEntrega(), salvo.getTempoEntregaMinutos(), salvo.getAtivo()));
    }

    @GetMapping
    public ResponseEntity<List<RestauranteResponse>> listarTodos(
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) Boolean ativo) {
        
        List<Restaurante> restaurantes = restauranteService.listarComFiltros(categoria, ativo);
        
        List<RestauranteResponse> response = restaurantes.stream()
            .map(r -> new RestauranteResponse(r.getId(), r.getNome(), r.getCategoria(), 
                r.getTelefone(), r.getTaxaEntrega(), r.getTempoEntregaMinutos(), r.getAtivo()))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponse> buscarPorId(@PathVariable Long id) {
        return restauranteService.buscarPorId(id)
                .map(r -> new RestauranteResponse(r.getId(), r.getNome(), r.getCategoria(), r.getTelefone(), r.getTaxaEntrega(), r.getTempoEntregaMinutos(), r.getAtivo()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoria}")
    public List<RestauranteResponse> buscarPorCategoria(@PathVariable String categoria) {
        return restauranteService.buscarPorCategoria(categoria).stream()
                .map(r -> new RestauranteResponse(r.getId(), r.getNome(), r.getCategoria(), r.getTelefone(), r.getTaxaEntrega(), r.getTempoEntregaMinutos(), r.getAtivo()))
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponse> atualizar(@PathVariable Long id, @Valid @RequestBody RestauranteRequest request) {
        Restaurante salvo = restauranteService.atualizar(id, request);
        
        return ResponseEntity.ok(new RestauranteResponse(
                salvo.getId(), salvo.getNome(), salvo.getCategoria(), salvo.getTelefone(), 
                salvo.getTaxaEntrega(), salvo.getTempoEntregaMinutos(), salvo.getAtivo()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        restauranteService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Buscar restaurantes disponíveis (ativos)
     * GET /api/restaurantes/disponiveis
     */
    @GetMapping("/disponiveis")
    public List<RestauranteResponse> buscarDisponiveis() {
        return restauranteService.listarAtivos().stream()
                .map(r -> new RestauranteResponse(
                    r.getId(), r.getNome(), r.getCategoria(), r.getTelefone(), 
                    r.getTaxaEntrega(), r.getTempoEntregaMinutos(), r.getAtivo()))
                .collect(Collectors.toList());
    }

    /**
     * Calcular taxa de entrega para um CEP
     * POST /api/restaurantes/{id}/taxa-entrega
     */
    @PostMapping("/{id}/taxa-entrega")
    public ResponseEntity<?> calcularTaxaEntrega(@PathVariable Long id, 
                                               @RequestBody Map<String, String> request) {
        try {
            String cep = request.get("cep");
            if (cep == null || cep.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("erro", "CEP é obrigatório"));
            }
            
            BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
            
            return ResponseEntity.ok(Map.of(
                "restauranteId", id,
                "cep", cep,
                "taxaEntrega", taxa,
                "moeda", "BRL"
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Buscar produtos de um restaurante
     * GET /api/restaurantes/{id}/produtos
     */
    @GetMapping("/{id}/produtos")
    public ResponseEntity<List<ProdutoResponse>> buscarProdutosPorRestaurante(@PathVariable Long id) {
        // Verificar se restaurante existe
        restauranteService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));
        
        // Buscar produtos do restaurante
        List<Produto> produtos = produtoService.buscarPorRestaurante(id);
        
        // Converter para Response
        List<ProdutoResponse> response = produtos.stream()
            .map(p -> new ProdutoResponse(
                p.getId(), 
                p.getNome(), 
                p.getCategoria(), 
                p.getDescricao(), 
                p.getPreco(), 
                p.getDisponivel()))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Ativar/desativar restaurante
     * PATCH /api/restaurantes/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<RestauranteResponse> alterarStatus(@PathVariable Long id, 
                                                            @RequestBody Map<String, Boolean> request) {
        Boolean ativo = request.get("ativo");
        Restaurante restaurante = restauranteService.alterarStatus(id, ativo);
        
        return ResponseEntity.ok(new RestauranteResponse(
            restaurante.getId(), restaurante.getNome(), restaurante.getCategoria(), 
            restaurante.getTelefone(), restaurante.getTaxaEntrega(), 
            restaurante.getTempoEntregaMinutos(), restaurante.getAtivo()));
    }

    /**
     * Calcular taxa de entrega por CEP
     * GET /api/restaurantes/{id}/taxa-entrega/{cep}
     */
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<Map<String, Object>> calcularTaxaEntregaPorCep(@PathVariable Long id, 
                                                                         @PathVariable String cep) {
        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
        
        return ResponseEntity.ok(Map.of(
            "restauranteId", id,
            "cep", cep,
            "taxaEntrega", taxa,
            "moeda", "BRL"
        ));
    }

    /**
     * Buscar restaurantes próximos por CEP
     * GET /api/restaurantes/proximos/{cep}
     */
    @GetMapping("/proximos/{cep}")
    public ResponseEntity<List<RestauranteResponse>> buscarProximos(@PathVariable String cep) {
        List<Restaurante> restaurantes = restauranteService.buscarProximos(cep);
        
        List<RestauranteResponse> response = restaurantes.stream()
            .map(r -> new RestauranteResponse(
                r.getId(), r.getNome(), r.getCategoria(), r.getTelefone(),
                r.getTaxaEntrega(), r.getTempoEntregaMinutos(), r.getAtivo()))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
}
