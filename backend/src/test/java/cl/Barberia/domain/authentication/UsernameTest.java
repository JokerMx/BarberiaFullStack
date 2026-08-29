package cl.Barberia.domain.authentication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsernameTest {
    @Test
    void aceptaUnCorreoComoNombreDeUsuario() {
        assertDoesNotThrow(() -> new Username("cliente@example.com"));
    }

    @Test
    void rechazaCaracteresNoPermitidos() {
        assertThrows(IllegalArgumentException.class, () -> new Username("cliente!"));
    }

    @Test
    void rejectsBlankAndTooShortUsername() {
        assertThrows(IllegalArgumentException.class, () -> new Username(" "));
        assertThrows(IllegalArgumentException.class, () -> new Username("ab"));
    }
}