package com.deliverytech.delivery_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    // Gera um token JWT para o usuário
    public String gerarToken(String email) {
        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + expiration);
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
        
        return Jwts.builder()
                .subject(email)
                .issuedAt(agora)
                .expiration(dataExpiracao)
                .signWith(key)
                .compact();
    }
    
    // Valida um token JWT e retorna as claims
    public Claims validarToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));

            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null; // Token inválido
        }
    }
    
    // Extrai o email (subject) do token
    public String getEmailFromToken(String token) {
        Claims claims = validarToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    public String generateToken(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateToken'");
    }
}