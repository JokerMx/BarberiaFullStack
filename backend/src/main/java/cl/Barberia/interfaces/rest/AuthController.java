package cl.Barberia.interfaces.rest;

import cl.Barberia.application.authentication.DTOs.LoginRequest;
import cl.Barberia.application.authentication.DTOs.LoginResponse;
import cl.Barberia.application.authentication.LoginService;
import cl.Barberia.domain.authentication.UsuarioAutenticado;
import cl.Barberia.domain.authentication.exceptions.CredencialesInvalidasException;
import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            // Determinar qué campo usar para el login
            String login = request.getUsername() != null ? request.getUsername() : request.getEmail();

            if (login == null || login.isBlank()) {
                return ResponseEntity.status(400)
                    .body(LoginResponse.failure("Usuario o email no proporcionado"));
            }

            UsuarioAutenticado autenticado = loginService.autenticar(login, request.getPassword());

            LoginResponse response = LoginResponse.success(
                autenticado.getUsuario().getUsername(),
                autenticado.getUsuario().getRol().name()
            );
            session.setAttribute("AUTH_USERNAME", autenticado.getUsuario().getUsername());
            session.setAttribute("AUTH_ROLE", autenticado.getUsuario().getRol().name());
            return ResponseEntity.ok(response);

        } catch (CuentaBloqueadaException e) {
            return ResponseEntity.status(423)
                .body(LoginResponse.bloqueado(e.getMessage()));

        } catch (CredencialesInvalidasException e) {
            return ResponseEntity.status(401)
                .body(LoginResponse.failure(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(LoginResponse.failure("Error interno del servidor"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("API de Barbería funcionando ✅");
    }
}