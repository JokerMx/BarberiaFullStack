package cl.Barberia.application.usermanagement;

import cl.Barberia.domain.usermanagement.Rol;
import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.domain.usermanagement.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrarUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario registrar(String username, String email, String nombreCompleto,
                             String password, String rolStr) {
        // Crear usuario (aún sin ID)
        Usuario usuario = Usuario.builder()
                .id(null)
                .username(username)
                .email(email)
                .nombreCompleto(nombreCompleto)
                .rol(Rol.fromString(rolStr))
                .activo(true)
                .build();

        // Guardar en la base de datos
        return usuarioRepository.save(usuario, password);
    }
}