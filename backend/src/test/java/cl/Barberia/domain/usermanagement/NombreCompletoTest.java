package cl.Barberia.domain.usermanagement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NombreCompletoTest {

    @Test
    void trimsValidName() {
        assertEquals("Cliente Prueba", new NombreCompleto(" Cliente Prueba ").getValue());
    }

    @Test
    void rejectsBlankAndInvalidLengthNames() {
        assertThrows(IllegalArgumentException.class, () -> new NombreCompleto(" "));
        assertThrows(IllegalArgumentException.class, () -> new NombreCompleto("A"));
        assertThrows(IllegalArgumentException.class, () -> new NombreCompleto("a".repeat(101)));
    }
}