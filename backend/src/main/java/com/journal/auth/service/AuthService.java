package com.journal.auth.service;

import com.journal.auth.dto.AuthRequest;
import com.journal.auth.dto.AuthResponse;
import com.journal.auth.dto.RegisterRequest;
import com.journal.config.JwtService;
import com.journal.user.dto.UserSummary;
import com.journal.user.model.RoleType;
import com.journal.user.model.UserAccount;
import com.journal.user.service.UserService;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        RoleType role = request.role() != null ? request.role() : RoleType.AUTHOR;
        UserAccount user = new UserAccount();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setOrganization(request.organization());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        UserAccount saved = userService.create(user);
        String accessToken = jwtService.generateAccessToken(saved, Map.of("role", saved.getRole().name()));
        String refreshToken = jwtService.generateRefreshToken(saved);
        return new AuthResponse(accessToken, refreshToken, userService.toSummary(saved));
    }

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserAccount principal = (UserAccount) authentication.getPrincipal();
        userService.updateLastLogin(principal);
        String accessToken = jwtService.generateAccessToken(principal, Map.of("role", principal.getRole().name()));
        String refreshToken = jwtService.generateRefreshToken(principal);
        UserSummary summary = userService.toSummary(principal);
        return new AuthResponse(accessToken, refreshToken, summary);
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        UserAccount user = userService.getByEmail(username);
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String accessToken = jwtService.generateAccessToken(user, Map.of("role", user.getRole().name()));
        String newRefresh = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, newRefresh, userService.toSummary(user));
    }
}
