package cl.Barberia.domain.authentication;

import lombok.Value;
import org.mindrot.jbcrypt.BCrypt;

@Value
public class PasswordHash {
    String value;

    private PasswordHash(String value) {
        this.value = value;
    }

    public static PasswordHash fromPlainPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        return new PasswordHash(hashed);
    }

    public static PasswordHash fromHash(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("El hash no puede estar vacío");
        }
        return new PasswordHash(hash);
    }

    public boolean verificar(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.value);
    }
}