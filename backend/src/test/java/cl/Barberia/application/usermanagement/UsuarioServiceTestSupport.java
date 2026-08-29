package cl.Barberia.application.usermanagement;

import cl.Barberia.domain.usermanagement.Rol;
import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.domain.usermanagement.UsuarioRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;

abstract class UsuarioServiceTestSupport {

    protected Usuario user(Long id, String username, String email) {
        return Usuario.builder()
            .id(id)
            .username(username)
            .email(email)
            .nombreCompleto("Cliente Prueba")
            .rol(Rol.CLIENTE)
            .activo(true)
            .build();
    }

    protected void stubAuthenticationState(UsuarioRepository usuarioRepository, String username) {
        when(usuarioRepository.findIntentosFallidosByUsername(username)).thenReturn(Optional.of(0));
        when(usuarioRepository.findBloqueadoHastaByUsername(username)).thenReturn(Optional.empty());
    }
}