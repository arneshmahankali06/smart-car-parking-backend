package com.parking.service;

import com.parking.dto.AuthResponse;
import com.parking.dto.LoginRequest;
import com.parking.dto.RegisterRequest;
import com.parking.entity.Role;
import com.parking.entity.UserEntity;
import com.parking.repository.UserRepository;
import com.parking.security.JwtUtil;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthService(UserRepository userRepo, PasswordEncoder encoder,
                       JwtUtil jwtUtil, @Lazy AuthenticationManager authManager) {
        this.userRepo    = userRepo;
        this.encoder     = encoder;
        this.jwtUtil     = jwtUtil;
        this.authManager = authManager;
    }

    // ─── UserDetailsService ───────────────────────────────────────────────────

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
            .orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + email));
    }

    // ─── Register ─────────────────────────────────────────────────────────────

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Email already registered"
            );
        }

        UserEntity user = new UserEntity();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setRole(Role.DRIVER);

        userRepo.save(user);

        String token = jwtUtil.generateToken(user, user.getId(),
                                             user.getRole().name());
        return new AuthResponse(token, user.getName(), user.getEmail(),
                                user.getRole().name(), user.getId());
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        UserEntity user = (UserEntity) loadUserByUsername(req.getEmail());
        String token = jwtUtil.generateToken(user, user.getId(),
                                             user.getRole().name());
        return new AuthResponse(token, user.getName(), user.getEmail(),
                                user.getRole().name(), user.getId());
    }
}
