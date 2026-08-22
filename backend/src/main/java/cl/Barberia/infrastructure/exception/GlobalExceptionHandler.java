package cl.Barberia.infrastructure.exception;

import cl.Barberia.domain.authentication.exceptions.CredencialesInvalidasException;
import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== ERRORES DE NEGOCIO (422 Unprocessable Entity) =====
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredencialesInvalidas(
            CredencialesInvalidasException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(CuentaBloqueadaException.class)
    public ResponseEntity<Map<String, Object>> handleCuentaBloqueada(
            CuentaBloqueadaException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.LOCKED, request);
    }

    // ===== ERRORES DE VALIDACIÓN (400 Bad Request) =====
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse("Error de validación: " + errors, HttpStatus.BAD_REQUEST, request);
    }

    // ===== RECURSO NO ENCONTRADO (404 Not Found) =====
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        HttpStatus status = ex.getMessage().contains("no encontrado") 
                ? HttpStatus.NOT_FOUND 
                : HttpStatus.INTERNAL_SERVER_ERROR;
        return buildErrorResponse(ex.getMessage(), status, request);
    }

    // ===== ERRORES GENÉRICOS (500 Internal Server Error) =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        return buildErrorResponse("Error interno del servidor", 
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // ===== MÉTODO AUXILIAR =====
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            String message, HttpStatus status, WebRequest request) {
        
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorResponse, status);
    }
}