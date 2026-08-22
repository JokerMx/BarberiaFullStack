package cl.Barberia.application.usermanagement;

import cl.Barberia.application.usermanagement.DTOs.UsuarioResponse;
import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.domain.usermanagement.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarUsuariosService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioResponse> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
            .map(usuario -> {
                var intentos = usuarioRepository.findIntentosFallidosByUsername(usuario.getUsername());
                var bloqueado = usuarioRepository.findBloqueadoHastaByUsername(usuario.getUsername());
                return UsuarioResponse.fromDomain(usuario, intentos.orElse(0), bloqueado.orElse(null));
            })
            .collect(Collectors.toList());
    }

    public List<UsuarioResponse> listarPorRol(String rol) {
        List<Usuario> usuarios = usuarioRepository.findByRol(rol);
        return usuarios.stream()
            .map(usuario -> {
                var intentos = usuarioRepository.findIntentosFallidosByUsername(usuario.getUsername());
                var bloqueado = usuarioRepository.findBloqueadoHastaByUsername(usuario.getUsername());
                return UsuarioResponse.fromDomain(usuario, intentos.orElse(0), bloqueado.orElse(null));
            })
            .collect(Collectors.toList());
    }
}