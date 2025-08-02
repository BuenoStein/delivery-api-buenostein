package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// Implementa a interface para validar a anotação @validCEP
// Valida strings que representam CEPs (códigos postais)
public class CEPValidator implements ConstraintValidator<ValidCEP, String> {

    // Método que contém a regra de validação
    // 'value' é o valor do campo anotado com @validCEP
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // Se o valor for nulo ou vazio, considera válido
        // Porque a anotação @NotNull deve ser usada para validar presença
        if (value == null || value.isEmpty()) {
            return true;
        }

        // Valida se o CEP tem o formato "00000-000" ou "00000000"
        // Usando regex para corresponder a cinco dígitos, hífen, três dígitos 
        // ou oito dígitos seguidos sem hífen
        return value.matches("\\d{5}-\\d{3}") || value.matches("\\d{8}");
    }
}
