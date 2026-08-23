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
        boolean usernameFormat = value.matches("^[a-zA-Z0-9_]+$");
        boolean emailFormat = value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        if (!usernameFormat && !emailFormat) {
            throw new IllegalArgumentException("El nombre de usuario debe contener letras, números y guión bajo, o ser un correo electrónico válido");
        }
        this.value = value;
    }
}