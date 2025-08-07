package org.driver.driverapp.exception;

public class TokenRefreshException extends RuntimeException {

    private final String token;

    public TokenRefreshException(String message) {
        super(message);
        this.token = null;
    }

    public TokenRefreshException(String token, String message) {
        super(message);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
