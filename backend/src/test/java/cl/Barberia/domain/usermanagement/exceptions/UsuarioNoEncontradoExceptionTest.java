package cl.Barberia.domain.usermanagement.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioNoEncontradoExceptionTest {
    @Test
    void preservesExceptionMessage() {
        assertEquals("Usuario no encontrado", new UsuarioNoEncontradoException("Usuario no encontrado").getMessage());
    }
}