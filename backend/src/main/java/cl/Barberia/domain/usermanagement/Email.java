package cl.Barberia.domain.usermanagement;

import lombok.Value;

@Value
public class Email {
    String value;

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        this.value = value.toLowerCase();
    }
}