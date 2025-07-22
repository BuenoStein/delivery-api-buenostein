package com.deliverytech.delivery_api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseCaptureFilter implements Filter {

    private final ObjectMapper objectMapper;

    public ResponseCaptureFilter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // ✅ Só processar APIs (não arquivos estáticos)
        if (!httpRequest.getRequestURI().startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ Wrapper para capturar request e response
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

        try {
            // ✅ Executar a requisição
            chain.doFilter(requestWrapper, responseWrapper);

            // ✅ Capturar e salvar a resposta
            capturarResposta(requestWrapper, responseWrapper);

        } finally {
            // ✅ IMPORTANTE: Copiar response de volta para o cliente
            responseWrapper.copyBodyToResponse();
        }
    }

    private void capturarResposta(ContentCachingRequestWrapper request,
                                 ContentCachingResponseWrapper response) {
        try {
            // ✅ Criar diretório se não existir
            File dir = new File("entregaveis");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // ✅ Gerar nome do arquivo
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String method = request.getMethod();
            String endpoint = request.getRequestURI()
                    .replace("/api/", "")
                    .replace("/", "_");
            
            String fileName = String.format("%s_%s_%s.txt", method, endpoint, timestamp);

            // ✅ Capturar dados da requisição
            String queryString = request.getQueryString();
            String fullUrl = request.getRequestURL().toString();
            if (queryString != null) {
                fullUrl += "?" + queryString;
            }

            // ✅ Capturar body da requisição
            String requestBody = new String(request.getContentAsByteArray());

            // ✅ Capturar body da resposta (AQUI ESTÁ A MAGIA!)
            String responseBody = new String(response.getContentAsByteArray());

            // ✅ Preparar conteúdo do arquivo
            StringBuilder content = new StringBuilder();
            content.append("=== ENTREGA - TESTE DE ENDPOINT ===\n");
            content.append("Data/Hora: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
            content.append("Método: ").append(method).append("\n");
            content.append("URL Completa: ").append(fullUrl).append("\n");
            content.append("Status HTTP: ").append(response.getStatus()).append("\n");
            content.append("Content-Type: ").append(response.getContentType()).append("\n");

            // ✅ ADICIONAR: Request Body (se houver)
            if (!requestBody.isEmpty()) {
                content.append("\n=== DADOS ENVIADOS ===\n");
                try {
                    Object jsonRequest = objectMapper.readValue(requestBody, Object.class);
                    String formattedRequest = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonRequest);
                    content.append(formattedRequest);
                } catch (Exception e) {
                    content.append(requestBody);
                }
            }

            // ✅ ADICIONAR: Response Body REAL
            content.append("\n\n=== RESPOSTA REAL ===\n");
            if (!responseBody.isEmpty()) {
                try {
                    Object jsonResponse = objectMapper.readValue(responseBody, Object.class);
                    String formattedResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResponse);
                    content.append(formattedResponse);
                } catch (Exception e) {
                    content.append(responseBody);
                }
            } else {
                content.append("(Resposta vazia)");
            }

            // ✅ Salvar arquivo
            try (FileWriter writer = new FileWriter(new File(dir, fileName))) {
                writer.write(content.toString());
            }

            // ✅ Log no console
            System.out.println("📄 Resposta completa salva: entregaveis/" + fileName);

        } catch (Exception e) {
            System.err.println("❌ Erro ao salvar resposta: " + e.getMessage());
        }
    }
}
