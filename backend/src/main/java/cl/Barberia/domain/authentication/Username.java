package cl.Barberia.domain.authentication;

import lombok.Value;

@Value  // Inmutable: genera constructor, getters, equals, hashCode
public class Username {
    String value;

    public Username(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        if (value.length() < 3 || value.length() > 50) {
            throw new IllegalArgumentException("El nombre de usuario debe tener entre 3 y 50 caracteres");
        }
        if (!value.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("El nombre de usuario solo puede contener letras, números y guión bajo");
        }
        this.value = value;
    }
}