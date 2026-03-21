package com.fighter.service;

import com.fighter.entity.Role;
import com.fighter.entity.User;
import com.fighter.exception.InvalidCredentialsException;
import com.fighter.model.AuthRequest;
import com.fighter.model.AuthResponce;
import com.fighter.model.RegisterRequest;
import com.fighter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public AuthResponce login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        log.info("User logged in: {}", user.getEmail());

        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public AuthResponce refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        String newAccessToken  = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtService.invalidateToken(token);
            log.info("Token invalidated");
        }
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
