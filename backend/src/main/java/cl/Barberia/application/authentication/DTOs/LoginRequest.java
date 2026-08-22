package cl.Barberia.application.authentication.DTOs;

import lombok.Data;

@Data
public class LoginRequest {
    private String login;
    private String password;
}