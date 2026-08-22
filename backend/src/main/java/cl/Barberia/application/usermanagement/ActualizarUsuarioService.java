package cl.Barberia.application.usermanagement;

import cl.Barberia.application.usermanagement.DTOs.ActualizarUsuarioRequest;
import cl.Barberia.application.usermanagement.DTOs.UsuarioResponse;
import cl.Barberia.domain.authentication.PasswordHash;
import cl.Barberia.domain.usermanagement.Email;
import cl.Barberia.domain.usermanagement.NombreCompleto;
import cl.Barberia.domain.usermanagement.Rol;
import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.domain.usermanagement.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarUsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            usuarioRepository.findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new RuntimeException("El email ya está en uso por otro usuario");
                    }
                });
            new Email(request.getEmail());
            usuarioRepository.actualizarEmail(id, request.getEmail());
        }

        if (request.getNombreCompleto() != null && !request.getNombreCompleto().isBlank()) {
            new NombreCompleto(request.getNombreCompleto());
            usuarioRepository.actualizarNombreCompleto(id, request.getNombreCompleto());
        }

        if (request.getRol() != null && !request.getRol().isBlank()) {
            Rol rol = Rol.fromString(request.getRol());
            usuarioRepository.actualizarRol(id, rol.name());
        }

        if (request.getActivo() != null) {
            usuarioRepository.actualizarActivo(id, request.getActivo());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String newHash = PasswordHash.fromPlainPassword(request.getPassword()).getValue();
            usuarioRepository.actualizarPassword(id, newHash);
        }

        Usuario usuarioActualizado = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de actualizar"));

        var intentos = usuarioRepository.findIntentosFallidosByUsername(usuarioActualizado.getUsername());
        var bloqueado = usuarioRepository.findBloqueadoHastaByUsername(usuarioActualizado.getUsername());

        return UsuarioResponse.fromDomain(usuarioActualizado, intentos.orElse(0), bloqueado.orElse(null));
    }
}