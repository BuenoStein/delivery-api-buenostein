package com.deliverytech.delivery_api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tratamento global de exceções para toda a aplicação
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Tratamento para validações de Bean Validation (@NotBlank, @Email, etc.)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("error", "Dados inválidos");
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        
        errors.put("validationErrors", fieldErrors);
        
        log.warn("Erro de validação: {}", fieldErrors);
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Tratamento para argumentos ilegais (cliente não encontrado, etc.)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Requisição inválida");
        error.put("message", ex.getMessage());
        
        log.warn("Erro de argumento ilegal: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Tratamento para recursos não encontrados
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex) {
        
        // Se for "não encontrado", retornar 404
        if (ex.getMessage().contains("não encontrado") || 
            ex.getMessage().contains("not found")) {
            
            Map<String, Object> error = new HashMap<>();
            error.put("timestamp", LocalDateTime.now());
            error.put("status", HttpStatus.NOT_FOUND.value());
            error.put("error", "Recurso não encontrado");
            error.put("message", ex.getMessage());
            
            log.warn("Recurso não encontrado: {}", ex.getMessage());
            return ResponseEntity.notFound().build();
        }
        
        // Outros RuntimeExceptions
        return handleGenericException(ex);
    }

    /**
     * Tratamento para violações de constraint
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Violação de restrição");
        error.put("message", ex.getMessage());
        
        log.warn("Violação de constraint: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Tratamento genérico para exceções não tratadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex) {
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Erro interno do servidor");
        error.put("message", "Ocorreu um erro inesperado");
        
        log.error("Erro interno: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
