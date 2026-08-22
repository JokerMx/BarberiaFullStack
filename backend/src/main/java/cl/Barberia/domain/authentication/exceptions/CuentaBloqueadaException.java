// domain/authentication/exceptions/CuentaBloqueadaException.java
package cl.Barberia.domain.authentication.exceptions;

public class CuentaBloqueadaException extends RuntimeException {
    public CuentaBloqueadaException(String message) {
        super(message);
    }
}