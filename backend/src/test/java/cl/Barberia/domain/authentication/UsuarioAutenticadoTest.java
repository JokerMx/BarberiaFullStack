package cl.Barberia.domain.authentication;

import cl.Barberia.domain.authentication.exceptions.CredencialesInvalidasException;
import cl.Barberia.domain.usermanagement.Rol;
import cl.Barberia.domain.usermanagement.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

class UsuarioAutenticadoTest {

    @Test
    void authenticatesValidPasswordAndTracksInvalidAttempt() {
        Usuario usuario = Usuario.builder().id(1L).username("cliente").email("cliente@example.com")
            .nombreCompleto("Cliente Prueba").rol(Rol.CLIENTE).build();
        UsuarioAutenticado autenticado = new UsuarioAutenticado(usuario, PasswordHash.fromPlainPassword("secret123"));

        assertTrue(autenticado.autenticar("secret123"));
        assertThrows(CredencialesInvalidasException.class, () -> autenticado.autenticar("incorrecta"));
        assertEquals(1, autenticado.getIntentosFallidos().getContador());
        assertEquals("cliente", autenticado.getUsername().getValue());
    }

    @Test
    void rejectsAuthenticationWhenAggregateIsAlreadyLocked() {
        UsuarioAutenticado autenticado = authenticatedUser();
        ReflectionTestUtils.setField(autenticado.getIntentosFallidos(), "bloqueadoHasta", LocalDateTime.now().plusMinutes(1));

        assertThrows(cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException.class,
            () -> autenticado.autenticar("secret123"));
    }

    private UsuarioAutenticado authenticatedUser() {
        Usuario usuario = Usuario.builder().id(1L).username("cliente").email("cliente@example.com")
            .nombreCompleto("Cliente Prueba").rol(Rol.CLIENTE).build();
        return new UsuarioAutenticado(usuario, PasswordHash.fromPlainPassword("secret123"));
    }
}