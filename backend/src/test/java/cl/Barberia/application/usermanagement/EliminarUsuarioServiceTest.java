package cl.Barberia.application.usermanagement;

import cl.Barberia.domain.usermanagement.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarUsuarioServiceTest extends UsuarioServiceTestSupport {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EliminarUsuarioService eliminarUsuarioService;

    @Test
    void deletesAnExistingUser() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user(1L, "cliente", "cliente@example.com")));

        eliminarUsuarioService.eliminar(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void rejectsDeletingAMissingUser() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> eliminarUsuarioService.eliminar(1L));

        verify(usuarioRepository, never()).deleteById(1L);
    }
}