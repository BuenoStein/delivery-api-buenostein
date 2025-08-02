package com.deliverytech.delivery_api.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import com.deliverytech.delivery_api.repository.ClienteRepository;

@Component("client")
public class ClientHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(ClientHealthIndicator.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Health health() {
        try {
            long totalClientes = clienteRepository.count();
            return Health.up()
            .withDetail("total-clientes", totalClientes)
            .withDetail("status-servico", "Serviço de clientes.")
            .build();
        } catch (Exception e) {
            logger.error("Erro ao verificar a saúde do serviço de clientes.");
            return Health.down(e).withDetail("error", e.getMessage()).build();
        }
    }

}
