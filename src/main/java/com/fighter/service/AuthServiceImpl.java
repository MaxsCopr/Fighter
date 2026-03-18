package com.fighter.service;

import com.fighter.entity.Role;
import com.fighter.entity.User;
import com.fighter.model.AuthRequest;
import com.fighter.model.AuthResponce;
import com.fighter.model.RegisterRequest;
import com.fighter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponce register(RegisterRequest req) {
        if(userRepository.existsByEmail(req.getEmail())) {
         log.info("User already Exist");
        }
        User user = User.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .build();
        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());
        String accessToken  = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        return buildAuthResponse(savedUser,accessToken,refreshToken);
    }

    @Override
    public AuthResponce login(AuthRequest req) {
        return null;
    }

    @Override
    public AuthResponce refreshToken(String refreshToken) {
        return null;
    }

    @Override
    public void logout(String authHeader) {

    }
    private AuthResponce buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponce.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiry())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
}
