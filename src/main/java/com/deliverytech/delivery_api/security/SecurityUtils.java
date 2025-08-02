package com.deliverytech.delivery_api.security;
// import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.model.Usuario; // Update this line if 'Usuario' is in 'model' package, or correct to the actual package
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {

    public static Optional<Usuario> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Usuario usuario) {
            return Optional.of(usuario);
        }
        return Optional.empty();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser()
                .map(Usuario::getId)
                .orElseThrow(() -> new RuntimeException("Usuário não autenticado"));
    }

    public static boolean hasRole(String role) {
        return getCurrentUser()
                .map(u -> u.getRole().name().equals(role))
                .orElse(false);
    }

    public static boolean isRestaurante() {
        return hasRole("RESTAURANTE");
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public static boolean isCliente() {
        return hasRole("CLIENTE");
    }

    public static boolean isEntregador() {
        return hasRole("ENTREGADOR");
    }
}