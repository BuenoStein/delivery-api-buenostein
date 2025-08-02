package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.dto.request.ClienteRequest; // ✅ ADICIONAR IMPORT
import com.deliverytech.delivery_api.dto.response.ApiResponseWrapper;
import com.deliverytech.delivery_api.service.ClienteService; // ✅ INTERFACE
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService; // ✅ INJEÇÃO DA INTERFACE

    /**
     * Cadastrar novo cliente
     * POST /clientes
     */
    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ClienteRequest clienteRequest) {
        try {
            log.info("Recebida requisição para cadastrar cliente: {}", clienteRequest.getEmail());
            Cliente clienteSalvo = clienteService.cadastrar(clienteRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação ao cadastrar cliente: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro interno ao cadastrar cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

    /**
     * Listar todos os clientes ativos
     * GET /clientes
     */
    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        log.info("Recebida requisição para listar clientes ativos");
        List<Cliente> clientes = clienteService.listarAtivos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Buscar cliente por ID
     * GET /clientes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Cliente>> buscarPorId(@PathVariable Long id) {
        log.info("Recebida requisição para buscar cliente ID: {}", id);
        Optional<Cliente> cliente = clienteService.buscarPorId(id);

        if (cliente.isPresent()) {
            return ResponseEntity.ok(ApiResponseWrapper.success(cliente.get(), "Cliente encontrado"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseWrapper.error("Cliente não encontrado"));
        }
    }

    /**
     * Buscar cliente por email
     * GET /clientes/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        log.info("Recebida requisição para buscar cliente por email: {}", email);
        Optional<Cliente> cliente = clienteService.buscarPorEmail(email);

        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Buscar clientes por nome
     * GET /clientes/buscar?nome=João
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorNome(@RequestParam String nome) {
        log.info("Recebida requisição para buscar clientes por nome: {}", nome);
        List<Cliente> clientes = clienteService.buscarPorNome(nome);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Atualizar cliente
     * PUT /api/clientes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                      @Valid @RequestBody ClienteRequest clienteRequest) { // ✅ ALTERAR: Cliente → ClienteRequest
        try {
            log.info("Recebida requisição para atualizar cliente ID: {}", id);
            Cliente clienteAtualizado = clienteService.atualizar(id, clienteRequest); // ✅ ALTERAR se o service suportar
            return ResponseEntity.ok(clienteAtualizado);
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação ao atualizar cliente: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro interno ao atualizar cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

    /**
     * Inativar cliente (soft delete)
     * DELETE /clientes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            log.info("Recebida requisição para inativar cliente ID: {}", id);
            clienteService.inativar(id);
            return ResponseEntity.ok().body("Cliente inativado com sucesso");
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao inativar cliente: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro interno ao inativar cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

    /**
     * Ativar/Desativar cliente (toggle status ativo)
     * PATCH /api/clientes/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> ativarDesativarCliente(@PathVariable Long id) {
        try {
            log.info("Recebida requisição para alterar status do cliente ID: {}", id);
            Cliente clienteAtualizado = clienteService.ativarDesativarCliente(id);
            
            String status = clienteAtualizado.getAtivo() ? "ativado" : "desativado";
            return ResponseEntity.ok()
                .body(Map.of(
                    "mensagem", "Cliente " + status + " com sucesso",
                    "cliente", clienteAtualizado
                ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao alterar status do cliente: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro interno ao alterar status do cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }
}