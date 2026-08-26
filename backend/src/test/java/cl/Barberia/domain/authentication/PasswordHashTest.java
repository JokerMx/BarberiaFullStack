package cl.Barberia.domain.authentication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashTest {

    @Test
    void hashesAndVerifiesValidPassword() {
        PasswordHash hash = PasswordHash.fromPlainPassword("secret123");

        assertNotEquals("secret123", hash.getValue());
        assertTrue(hash.verificar("secret123"));
        assertFalse(hash.verificar("wrong-password"));
    }

    @Test
    void rejectsShortPassword() {
        assertThrows(IllegalArgumentException.class,
            () -> PasswordHash.fromPlainPassword("12345"));
    }
}
