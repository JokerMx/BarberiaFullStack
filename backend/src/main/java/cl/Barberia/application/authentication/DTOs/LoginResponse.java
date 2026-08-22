package cl.Barberia.application.authentication.DTOs;

import lombok.Data;

@Data
public class LoginResponse {
    private final boolean success;
    private final String mensaje;
    private final String username;
    private final String rol;
    private final boolean bloqueado;

    public static LoginResponse success(String username, String rol) {
        return new LoginResponse(true, "Login exitoso", username, rol, false);
    }

    public static LoginResponse failure(String mensaje) {
        return new LoginResponse(false, mensaje, null, null, false);
    }

    public static LoginResponse bloqueado(String mensaje) {
        return new LoginResponse(false, mensaje, null, null, true);
    }
}