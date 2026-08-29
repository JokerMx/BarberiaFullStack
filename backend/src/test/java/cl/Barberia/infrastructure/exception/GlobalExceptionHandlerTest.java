package cl.Barberia.infrastructure.exception;

import cl.Barberia.domain.authentication.exceptions.CredencialesInvalidasException;
import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsBusinessAndValidationErrorsToClientStatuses() {
        WebRequest request = request("uri=/api/auth/login");

        assertEquals(401, handler.handleCredencialesInvalidas(new CredencialesInvalidasException("invalid"), request).getStatusCode().value());
        assertEquals(423, handler.handleCuentaBloqueada(new CuentaBloqueadaException("locked"), request).getStatusCode().value());
        assertEquals(400, handler.handleIllegalArgument(new IllegalArgumentException("invalid"), request).getStatusCode().value());
    }

    @Test
    void mapsKnownRuntimeNotFoundAndUnexpectedRuntimeErrors() {
        WebRequest request = request("uri=/api/usuarios/1");

        assertEquals(404, handler.handleRuntimeException(new RuntimeException("Usuario no encontrado"), request).getStatusCode().value());
        assertEquals(500, handler.handleRuntimeException(new RuntimeException("database error"), request).getStatusCode().value());
        assertEquals(500, handler.handleGenericException(new Exception("unexpected"), request).getStatusCode().value());
    }

    @Test
    void hidesSwaggerOutsideDevelopmentAndReturnsOtherMissingResources() {
        assertEquals("Swagger no disponible en este entorno", handler.handleNoResourceFound(
            new NoResourceFoundException(HttpMethod.GET, "swagger-ui/index.html"), request("uri=/swagger-ui/index.html"))
            .getBody().get("message"));
        assertEquals(404, handler.handleNoResourceFound(
            new NoResourceFoundException(HttpMethod.GET, "missing"), request("uri=/missing"))
            .getStatusCode().value());
    }

    @Test
    void mapsValidationAndParameterConversionFailures() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("request", "email", "invalid")));
        MethodArgumentNotValidException validation = mock(MethodArgumentNotValidException.class);
        when(validation.getBindingResult()).thenReturn(bindingResult);
        ConversionFailedException conversion = new ConversionFailedException(
            TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Long.class), "bad", new IllegalArgumentException("bad"));
        MethodArgumentTypeMismatchException typeMismatch = mock(MethodArgumentTypeMismatchException.class);
        when(typeMismatch.getMessage()).thenReturn("wrong type");
        WebRequest request = request("uri=/api/usuarios/bad");

        assertEquals(400, handler.handleValidationExceptions(validation, request).getStatusCode().value());
        assertEquals(400, handler.handleConversionFailed(conversion, request).getStatusCode().value());
        assertEquals(400, handler.handleTypeMismatch(typeMismatch, request).getStatusCode().value());
    }

    private WebRequest request(String description) {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn(description);
        return request;
    }
}