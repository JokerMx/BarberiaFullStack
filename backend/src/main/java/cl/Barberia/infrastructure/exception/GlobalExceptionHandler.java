package cl.Barberia.infrastructure.exception;

import cl.Barberia.domain.authentication.exceptions.CredencialesInvalidasException;
import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== ERRORES DE SWAGGER =====
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(
            NoResourceFoundException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        if (path.contains("swagger-ui") || path.contains("v3/api-docs")) {
            return buildErrorResponse("Swagger no disponible en este entorno",
                HttpStatus.NOT_FOUND, request);
        }
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    // ===== ERRORES DE NEGOCIO =====
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    // ===== ERRORES DE VALIDACIÓN =====
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

    // ===== ERRORES DE TIPO (ej: conversión de String a Long) =====
    @ExceptionHandler(org.springframework.core.convert.ConversionFailedException.class)
    public ResponseEntity<Map<String, Object>> handleConversionFailed(
            org.springframework.core.convert.ConversionFailedException ex, WebRequest request) {
        return buildErrorResponse("Error en el formato del parámetro: " + ex.getMessage(),
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex, WebRequest request) {
        return buildErrorResponse("Error en el tipo de dato: " + ex.getMessage(),
                HttpStatus.BAD_REQUEST, request);
    }

    // ===== ERRORES GENÉRICOS =====
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Si el mensaje contiene "no encontrado", devolver 404
        if (message != null && message.contains("no encontrado")) {
            status = HttpStatus.NOT_FOUND;
        }

        return buildErrorResponse(message, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        return buildErrorResponse("Error interno del servidor: " + ex.getMessage(),
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