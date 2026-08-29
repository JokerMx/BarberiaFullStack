package cl.Barberia.application.authentication;

import cl.Barberia.domain.authentication.PasswordHash;
import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import cl.Barberia.domain.authentication.exceptions.CredencialesInvalidasException;
import cl.Barberia.domain.usermanagement.Rol;
import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.domain.usermanagement.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private LoginService loginService;

    @Test
    void authenticatesActiveUserAndResetsFailedAttempts() {
        Usuario usuario = activeUser();
        PasswordHash passwordHash = PasswordHash.fromPlainPassword("secret123");
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findPasswordHashByUsername("cliente")).thenReturn(Optional.of(passwordHash));

        boolean authenticated = loginService.autenticar("cliente", "secret123").autenticar("secret123");

        assertTrue(authenticated);
        verify(usuarioRepository).actualizarIntentosFallidos(1L, 0, null);
    }

    @Test
    void searchesByEmailWhenUsernameIsNotFound() {
        Usuario usuario = activeUser();
        when(usuarioRepository.findByUsername("cliente@example.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findPasswordHashByUsername("cliente")).thenReturn(Optional.of(PasswordHash.fromPlainPassword("secret123")));

        loginService.autenticar("cliente@example.com", "secret123");

        verify(usuarioRepository).findByEmail("cliente@example.com");
        verify(usuarioRepository).actualizarIntentosFallidos(1L, 0, null);
    }

    @Test
    void rejectsBlankLoginWithoutQueryingRepository() {
        assertThrows(CredencialesInvalidasException.class, () -> loginService.autenticar(" ", "secret123"));

        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    void rejectsInactiveUserBeforeReadingPasswordHash() {
        Usuario usuario = Usuario.builder()
            .id(1L)
            .username("cliente")
            .email("cliente@example.com")
            .nombreCompleto("Cliente Prueba")
            .rol(Rol.CLIENTE)
            .activo(false)
            .fechaCreacion(LocalDateTime.now())
            .build();
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(usuario));

        assertThrows(CredencialesInvalidasException.class, () -> loginService.autenticar("cliente", "secret123"));

        verify(usuarioRepository, never()).findPasswordHashByUsername("cliente");
    }

    @Test
    void rejectsUnknownUserAfterSearchingUsernameAndEmail() {
        when(usuarioRepository.findByUsername("missing")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("missing")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class, () -> loginService.autenticar("missing", "secret123"));
    }

    @Test
    void rejectsUserWithoutStoredPasswordHash() {
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(activeUser()));
        when(usuarioRepository.findPasswordHashByUsername("cliente")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class, () -> loginService.autenticar("cliente", "secret123"));
    }

    @Test
    void recordsFailedAttemptWhenPasswordIsInvalid() {
        Usuario usuario = activeUser();
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findPasswordHashByUsername("cliente")).thenReturn(Optional.of(PasswordHash.fromPlainPassword("secret123")));

        assertThrows(CredencialesInvalidasException.class, () -> loginService.autenticar("cliente", "incorrecta"));

        verify(usuarioRepository).incrementarIntentosFallidos(1L);
    }

    @Test
    void persistsAccountLockWhenAuthenticationIsBlocked() {
        Usuario usuario = activeUser();
        PasswordHash passwordHash = org.mockito.Mockito.mock(PasswordHash.class);
        when(passwordHash.verificar("secret123")).thenThrow(new CuentaBloqueadaException("locked"));
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findPasswordHashByUsername("cliente")).thenReturn(Optional.of(passwordHash));

        assertThrows(CuentaBloqueadaException.class, () -> loginService.autenticar("cliente", "secret123"));

        verify(usuarioRepository).actualizarIntentosFallidos(org.mockito.Mockito.eq(1L), org.mockito.Mockito.eq(5), org.mockito.ArgumentMatchers.any());
    }

    private Usuario activeUser() {
        return Usuario.builder()
            .id(1L)
            .username("cliente")
            .email("cliente@example.com")
            .nombreCompleto("Cliente Prueba")
            .rol(Rol.CLIENTE)
            .activo(true)
            .fechaCreacion(LocalDateTime.now())
            .build();
    }
}