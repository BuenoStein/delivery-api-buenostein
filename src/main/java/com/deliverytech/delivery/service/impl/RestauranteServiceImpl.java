package com.deliverytech.delivery_api.service.impl;

import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.dto.request.RestauranteRequest; // ✅ ADICIONAR
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // ✅ ADICIONAR
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ ADICIONAR

import java.math.BigDecimal; // ✅ ADICIONAR
import java.util.List;
import java.util.Optional;

@Slf4j // ✅ ADICIONAR
@Service
@Transactional // ✅ ADICIONAR
@RequiredArgsConstructor
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteRepository restauranteRepository;

    @Override
    public Restaurante cadastrar(RestauranteRequest restauranteRequest) { // ✅ MUDAR PARÂMETRO
        log.info("Iniciando cadastro de restaurante: {}", restauranteRequest.getNome());
        
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(restauranteRequest.getNome());
        restaurante.setCategoria(restauranteRequest.getCategoria());
/*         restaurante.setEndereco(restauranteRequest.getEndereco()); */
        restaurante.setTaxaEntrega(restauranteRequest.getTaxaEntrega());
        restaurante.setTelefone(restauranteRequest.getTelefone());
/*         restaurante.setEmail(restauranteRequest.getEmail()); */
        restaurante.setTempoEntregaMinutos(restauranteRequest.getTempoEntregaMinutos()); // ✅ ADICIONAR
        restaurante.setAtivo(true);
        
        Restaurante salvo = restauranteRepository.save(restaurante);
        log.info("Restaurante cadastrado com sucesso - ID: {}", salvo.getId());
        
        return salvo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Restaurante> buscarPorId(Long id) {
        return restauranteRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> listarTodos() {
        return restauranteRepository.findAll();
    }

    // ✅ IMPLEMENTAR MÉTODO FALTANTE
    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> listarAtivos() {
        return restauranteRepository.findByAtivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoria(categoria);
    }

    // ✅ IMPLEMENTAR MÉTODO FALTANTE
    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorAvaliacao(BigDecimal minAvaliacao) {
        return restauranteRepository.findByAvaliacaoGreaterThanEqual(minAvaliacao);
    }

    // ✅ IMPLEMENTAR MÉTODO FALTANTE
    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorTaxaEntrega(BigDecimal maxTaxa) {
        return restauranteRepository.findByTaxaEntregaLessThanEqual(maxTaxa);
    }

    @Override
    public Restaurante atualizar(Long id, RestauranteRequest atualizado) {
        return restauranteRepository.findById(id)
            .map(r -> {
                r.setNome(atualizado.getNome());
                r.setTelefone(atualizado.getTelefone());
                r.setCategoria(atualizado.getCategoria());
                r.setTaxaEntrega(atualizado.getTaxaEntrega());
                r.setTempoEntregaMinutos(atualizado.getTempoEntregaMinutos());
                return restauranteRepository.save(r);
            }).orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));
    }

    // ✅ IMPLEMENTAR MÉTODO FALTANTE
    @Override
    public void inativar(Long id) {
        restauranteRepository.findById(id)
            .ifPresentOrElse(
                restaurante -> {
                    restaurante.setAtivo(false);
                    restauranteRepository.save(restaurante);
                    log.info("Restaurante inativado - ID: {}", id);
                },
                () -> {
                    throw new RuntimeException("Restaurante não encontrado - ID: " + id);
                }
            );
    }

    /**
     * Calcular taxa de entrega baseada no restaurante e CEP
     * Lógica simplificada para demonstração
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        log.info("Calculando taxa de entrega - Restaurante ID: {}, CEP: {}", restauranteId, cep);
        
        // Buscar restaurante
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new RuntimeException("Restaurante não encontrado - ID: " + restauranteId));
        
        // Verificar se restaurante está ativo
        if (!restaurante.getAtivo()) {
            throw new RuntimeException("Restaurante não está disponível para entrega");
        }
        
        // Lógica simplificada de cálculo baseada no CEP
        BigDecimal taxaBase = restaurante.getTaxaEntrega();
        
        // Simular cálculo por região do CEP
        String primeirosDigitos = cep.substring(0, Math.min(2, cep.length()));
        
        try {
            int codigoRegiao = Integer.parseInt(primeirosDigitos);
            
            // Lógica de exemplo:
            // CEP 01xxx-xxx (centro) = taxa normal
            // CEP 02xxx-xxx a 05xxx-xxx = taxa + 20%
            // CEP 06xxx-xxx a 09xxx-xxx = taxa + 50%
            // Outros = taxa + 100%
            
            BigDecimal multiplicador;
            if (codigoRegiao == 1) {
                multiplicador = BigDecimal.ONE; // Taxa normal
            } else if (codigoRegiao >= 2 && codigoRegiao <= 5) {
                multiplicador = new BigDecimal("1.20"); // +20%
            } else if (codigoRegiao >= 6 && codigoRegiao <= 9) {
                multiplicador = new BigDecimal("1.50"); // +50%
            } else {
                multiplicador = new BigDecimal("2.00"); // +100%
            }
            
            BigDecimal taxaFinal = taxaBase.multiply(multiplicador).setScale(2, BigDecimal.ROUND_HALF_UP);
            
            log.info("Taxa calculada: R$ {} (base: R$ {}, multiplicador: {})", 
                    taxaFinal, taxaBase, multiplicador);
            
            return taxaFinal;
            
        } catch (NumberFormatException e) {
            log.warn("CEP inválido: {}, usando taxa base", cep);
            return taxaBase;
        }
    }
}
