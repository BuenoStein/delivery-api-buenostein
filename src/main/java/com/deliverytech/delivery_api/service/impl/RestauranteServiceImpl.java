package com.deliverytech.delivery_api.service.impl;

import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.dto.request.RestauranteRequest;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Slf4j 
@Service
@Transactional
@RequiredArgsConstructor
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteRepository restauranteRepository;

    @Override
    public Restaurante cadastrar(RestauranteRequest restauranteRequest) {
        log.info("Iniciando cadastro de restaurante: {}", restauranteRequest.getNome());
        
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(restauranteRequest.getNome());
        restaurante.setCategoria(restauranteRequest.getCategoria());
/*         restaurante.setEndereco(restauranteRequest.getEndereco()); */
        restaurante.setTaxaEntrega(restauranteRequest.getTaxaEntrega());
        restaurante.setTelefone(restauranteRequest.getTelefone());
/*         restaurante.setEmail(restauranteRequest.getEmail()); */
        restaurante.setTempoEntregaMinutos(restauranteRequest.getTempoEntregaMinutos()); 
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

    /**
     * Alterar status ativo/inativo do restaurante
     */
    @Override
    public Restaurante alterarStatus(Long id, Boolean ativo) {
        log.info("Alterando status do restaurante ID: {} para: {}", id, ativo);
        
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurante não encontrado - ID: " + id));
        
        restaurante.setAtivo(ativo);
        Restaurante salvo = restauranteRepository.save(restaurante);
        
        log.info("Status do restaurante {} alterado para: {}", id, ativo);
        return salvo;
    }

    /**
     * Buscar restaurantes próximos por CEP
     * Lógica simplificada baseada nos primeiros dígitos do CEP
     */
    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> buscarProximos(String cep) {
        log.info("Buscando restaurantes próximos ao CEP: {}", cep);
        
        // Lógica simplificada: considera próximos os restaurantes ativos
        // Em um cenário real, seria feita integração com API de mapas
        List<Restaurante> restaurantesAtivos = restauranteRepository.findByAtivoTrue();
        
        // Simular proximidade baseada no CEP
        String primeirosDigitos = cep.substring(0, Math.min(2, cep.length()));
        
        try {
            int codigoRegiao = Integer.parseInt(primeirosDigitos);
            
            // Filtrar restaurantes "próximos" baseado na região do CEP
            // Para demonstração, considera próximo se código da região for <= 5
            if (codigoRegiao <= 5) {
                log.info("Encontrados {} restaurantes próximos ao CEP {}", restaurantesAtivos.size(), cep);
                return restaurantesAtivos;
            } else {
                // Para regiões mais distantes, retorna apenas restaurantes com taxa <= 10.00
                List<Restaurante> restaurantesProximos = restaurantesAtivos.stream()
                    .filter(r -> r.getTaxaEntrega().compareTo(new BigDecimal("10.00")) <= 0)
                    .toList();
                
                log.info("Encontrados {} restaurantes próximos ao CEP {} (região distante)", 
                        restaurantesProximos.size(), cep);
                return restaurantesProximos;
            }
            
        } catch (NumberFormatException e) {
            log.warn("CEP inválido: {}, retornando todos os restaurantes ativos", cep);
            return restaurantesAtivos;
        }
    }

    /**
     * Listar restaurantes com filtros opcionais
     */
    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> listarComFiltros(String categoria, Boolean ativo) {
        log.info("Listando restaurantes com filtros - Categoria: {}, Ativo: {}", categoria, ativo);
        
        // Se nenhum filtro foi fornecido, retorna todos
        if (categoria == null && ativo == null) {
            return restauranteRepository.findAll();
        }
        
        // Se apenas categoria foi fornecida
        if (categoria != null && ativo == null) {
            return restauranteRepository.findByCategoria(categoria);
        }
        
        // Se apenas status ativo foi fornecido
        if (categoria == null && ativo != null) {
            return ativo ? restauranteRepository.findByAtivoTrue() 
                         : restauranteRepository.findByAtivoFalse();
        }
        
        // Se ambos os filtros foram fornecidos
        return restauranteRepository.findByCategoriaAndAtivo(categoria, ativo);
    }
}
