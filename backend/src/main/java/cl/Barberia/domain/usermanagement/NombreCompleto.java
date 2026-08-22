package cl.Barberia.domain.usermanagement;

import lombok.Value;

@Value
public class NombreCompleto {
    String value;

    public NombreCompleto(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (value.length() < 2 || value.length() > 100) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 100 caracteres");
        }
        this.value = value.trim();
    }
}