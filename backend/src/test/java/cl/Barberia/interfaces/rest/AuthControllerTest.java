package cl.Barberia.interfaces.rest;

import cl.Barberia.application.authentication.DTOs.LoginRequest;
import cl.Barberia.application.authentication.LoginService;
import cl.Barberia.domain.authentication.PasswordHash;
import cl.Barberia.domain.authentication.UsuarioAutenticado;
import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import cl.Barberia.domain.authentication.exceptions.CredencialesInvalidasException;
import cl.Barberia.domain.usermanagement.Rol;
import cl.Barberia.domain.usermanagement.Usuario;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private LoginService loginService;
    @Mock private HttpSession session;
    @InjectMocks private AuthController authController;

    @Test
    void rejectsRequestWithoutUsernameOrEmail() {
        LoginRequest request = new LoginRequest();
        request.setPassword("secret123");

        assertEquals(400, authController.login(request, session).getStatusCode().value());
    }

    @Test
    void mapsInvalidCredentialsAndSupportsLogoutAndHealth() {
        LoginRequest request = new LoginRequest();
        request.setUsername("cliente"); request.setPassword("bad-password");
        when(loginService.autenticar("cliente", "bad-password"))
            .thenThrow(new CredencialesInvalidasException("invalid"));

        assertEquals(401, authController.login(request, session).getStatusCode().value());
        assertEquals(204, authController.logout(session).getStatusCode().value());
        assertEquals(200, authController.health().getStatusCode().value());
        verify(session).invalidate();
    }

    @Test
    void storesSessionAndReturnsSuccessForValidLogin() {
        LoginRequest request = new LoginRequest();
        request.setEmail("cliente@example.com"); request.setPassword("secret123");
        Usuario usuario = Usuario.builder().id(1L).username("cliente").email("cliente@example.com")
            .nombreCompleto("Cliente Prueba").rol(Rol.CLIENTE).build();
        when(loginService.autenticar("cliente@example.com", "secret123"))
            .thenReturn(new UsuarioAutenticado(usuario, PasswordHash.fromPlainPassword("secret123")));

        var response = authController.login(request, session);

        assertEquals(200, response.getStatusCode().value());
        verify(session).setAttribute("AUTH_USERNAME", "cliente");
        verify(session).setAttribute("AUTH_ROLE", "CLIENTE");
    }

    @Test
    void mapsLockedAndUnexpectedLoginFailures() {
        LoginRequest request = new LoginRequest();
        request.setUsername("cliente"); request.setPassword("secret123");
        when(loginService.autenticar("cliente", "secret123"))
            .thenThrow(new CuentaBloqueadaException("locked"))
            .thenThrow(new IllegalStateException("unexpected"));

        assertEquals(423, authController.login(request, session).getStatusCode().value());
        assertEquals(500, authController.login(request, session).getStatusCode().value());
    }
}