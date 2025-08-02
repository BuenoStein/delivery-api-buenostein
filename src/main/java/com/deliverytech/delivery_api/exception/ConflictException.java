package com.deliverytech.delivery_api.exception;

public class ConflictException extends BusinessException {
    
    public ConflictException(String entity, String field) {
        super(
            String.format("%s com %s já existe", entity, field),
            "entity.conflict"
        );
    }
}
