package cl.Barberia.application.usermanagement;

import cl.Barberia.domain.usermanagement.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerUsuarioServiceTest extends UsuarioServiceTestSupport {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ObtenerUsuarioService obtenerUsuarioService;

    @Test
    void returnsUserByIdWithAuthenticationState() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user(1L, "cliente", "cliente@example.com")));
        when(usuarioRepository.findIntentosFallidosByUsername("cliente")).thenReturn(Optional.of(2));
        LocalDateTime lockedUntil = LocalDateTime.now().plusMinutes(30);
        when(usuarioRepository.findBloqueadoHastaByUsername("cliente")).thenReturn(Optional.of(lockedUntil));

        var response = obtenerUsuarioService.obtenerPorId(1L);

        assertEquals("cliente", response.getUsername());
        assertEquals(2, response.getIntentosFallidos());
        assertEquals(lockedUntil, response.getBloqueadoHasta());
    }

    @Test
    void returnsUserByUsername() {
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(user(1L, "cliente", "cliente@example.com")));
        stubAuthenticationState(usuarioRepository, "cliente");

        assertEquals("cliente", obtenerUsuarioService.obtenerPorUsername("cliente").getUsername());
    }

    @Test
    void returnsUserByEmail() {
        when(usuarioRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(user(1L, "cliente", "cliente@example.com")));
        stubAuthenticationState(usuarioRepository, "cliente");

        assertEquals("cliente@example.com", obtenerUsuarioService.obtenerPorEmail("cliente@example.com").getEmail());
    }

    @Test
    void rejectsMissingUser() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> obtenerUsuarioService.obtenerPorId(99L));
    }

    @Test
    void rejectsMissingUserByUsernameAndEmail() {
        when(usuarioRepository.findByUsername("missing")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> obtenerUsuarioService.obtenerPorUsername("missing"));
        assertThrows(RuntimeException.class, () -> obtenerUsuarioService.obtenerPorEmail("missing@example.com"));
    }
}