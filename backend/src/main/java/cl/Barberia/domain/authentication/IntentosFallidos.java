package cl.Barberia.domain.authentication;

import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;  // ← IMPORT AGREGADO
import java.time.LocalDateTime;

public class IntentosFallidos {
    private static final int MAX_INTENTOS = 5;
    private static final int TIEMPO_BLOQUEO_MINUTOS = 30;

    private int contador;
    private LocalDateTime bloqueadoHasta;

    public IntentosFallidos() {
        this.contador = 0;
        this.bloqueadoHasta = null;
    }

    public void registrarIntentoFallido() {
        if (estaBloqueado()) {
            throw new CuentaBloqueadaException("Cuenta bloqueada hasta: " + bloqueadoHasta);
        }
        contador++;
        if (contador >= MAX_INTENTOS) {
            bloqueadoHasta = LocalDateTime.now().plusMinutes(TIEMPO_BLOQUEO_MINUTOS);
            throw new CuentaBloqueadaException(
                    "Demasiados intentos fallidos. Cuenta bloqueada por " + TIEMPO_BLOQUEO_MINUTOS + " minutos"
            );
        }
    }

    public void resetear() {
        this.contador = 0;
        this.bloqueadoHasta = null;
    }

    public boolean estaBloqueado() {
        if (bloqueadoHasta == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(bloqueadoHasta)) {
            resetear();
            return false;
        }
        return true;
    }

    public int getContador() {
        return contador;
    }

    public LocalDateTime getBloqueadoHasta() {
        return bloqueadoHasta;
    }
}