package com.alita.agendador_horarios.infrastructure.exceptions;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

     private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Método utilitário para construir o erro
    private ErrorResponse buildError(HttpStatus status, String message, String path) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path);
    }

    @ExceptionHandler({
        AgendamentoNotFoundException.class
})
public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest req) {
    ErrorResponse error = buildError(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}

@ExceptionHandler({
        AgendamentoConflictException.class
})
public ResponseEntity<ErrorResponse> handleConflict(AgendamentoConflictException ex, HttpServletRequest req) {
    ErrorResponse error = buildError(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
}

@ExceptionHandler({
        AgendamentoInvalidException.class
})
public ResponseEntity<ErrorResponse> handleInvalid(RuntimeException ex, HttpServletRequest req) {
    ErrorResponse error = buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    return ResponseEntity.badRequest().body(error);
}
}
