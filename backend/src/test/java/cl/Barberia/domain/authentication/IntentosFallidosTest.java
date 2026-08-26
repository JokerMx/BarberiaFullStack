package cl.Barberia.domain.authentication;

import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntentosFallidosTest {

    @Test
    void blocksAccountOnFifthFailedAttempt() {
        IntentosFallidos intentos = new IntentosFallidos();

        for (int i = 0; i < 4; i++) {
            intentos.registrarIntentoFallido();
        }

        assertThrows(CuentaBloqueadaException.class, intentos::registrarIntentoFallido);
        assertTrue(intentos.estaBloqueado());
        assertEquals(5, intentos.getContador());
    }

    @Test
    void resetClearsAttemptsAndLock() {
        IntentosFallidos intentos = new IntentosFallidos();
        intentos.registrarIntentoFallido();

        intentos.resetear();

        assertEquals(0, intentos.getContador());
        assertFalse(intentos.estaBloqueado());
    }
}
