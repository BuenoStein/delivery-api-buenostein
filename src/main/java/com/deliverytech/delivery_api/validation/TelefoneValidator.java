package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isEmpty()) {
            return true;
        }

        // Aceita os formatos:
        // (99) 99999-9999 ou 1199999999
        return value.matches("\\(\\d{2}\\)\\s\\d{5}-\\d{4}") || value.matches("\\d{11}");
    }
}
