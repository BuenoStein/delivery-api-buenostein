package com.deliverytech.delivery_api.validation;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CategoriaValidator implements ConstraintValidator<ValidCategoria, String> {

    // Lista de categorias permitidas
    private static final String[] CATEGORIAS_PERMITIDAS = {
        "COMIDA CASEIRA",
        "FAST FOOD",
        "JAPONESA",
        "ITALIANA",
        "PIZZARIA",
        "VEGETARIANA",
        "SAUDÁVEL",
        "LANCHES",
        "DOCES E BOLOS",
        "CAFETERIA"
    };

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isEmpty()) {
            return true;
        }

        return Arrays.asList(CATEGORIAS_PERMITIDAS).contains(value.toUpperCase());
    }
}
