package com.deliverytech.delivery_api.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.deliverytech.delivery_api.dto.response.PedidoResponse;
import com.deliverytech.delivery_api.model.Pedido;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        // Configuração para Pedido → PedidoResponse
        mapper.addMappings(new PropertyMap<Pedido, PedidoResponse>() {
            @Override
            protected void configure() {
                map().setClienteId(source.getCliente().getId());
                map().setRestauranteId(source.getRestaurante().getId());
            }
        });
        
        return mapper;
    }
}
