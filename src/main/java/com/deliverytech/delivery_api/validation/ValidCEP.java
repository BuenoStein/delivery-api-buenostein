package com.deliverytech.delivery_api.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

// Indica que essa anotação será incluída na documentação Java (Javadoc) 
@Documented

// Define que essa anotação é uma restrição de validação
// A lógica de validação será implementada na classe CEPValidator
@Constraint(validatedBy = CEPValidator.class)

// Especifica que essa anotação pode ser usada somente em campos (atributos)
@Target({ElementType.FIELD})

// Define que essa anotação será mantida em tempo de execução (reflexão)
@Retention(RetentionPolicy.RUNTIME)

// Definição da anotação personalizada @ValidCEP
public @interface ValidCEP {
     // Mensagem padrão que será exibida caso a validação falhe
    String message() default "CEP inválido";
    
    // Permite agrupar validações para diferentes cenários
    Class<?>[] groups() default {};

    // Payload customizado para transportar metadados de validação
    Class<? extends Payload>[] payload() default {};
}
