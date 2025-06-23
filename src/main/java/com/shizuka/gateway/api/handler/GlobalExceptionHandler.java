package com.shizuka.gateway.api.handler;

import com.shizuka.gateway.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RouteConfigNotFoundException.class)
    public ResponseEntity<?> handleNotFound(RouteConfigNotFoundException ex) {
        log.warn("Not Found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RouteConfigAlreadyExistsException.class)
    public ResponseEntity<?> handleAlreadyExists(RouteConfigAlreadyExistsException ex) {
        log.warn("Already Exists: {}", ex.getMessage());
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateRouteConfigIdException.class)
    public ResponseEntity<?> handleDuplicateIds(DuplicateRouteConfigIdException ex) {
        log.warn("Duplicate IDs: {}", ex.getMessage());
        return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
    }
}
