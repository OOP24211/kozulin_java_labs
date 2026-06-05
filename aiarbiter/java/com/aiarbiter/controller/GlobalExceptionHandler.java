package com.aiarbiter.controller;

import com.aiarbiter.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import jakarta.persistence.EntityNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad request");
        pd.setDetail(e.getMessage());
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Registration error");
        pd.setDetail(e.getMessage());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation error");
        pd.setDetail(e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Invalid request"));
        return pd;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFound(EntityNotFoundException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Not found");
        pd.setDetail(e.getMessage());
        return pd;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ProblemDetail handleClientError(HttpClientErrorException e) {
        log.warn("API client error {}: {}", e.getStatusCode(), e.getMessage());
        var pd = ProblemDetail.forStatus(e.getStatusCode());
        pd.setTitle("AI provider error");
        pd.setDetail("Provider returned " + e.getStatusCode());
        return pd;
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ProblemDetail handleServerError(HttpServerErrorException e) {
        log.error("API server error {}: {}", e.getStatusCode(), e.getMessage());
        var pd = ProblemDetail.forStatus(e.getStatusCode());
        pd.setTitle("AI provider error");
        pd.setDetail("Provider returned " + e.getStatusCode());
        return pd;
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleTimeout(ResourceAccessException e) {
        log.error("API timeout: {}", e.getMessage());
        var pd = ProblemDetail.forStatus(HttpStatus.GATEWAY_TIMEOUT);
        pd.setTitle("Request timeout");
        pd.setDetail("AI provider did not respond in time");
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception e) {
        log.error("Unexpected error", e);
        var pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Internal error");
        pd.setDetail("An unexpected error occurred");
        return pd;
    }
}
