package com.deliverytech.delivery_api.service.impl;

import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.dto.request.ClienteRequest; // ADICIONAR IMPORT
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    /**
         * Cadastrar novo cliente com validações completas
         */
        @Override
        public Cliente cadastrar(ClienteRequest clienteRequest) {
            log.info("Iniciando cadastro de cliente: {}", clienteRequest.getEmail());
            
            // Converter ClienteRequest para Cliente
            Cliente cliente = new Cliente();
            cliente.setNome(clienteRequest.getNome());
            cliente.setEmail(clienteRequest.getEmail());
            cliente.setTelefone(clienteRequest.getTelefone());
            cliente.setEndereco(clienteRequest.getEndereco()); // <-----
            
            // Validar email único
            if (clienteRepository.existsByEmail(cliente.getEmail())) {
                throw new IllegalArgumentException("Email já cadastrado: " + cliente.getEmail());
            }
    
            // Validações de negócio
            validarDadosCliente(cliente);
    
            // Definir como ativo por padrão
            cliente.setAtivo(true);
    
            Cliente clienteSalvo = clienteRepository.save(cliente);
            log.info("Cliente cadastrado com sucesso - ID: {}", clienteSalvo.getId());
            
            return clienteSalvo;
        }
    
        /**
         * Método auxiliar para cadastrar cliente a partir de uma entidade Cliente.
         */

    /**
     * Buscar cliente por ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        log.debug("Buscando cliente por ID: {}", id);
        return clienteRepository.findById(id);
    }

    /**
     * Buscar cliente por email
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorEmail(String email) {
        log.debug("Buscando cliente por email: {}", email);
        return clienteRepository.findByEmail(email);
    }

    /**
     * Listar todos os clientes ativos
     */
    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarAtivos() {
        log.debug("Listando clientes ativos");
        return clienteRepository.findByAtivoTrue();
    }

    /**
     * Buscar clientes por nome
     */
    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String nome) {
        log.debug("Buscando clientes por nome: {}", nome);
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Atualizar dados do cliente COM TODAS AS VALIDAÇÕES
     */
    @Override
    public Cliente atualizar(Long id, ClienteRequest clienteRequest) { // CORRIGIR: Cliente → ClienteRequest
        log.info("Atualizando cliente ID: {}", id);
        
        Cliente cliente = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        // Verificar se email não está sendo usado por outro cliente
        if (!cliente.getEmail().equals(clienteRequest.getEmail()) && // CORRIGIR: clienteAtualizado → clienteRequest
            clienteRepository.existsByEmail(clienteRequest.getEmail())) { // CORRIGIR
            throw new IllegalArgumentException("Email já cadastrado: " + clienteRequest.getEmail()); // CORRIGIR
        }

        // Criar cliente temporário para validação
        Cliente clienteParaValidacao = new Cliente();
        clienteParaValidacao.setNome(clienteRequest.getNome());
        clienteParaValidacao.setEmail(clienteRequest.getEmail());
        clienteParaValidacao.setTelefone(clienteRequest.getTelefone());
        clienteParaValidacao.setEndereco(clienteRequest.getEndereco());

        // Validar dados atualizados
        validarDadosCliente(clienteParaValidacao);

        // Atualizar campos
        cliente.setNome(clienteRequest.getNome()); // CORRIGIR
        cliente.setEmail(clienteRequest.getEmail()); // CORRIGIR
        cliente.setTelefone(clienteRequest.getTelefone()); // CORRIGIR
        cliente.setEndereco(clienteRequest.getEndereco()); // CORRIGIR

        Cliente clienteSalvo = clienteRepository.save(cliente);
        log.info("Cliente atualizado com sucesso - ID: {}", clienteSalvo.getId());
        
        return clienteSalvo;
    }

    /**
     * Inativar cliente (soft delete) COM VALIDAÇÃO
     */
    @Override
    public void inativar(Long id) {
        log.info("Inativando cliente ID: {}", id);
        
        Cliente cliente = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        // Verificar se cliente já está inativo
        if (!cliente.getAtivo()) {
            throw new IllegalArgumentException("Cliente já está inativo: " + id);
        }

        // Usar método da entidade se disponível, senão usar setter
        try {
            cliente.inativar();
        } catch (Exception e) {
            cliente.setAtivo(false);
        }
        
        clienteRepository.save(cliente);
        log.info("Cliente inativado com sucesso - ID: {}", id);
    }

    /**
     * Ativar/Desativar cliente (toggle status ativo)
     * NOVO MÉTODO para atender requisito da atividade
     */
    @Override
    public Cliente ativarDesativarCliente(Long id) {
        log.info("Alterando status do cliente ID: {}", id);
        
        Cliente cliente = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        // Toggle do status
        cliente.setAtivo(!cliente.getAtivo());
        
        Cliente clienteSalvo = clienteRepository.save(cliente);
        log.info("Status do cliente alterado para: {} - ID: {}", clienteSalvo.getAtivo(), id);
        
        return clienteSalvo;
    }

    /**
     * VALIDAÇÕES DE NEGÓCIO COMPLETAS (método privado)
     */
    private void validarDadosCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        if (cliente.getNome().length() < 2) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
        }

        if (cliente.getNome().length() > 100) {
            throw new IllegalArgumentException("Nome não pode ter mais de 100 caracteres");
        }

        if (!cliente.getEmail().contains("@") || !cliente.getEmail().contains(".")) {
            throw new IllegalArgumentException("Email deve ter formato válido");
        }

        if (cliente.getEmail().length() > 150) {
            throw new IllegalArgumentException("Email não pode ter mais de 150 caracteres");
        }

        log.debug("Validações de negócio aprovadas para cliente: {}", cliente.getEmail());
    }
}