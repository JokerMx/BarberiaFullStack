package cl.Barberia.domain.usermanagement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

class UsuarioTest {

    @Test
    void defaultsToActiveAndTracksActivationChanges() {
        Usuario usuario = Usuario.builder().id(1L).username("cliente").email("cliente@example.com")
            .nombreCompleto("Cliente Prueba").rol(Rol.CLIENTE).build();

        assertTrue(usuario.isActivo());
        assertNotNull(usuario.getFechaCreacion());
        usuario.desactivar();
        assertFalse(usuario.isActivo());
        usuario.activar();
        assertTrue(usuario.isActivo());
    }

    @Test
    void keepsExplicitAuditDates() {
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        LocalDateTime updated = LocalDateTime.now().minusHours(1);
        Usuario usuario = Usuario.builder().id(1L).username("cliente").email("cliente@example.com")
            .nombreCompleto("Cliente Prueba").rol(Rol.CLIENTE).fechaCreacion(created).fechaActualizacion(updated).build();

        assertEquals(created, usuario.getFechaCreacion());
        assertEquals(updated, usuario.getFechaActualizacion());
    }
}