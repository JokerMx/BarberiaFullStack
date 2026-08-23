package cl.Barberia.application.authentication.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String mensaje;
    private String username;
    private String rol;
    private boolean bloqueado;

    public static LoginResponse success(String username, String rol) {
        return LoginResponse.builder()
            .success(true)
            .mensaje("Login exitoso")
            .username(username)
            .rol(rol)
            .bloqueado(false)
            .build();
    }

    public static LoginResponse failure(String mensaje) {
        return LoginResponse.builder()
            .success(false)
            .mensaje(mensaje)
            .bloqueado(false)
            .build();
    }

    public static LoginResponse bloqueado(String mensaje) {
        return LoginResponse.builder()
            .success(false)
            .mensaje(mensaje)
            .bloqueado(true)
            .build();
    }
}