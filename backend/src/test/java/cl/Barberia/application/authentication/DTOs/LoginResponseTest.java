package cl.Barberia.application.authentication.DTOs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {
    @Test void createsSuccessFailureAndLockedResponses() {
        assertTrue(LoginResponse.success("cliente", "CLIENTE").isSuccess());
        assertFalse(LoginResponse.failure("invalid").isSuccess());
        assertTrue(LoginResponse.bloqueado("locked").isBloqueado());
    }
}