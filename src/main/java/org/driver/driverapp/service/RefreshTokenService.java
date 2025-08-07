package org.driver.driverapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.driver.driverapp.exception.TokenRefreshException;
import org.driver.driverapp.model.RefreshToken;
import org.driver.driverapp.model.User;
import org.driver.driverapp.repository.RefreshTokenRepository;
import org.driver.driverapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refresh.expiration-ms:604800000}") // default 7 days
    private Long refreshTokenDurationMs;

    // 🔄 Create and save a refresh token for a user
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(token);
    }

    // ✅ Check if token is valid
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token expired. Please login again.");
        }
        return token;
    }

    // 🗑️ Delete all tokens for a user (logout from all devices)
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.deleteAllByUser(user);
    }

    // 🔁 Optional: Token rotation — delete old tokens except the latest
    @Transactional
    public void rotateTokens(User user, String keepToken) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
        for (RefreshToken t : tokens) {
            if (!t.getToken().equals(keepToken)) {
                refreshTokenRepository.delete(t);
            }
        }
    }

    public RefreshToken getByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException(token, "Refresh token not found"));
    }
}
