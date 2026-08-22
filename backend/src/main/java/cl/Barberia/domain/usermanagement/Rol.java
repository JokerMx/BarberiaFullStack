package cl.Barberia.domain.usermanagement;

public enum Rol {
    ADMIN,
    BARBERO,
    CLIENTE;

    public static Rol fromString(String valor) {
        try {
            return Rol.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido: " + valor);
        }
    }
}