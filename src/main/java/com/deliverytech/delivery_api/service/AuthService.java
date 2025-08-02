package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.LoginRequest;
import com.deliverytech.delivery_api.dto.request.RegisterRequest;
import com.deliverytech.delivery_api.dto.response.LoginResponse;
import com.deliverytech.delivery_api.dto.response.UserResponse;
import com.deliverytech.delivery_api.model.Usuario;

public interface AuthService {
    
    LoginResponse login(LoginRequest request);

    Usuario register(RegisterRequest request);

    UserResponse getCurrentUser();
}
