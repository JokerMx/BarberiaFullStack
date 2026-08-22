package cl.Barberia.application.authentication;

import cl.Barberia.domain.authentication.PasswordHash;
import cl.Barberia.domain.authentication.UsuarioAutenticado;
import cl.Barberia.domain.authentication.exceptions.CredencialesInvalidasException;
import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.domain.usermanagement.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;  // ← IMPORT AGREGADO

@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;

    public LoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioAutenticado autenticar(String login, String plainPassword) {
        // 1. Buscar usuario
        Usuario usuario = usuarioRepository.findByUsername(login)
                .or(() -> usuarioRepository.findByEmail(login))
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario no encontrado"));
                
    // 2. Verificar si está activo
        if (!usuario.isActivo()) {
            throw new CredencialesInvalidasException("Usuario desactivado");
        }

        // 3. Obtener el hash de la contraseña
        PasswordHash passwordHash = usuarioRepository.findPasswordHashByUsername(login)
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));

        // 4. Crear agregado y autenticar
        UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado(usuario, passwordHash);

        try {
            usuarioAutenticado.autenticar(plainPassword);
            return usuarioAutenticado;
        } catch (CuentaBloqueadaException e) {
            usuarioRepository.actualizarIntentosFallidos(usuario.getId(), 5, LocalDateTime.now().plusMinutes(30));
            throw e;
        } catch (CredencialesInvalidasException e) {
            usuarioRepository.incrementarIntentosFallidos(usuario.getId());
            throw e;
        }
    }
}