package org.driver.driverapp.controller;

import org.driver.driverapp.exception.TokenRefreshException;
import org.driver.driverapp.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.LoginRequestDTO;
import org.driver.driverapp.dto.AuthResponseDTO;
import org.driver.driverapp.dto.RegisterUserRequestDTO;
import org.driver.driverapp.model.RefreshToken;
import org.driver.driverapp.repository.UserRepository;
import org.driver.driverapp.security.JwtUtil;
import org.driver.driverapp.service.TokenRefreshService;
import org.driver.driverapp.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority()
        );

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        RefreshToken refreshToken = tokenRefreshService.createRefreshToken(user);

        return ResponseEntity.ok(new AuthResponseDTO(accessToken, refreshToken.getToken()));
    }
    @GetMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String oldRefreshToken = authHeader.substring(7);

        try {
            AuthResponseDTO response = tokenRefreshService.refreshToken(oldRefreshToken);
            return ResponseEntity.ok(response);
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(401).body(new AuthResponseDTO("Refresh token expired or invalid", null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserRequestDTO request) {
        userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully!");
    }
}
