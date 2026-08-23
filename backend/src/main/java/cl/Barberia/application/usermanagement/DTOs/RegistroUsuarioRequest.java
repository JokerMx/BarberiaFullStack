package cl.Barberia.application.usermanagement.DTOs;

import lombok.Data;

@Data
public class RegistroUsuarioRequest {
    private String username;
    private String email;
    private String nombreCompleto;
    private String password;
    private String rol;
}