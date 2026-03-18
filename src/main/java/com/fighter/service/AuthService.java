package com.fighter.service;

import com.fighter.model.AuthRequest;
import com.fighter.model.AuthResponce;
import com.fighter.model.RegisterRequest;

public interface AuthService {
    public AuthResponce register(RegisterRequest req);
    public AuthResponce login(AuthRequest req);
    public AuthResponce refreshToken(String refreshToken);
    void logout(String authHeader);
}
