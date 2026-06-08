package com.navcare.exception;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.navcare.dto.ErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Aqui eu centralizo o formato de erro para que o frontend sempre receba
    // a mesma estrutura, independentemente de qual camada disparou a excecao.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Recurso não encontrado", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(fieldError -> fieldError.getDefaultMessage())
            .orElse("Dados inválidos.");
        return build(HttpStatus.BAD_REQUEST, "Erro de validação", message, request.getRequestURI());
    }

    @ExceptionHandler(AiIntegrationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAi(AiIntegrationException exception, HttpServletRequest request) {
        return build(HttpStatus.BAD_GATEWAY, "Erro na integração com IA", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidJson(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "JSON inválido", "Verifique o corpo da requisição.", request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrity(DataIntegrityViolationException exception, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Conflito de dados", "Já existe um registro com essas informações.", request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorized(UnauthorizedException exception, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Não autorizado", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception exception, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Ocorreu um erro inesperado.", request.getRequestURI());
    }

    // Eu concentro a montagem da resposta para nao repetir o mesmo envelope em cada handler.
    private ResponseEntity<ErrorResponseDTO> build(HttpStatus status, String error, String message, String path) {
        return ResponseEntity.status(status).body(ErrorResponseDTO.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(error)
            .message(message)
            .path(path)
            .build());
    }
}
