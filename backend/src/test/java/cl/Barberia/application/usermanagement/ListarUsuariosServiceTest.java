package cl.Barberia.application.usermanagement;

import cl.Barberia.domain.usermanagement.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarUsuariosServiceTest extends UsuarioServiceTestSupport {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ListarUsuariosService listarUsuariosService;

    @Test
    void returnsAllUsersWithTheirAuthenticationState() {
        when(usuarioRepository.findAll()).thenReturn(List.of(
            user(1L, "cliente", "cliente@example.com"),
            user(2L, "barbero", "barbero@example.com")));
        stubAuthenticationState(usuarioRepository, "cliente");
        stubAuthenticationState(usuarioRepository, "barbero");

        var responses = listarUsuariosService.listarTodos();

        assertEquals(2, responses.size());
        assertEquals("barbero@example.com", responses.get(1).getEmail());
    }

    @Test
    void returnsOnlyUsersForRequestedRole() {
        when(usuarioRepository.findByRol("CLIENTE")).thenReturn(List.of(user(1L, "cliente", "cliente@example.com")));
        stubAuthenticationState(usuarioRepository, "cliente");

        var responses = listarUsuariosService.listarPorRol("CLIENTE");

        assertEquals(1, responses.size());
        assertEquals("CLIENTE", responses.get(0).getRol());
    }
}