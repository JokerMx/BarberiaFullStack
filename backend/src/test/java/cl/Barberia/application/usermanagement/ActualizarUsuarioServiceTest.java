package cl.Barberia.application.usermanagement;

import cl.Barberia.application.usermanagement.DTOs.ActualizarUsuarioRequest;
import cl.Barberia.domain.usermanagement.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarUsuarioServiceTest extends UsuarioServiceTestSupport {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ActualizarUsuarioService actualizarUsuarioService;

    @Test
    void updatesEveryMutableUserField() {
        var usuario = user(1L, "cliente", "cliente@example.com");
        var usuarioActualizado = user(1L, "cliente", "nuevo@example.com");
        usuarioActualizado.desactivar();
        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest();
        request.setEmail("nuevo@example.com");
        request.setNombreCompleto("Cliente Nuevo");
        request.setRol("BARBERO");
        request.setActivo(false);
        request.setPassword("newsecret123");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario), Optional.of(usuarioActualizado));
        when(usuarioRepository.findByEmail("nuevo@example.com")).thenReturn(Optional.empty());
        stubAuthenticationState(usuarioRepository, "cliente");

        var response = actualizarUsuarioService.actualizar(1L, request);

        verify(usuarioRepository).actualizarEmail(1L, "nuevo@example.com");
        verify(usuarioRepository).actualizarNombreCompleto(1L, "Cliente Nuevo");
        verify(usuarioRepository).actualizarRol(1L, "BARBERO");
        verify(usuarioRepository).actualizarActivo(1L, false);
        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
        verify(usuarioRepository).actualizarPassword(org.mockito.ArgumentMatchers.eq(1L), hashCaptor.capture());
        assertEquals(false, response.isActivo());
        org.junit.jupiter.api.Assertions.assertNotEquals("newsecret123", hashCaptor.getValue());
    }

    @Test
    void rejectsEmailAlreadyAssignedToAnotherUser() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user(1L, "cliente", "cliente@example.com")));
        when(usuarioRepository.findByEmail("duplicate@example.com")).thenReturn(Optional.of(user(2L, "otro", "duplicate@example.com")));
        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest();
        request.setEmail("duplicate@example.com");

        assertThrows(RuntimeException.class, () -> actualizarUsuarioService.actualizar(1L, request));

        verify(usuarioRepository, never()).actualizarEmail(1L, "duplicate@example.com");
    }

    @Test
    void rejectsMissingUserBeforeApplyingChanges() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> actualizarUsuarioService.actualizar(1L, new ActualizarUsuarioRequest()));

        verify(usuarioRepository, never()).actualizarEmail(org.mockito.ArgumentMatchers.anyLong(), anyString());
    }

    @Test
    void ignoresBlankOptionalFieldsAndReturnsCurrentUser() {
        var usuario = user(1L, "cliente", "cliente@example.com");
        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest();
        request.setEmail(" "); request.setNombreCompleto(" "); request.setRol(" "); request.setPassword(" ");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario), Optional.of(usuario));
        stubAuthenticationState(usuarioRepository, "cliente");

        assertEquals("cliente", actualizarUsuarioService.actualizar(1L, request).getUsername());
        verify(usuarioRepository, never()).actualizarEmail(org.mockito.ArgumentMatchers.anyLong(), anyString());
        verify(usuarioRepository, never()).actualizarNombreCompleto(org.mockito.ArgumentMatchers.anyLong(), anyString());
        verify(usuarioRepository, never()).actualizarRol(org.mockito.ArgumentMatchers.anyLong(), anyString());
        verify(usuarioRepository, never()).actualizarPassword(org.mockito.ArgumentMatchers.anyLong(), anyString());
    }

    @Test
    void rejectsWhenUserDisappearsAfterUpdate() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user(1L, "cliente", "cliente@example.com")), Optional.empty());

        assertThrows(RuntimeException.class, () -> actualizarUsuarioService.actualizar(1L, new ActualizarUsuarioRequest()));
    }
}