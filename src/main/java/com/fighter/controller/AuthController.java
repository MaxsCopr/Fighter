package com.fighter.controller;

import com.fighter.model.AuthResponce;
import com.fighter.model.RegisterRequest;
import com.fighter.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

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
}
