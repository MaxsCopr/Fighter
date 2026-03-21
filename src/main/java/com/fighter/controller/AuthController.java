package com.fighter.controller;

import com.fighter.model.AuthRequest;
import com.fighter.model.AuthResponce;
import com.fighter.model.RegisterRequest;
import com.fighter.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("liveness")
    public ResponseEntity<String> liveness(){
        return new ResponseEntity<>("Running", HttpStatus.OK);
    }
    @PostMapping("register")
    public ResponseEntity<AuthResponce> register(@RequestBody RegisterRequest req){
        AuthResponce res = authService.register(req);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponce> login(
            @Valid @RequestBody AuthRequest request) {
        AuthResponce response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponce> refresh(
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        AuthResponce response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }
}
