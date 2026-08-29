package cl.Barberia.domain.usermanagement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void normalizesValidEmailToLowercase() {
        assertEquals("cliente@example.com", new Email("Cliente@Example.COM").getValue());
    }

    @Test
    void rejectsBlankAndMalformedEmails() {
        assertThrows(IllegalArgumentException.class, () -> new Email(" "));
        assertThrows(IllegalArgumentException.class, () -> new Email("not-an-email"));
    }
}