package cl.Barberia.application.usermanagement;

import cl.Barberia.application.usermanagement.DTOs.UsuarioResponse;
import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.domain.usermanagement.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerUsuarioService {

    private final UsuarioRepository usuarioRepository;

    // ===== OBTENER POR ID =====
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        var intentos = usuarioRepository.findIntentosFallidosByUsername(usuario.getUsername());
        var bloqueado = usuarioRepository.findBloqueadoHastaByUsername(usuario.getUsername());

        return UsuarioResponse.fromDomain(usuario, intentos.orElse(0), bloqueado.orElse(null));
    }

    // ===== OBTENER POR USERNAME =====
    public UsuarioResponse obtenerPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        var intentos = usuarioRepository.findIntentosFallidosByUsername(usuario.getUsername());
        var bloqueado = usuarioRepository.findBloqueadoHastaByUsername(usuario.getUsername());

        return UsuarioResponse.fromDomain(usuario, intentos.orElse(0), bloqueado.orElse(null));
    }

    // ===== OBTENER POR EMAIL (NUEVO) =====
    public UsuarioResponse obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        var intentos = usuarioRepository.findIntentosFallidosByUsername(usuario.getUsername());
        var bloqueado = usuarioRepository.findBloqueadoHastaByUsername(usuario.getUsername());

        return UsuarioResponse.fromDomain(usuario, intentos.orElse(0), bloqueado.orElse(null));
    }
}