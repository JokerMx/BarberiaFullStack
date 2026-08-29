package cl.Barberia.application.usermanagement;

import cl.Barberia.domain.usermanagement.Rol;
import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.domain.usermanagement.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RegistrarUsuarioService registrarUsuarioService;

    @Test
    void persistsAnActiveUserWithRequestedRole() {
        when(usuarioRepository.save(org.mockito.ArgumentMatchers.any(Usuario.class), org.mockito.ArgumentMatchers.eq("secret123")))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario registered = registrarUsuarioService.registrar(
            "cliente", "cliente@example.com", "Cliente Prueba", "secret123", "cliente");

        ArgumentCaptor<Usuario> userCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(userCaptor.capture(), org.mockito.ArgumentMatchers.eq("secret123"));
        assertEquals("cliente", registered.getUsername());
        assertEquals(Rol.CLIENTE, userCaptor.getValue().getRol());
        assertEquals(true, userCaptor.getValue().isActivo());
    }

    @Test
    void rejectsAnUnknownRoleBeforePersistence() {
        assertThrows(IllegalArgumentException.class, () -> registrarUsuarioService.registrar(
            "cliente", "cliente@example.com", "Cliente Prueba", "secret123", "unknown"));

        verifyNoInteractions(usuarioRepository);
    }
}