package com.deliverytech.delivery_api.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(
    description = "Resposta de erro padronizada com a RFC 7807",
    title = "Resposta de erro padronizada")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "Data e hora do erro", example = "2025-07-14T21:41:27")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    @Schema(description = "Código HTTP do erro", example = "400")
    private final int status;

    @Schema(description = "Tipo do erro")
    private final String error;

    @Schema(description = "Mensagem explicando o erro")
    private final String message;

    @Schema(description = "Caminho da requisição")
    private final String path;

    @Schema(description = "Detalhes adicionais")
    private final Map<String, String> details;

    public ErrorResponse(int status, String error, String message, String path,
            Map<String, String> details) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, path, null);
    }
}
