package cl.Barberia.interfaces.rest;

import cl.Barberia.application.authentication.DTOs.LoginRequest;
import cl.Barberia.application.authentication.DTOs.LoginResponse;
import cl.Barberia.application.authentication.LoginService;
import cl.Barberia.domain.authentication.UsuarioAutenticado;
import cl.Barberia.domain.authentication.exceptions.CredencialesInvalidasException;
import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            // ===== DETERMINAR QUÉ CAMPO USAR =====
            String login = request.getUsername();
            
            // Si no hay username, usar email
            if (login == null || login.isBlank()) {
                login = request.getEmail();
            }
            
            // Si ambos son null, error
            if (login == null || login.isBlank()) {
                return ResponseEntity.status(400)
                    .body(LoginResponse.failure("Usuario o email no proporcionado"));
            }
            
            UsuarioAutenticado autenticado = loginService.autenticar(login, request.getPassword());
            
            LoginResponse response = LoginResponse.success(
                autenticado.getUsuario().getUsername(),
                autenticado.getUsuario().getRol().name()
            );
            return ResponseEntity.ok(response);
            
        } catch (CuentaBloqueadaException e) {
            return ResponseEntity.status(423)
                .body(LoginResponse.bloqueado(e.getMessage()));
                
        } catch (CredencialesInvalidasException e) {
            return ResponseEntity.status(401)
                .body(LoginResponse.failure(e.getMessage()));
                
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(LoginResponse.failure("Error interno del servidor: " + e.getMessage()));
        }
    }
}