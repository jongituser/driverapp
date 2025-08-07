package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.AuthResponseDTO;
import org.driver.driverapp.exception.TokenRefreshException;
import org.driver.driverapp.model.RefreshToken;
import org.driver.driverapp.model.User;
import org.driver.driverapp.repository.RefreshTokenRepository;
import org.driver.driverapp.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final RefreshTokenRepository refreshTokenRepo;
    private final JwtUtil jwtUtil;

    // ⏳ Set the refresh token validity period
    private final Duration refreshTokenExpiry = Duration.ofDays(7);

    // 🔁 Refresh access token using a valid refresh token
    public AuthResponseDTO refreshToken(String oldToken) {
        RefreshToken existing = refreshTokenRepo.findByToken(oldToken)
                .orElseThrow(() -> new TokenRefreshException("Invalid refresh token"));

        // 🔒 Check if token has expired
        if (existing.isExpired()) {
            refreshTokenRepo.delete(existing);
            throw new TokenRefreshException("Refresh token has expired");
        }

        // ❌ Invalidate the old token (rotation policy)
        refreshTokenRepo.delete(existing);

        // 🔄 Create and store a new refresh token
        RefreshToken newToken = createRefreshToken(existing.getUser());

        // 🔐 Generate new access token
        String newAccessToken = jwtUtil.generateToken(
                existing.getUser().getUsername(),
                "ROLE_" + existing.getUser().getRole().name()
        );

        return new AuthResponseDTO(newAccessToken, newToken.getToken());
    }

    // ✅ Create and persist a new refresh token for a user
    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(refreshTokenExpiry))
                .build();

        return refreshTokenRepo.save(token);
    }
}
