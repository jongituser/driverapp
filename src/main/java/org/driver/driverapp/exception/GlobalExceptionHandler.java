package org.driver.driverapp.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🎯 Handle custom refresh token errors
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<?> handleTokenRefreshException(TokenRefreshException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", "Refresh Token Error", "message", ex.getMessage())
        );
    }

    // 🧍 Handle user not found
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "User Not Found", "message", ex.getMessage())
        );
    }

    // 🧪 Handle DTO validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    // 🧨 Catch-all for unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("error", "Internal Server Error", "message", ex.getMessage())
        );
    }
}
