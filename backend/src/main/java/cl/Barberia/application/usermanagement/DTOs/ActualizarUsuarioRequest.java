package cl.Barberia.application.usermanagement.DTOs;

import lombok.Data;

@Data
public class ActualizarUsuarioRequest {
    private String email;
    private String nombreCompleto;
    private String rol;
    private Boolean activo;
    private String password;
}