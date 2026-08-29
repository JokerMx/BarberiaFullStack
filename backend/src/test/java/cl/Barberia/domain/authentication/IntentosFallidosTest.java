package cl.Barberia.domain.authentication;

import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

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

    @Test
    void rejectsFurtherAttemptsWhileAccountIsLocked() {
        IntentosFallidos intentos = new IntentosFallidos();

        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                intentos.registrarIntentoFallido();
            } catch (CuentaBloqueadaException ignored) {
            }
        }

        assertThrows(CuentaBloqueadaException.class, intentos::registrarIntentoFallido);
    }

    @Test
    void clearsExpiredLockBeforeCheckingState() {
        IntentosFallidos intentos = new IntentosFallidos();
        ReflectionTestUtils.setField(intentos, "bloqueadoHasta", LocalDateTime.now().minusMinutes(1));
        ReflectionTestUtils.setField(intentos, "contador", 5);

        assertFalse(intentos.estaBloqueado());
        assertEquals(0, intentos.getContador());
    }
}
