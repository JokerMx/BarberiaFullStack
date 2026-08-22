// domain/authentication/exceptions/CredencialesInvalidasException.java
package cl.Barberia.domain.authentication.exceptions;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}

